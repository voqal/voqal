package dev.voqal.services

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.file.fileSource
import com.aallam.openai.api.file.fileUpload
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.vectorstore.VectorStoreRequest
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.lang.Language
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.impl.DocumentMarkupModel
import com.intellij.openapi.editor.impl.RangeMarkerImpl
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.roots.ContentIterator
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.readText
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.file.PsiDirectoryFactory
import com.intellij.util.containers.CollectionFactory
import dev.voqal.assistant.tool.code.CreateClassTool.Companion.getSupportedFileExtensions
import dev.voqal.config.settings.LanguageModelSettings
import io.vertx.core.Promise
import kotlinx.coroutines.future.await
import okio.Buffer
import org.apache.commons.text.similarity.LevenshteinDistance
import org.joor.Reflect

/**
 * Handle finding code/files/etc. in the current project.
 */
@Service(Service.Level.PROJECT)
class VoqalSearchService(private val project: Project) {

    private val log = project.getVoqalLogger(this::class)
    private val levenshteinDistance = LevenshteinDistance()

    fun getActiveProblems(editor: Editor): List<HighlightInfo> {
        return DocumentMarkupModel.forDocument(editor.document, project, false)
            .allHighlighters.toList()
            .filterIsInstance<RangeMarkerImpl>()
            .mapNotNull {
                Reflect.on(it).field("myErrorStripeTooltip").get() as? HighlightInfo
            }
            .filter { it.severity == HighlightSeverity.ERROR }
    }

    @OptIn(BetaOpenAI::class)
    suspend fun syncLocalFilesToVectorStore(lmSettings: LanguageModelSettings) {
        val filesToUpload = mutableListOf<VirtualFile>()
        ProjectFileIndex.getInstance(project).iterateContent(object : ContentIterator {
            override fun processFile(fileOrDir: VirtualFile): Boolean {
                if (fileOrDir.isDirectory) {
                    return true
                }
                val psiFile = ReadAction.compute(ThrowableComputable {
                    PsiManager.getInstance(project).findFile(fileOrDir)
                }) ?: return true
                if (psiFile.isFile()) {
                    filesToUpload.add(fileOrDir)
                }
                return true
            }
        })
        log.info("Files to upload: ${filesToUpload.size}")

        val openAI = OpenAI(
            token = lmSettings.providerKey,
            logging = LoggingConfig(LogLevel.None)
        )
        val uploadedFileIds = mutableListOf<FileId>()
        filesToUpload.forEach {
            val fileText = ReadAction.compute(ThrowableComputable { it.readText() })
            val fileTextWithLineNumbers = fileText.lines()
                .mapIndexed { index, line -> "${index + 1}|$line" }
                .joinToString("\n")

            val fileRequest = fileUpload {
                file = fileSource {
                    name = it.name //WithoutExtension + ".txt"
                    source = Buffer().writeUtf8(fileTextWithLineNumbers)
                }
                purpose = Purpose("assistants")
            }
            val createdFile = openAI.file(fileRequest)
            uploadedFileIds.add(createdFile.id)
        }

        val vectorStore = openAI.createVectorStore(
            request = VectorStoreRequest(
                name = project.name,
                fileIds = uploadedFileIds
            )
        )
        log.info("Vector store created: ${vectorStore.id}")

        val promptSettings = project.service<VoqalConfigService>().getPromptSettings("Search Mode")
        project.service<VoqalConfigService>().updateConfig(promptSettings.copy(vectorStoreId = vectorStore.id.id))
    }

    fun getSourceRoots(): List<VirtualFile> {
        val moduleManager = ModuleManager.getInstance(project)
        return moduleManager.modules.flatMap { module ->
            val rootManager = ModuleRootManager.getInstance(module)
            rootManager.sourceRoots.toList()
        }
    }

    fun getMainSourceRoot(lang: Language): VirtualFile? {
        val sourceRoots = getSourceRoots()
        val mainSourceRoot = sourceRoots.firstOrNull {
            it.path.contains("src/main/" + lang.id.lowercase())
        }
        return mainSourceRoot
    }

    fun getProjectRoot(): VirtualFile {
        return project.guessProjectDir() ?: project.baseDir!!
    }

    fun findExactMatches(name: String): List<Pair<VirtualFile, String>> {
        val name = name.lowercase()
        //consider style diffs (i.e CamelCase, snake_case, etc)
        val nameSet = mutableSetOf<String>()
        nameSet.add(name) //original
        nameSet.add(name.replace(" ", "_"))
        nameSet.add(name.replace(" ", ""))

        //see if we can find a file that matches this name
        val exactMatches = mutableListOf<Pair<VirtualFile, String>>()
        ProjectFileIndex.getInstance(project).iterateContent { file ->
            if (!file.isDirectory && file.nameWithoutExtension.lowercase() in nameSet) {
                exactMatches.add(Pair(file, file.nameWithoutExtension))
            } else if (file.path.lowercase().endsWith(name)) {
                //check for exact match from root
                exactMatches.add(Pair(file, file.name))
            }
            true
        }
        return exactMatches
    }

    suspend fun findFile(name: String, exact: Boolean = true): VirtualFile? {
        return findFiles(name, exact).firstOrNull()
    }

    private suspend fun findFiles(name: String, exact: Boolean = false): List<VirtualFile> {
        return findFiles(getAllFiles(), name, exact)
    }

    fun findFiles(searchFiles: Collection<VirtualFile>, name: String, exact: Boolean = false): List<VirtualFile> {
        //check for exact match
        val matchFiles = searchFiles.filter { isNameMatch(name, it) }
        if (matchFiles.isNotEmpty()) {
            if (matchFiles.size == 1 || !exact) {
                return matchFiles
            }
        }

        if (exact) {
            return emptyList()
        } else {
            //return by closest match
            val filesByDistance = searchFiles.sortedBy {
                levenshteinDistance.apply(name.lowercase(), it.name.substringBefore(".").lowercase()).toDouble()
            }
            return filesByDistance
        }
    }

    private fun isNameMatch(name: String, file: VirtualFile): Boolean {
        //consider style diffs (i.e CamelCase, snake_case, etc)
        val nameSet = mutableSetOf<String>()
        nameSet.add(name) //original
        nameSet.add(name.replace(".", "/"))
        nameSet.add(name.replace(" ", "_"))
        nameSet.add(name.replace(" ", ""))

        //check for exact match
        val exactMatch = nameSet.any {
            file.name.equals(it, ignoreCase = true) || file.nameWithoutExtension.equals(it, ignoreCase = true)
        }
        if (exactMatch) {
            return true
        }

        //check for exact match from root
        val exactMatchFromRoot = nameSet.any {
            file.path.endsWith(it, ignoreCase = true) ||
                    (file.extension?.let { ext ->
                        file.path.substringBeforeLast(".$ext").endsWith(it, ignoreCase = true)
                    } == true)
        }
        if (exactMatchFromRoot) {
            return true
        }

        return false
    }

    fun getFlattenedPackages(): List<String> {
        val projectFileIndex = ProjectFileIndex.getInstance(project)
        val packages = CollectionFactory.createSmallMemoryFootprintSet<String>()
        projectFileIndex.iterateContent { file ->
            if (file.isDirectory) {
                val packageName = projectFileIndex.getPackageNameByDirectory(file)
                if (!packageName.isNullOrBlank()) {
                    packages.add(packageName)
                }
            }
            true
        }
        return packages.toList()
    }

    fun getPackageByName(root: VirtualFile, name: String): PsiDirectory? {
        val vf = root.findFileByRelativePath(name.replace(".", "/"))
        return vf?.let { PsiDirectoryFactory.getInstance(project).createDirectory(it) }
    }

    suspend fun getAllFiles(): Collection<VirtualFile> {
        return getAllFiles(emptyList())
    }

    //todo: cache, file watch
    private suspend fun getAllFiles(
        ext: List<String> = getSupportedFileExtensions()
    ): Collection<VirtualFile> {
        val promise = Promise.promise<Collection<VirtualFile>>()
        val task = object : Task.Backgroundable(project, "Fetching files", false) {
            override fun run(indicator: ProgressIndicator) {
                promise.complete(ReadAction.compute<Set<VirtualFile>, Throwable> {
                    val files = CollectionFactory.createSmallMemoryFootprintSet<VirtualFile>()
                    val projectFileIndex = ProjectFileIndex.getInstance(project)
                    projectFileIndex.iterateContent { file ->
                        val inSourceContent = projectFileIndex.isInSourceContent(file)
                                || (project.basePath?.let { file.path.startsWith(it) } == true)
                        if (!file.isDirectory && (ext.isEmpty() || ext.contains(file.extension?.lowercase())) && inSourceContent) {
                            if (!file.path.endsWith(".class")) { //todo: more robust
                                files.add(file)
                            }
                        }
                        true
                    }
                    files
                })
            }
        }
        ProgressManager.getInstance().run(task)
        return promise.future().toCompletionStage().await()
    }

    fun getLanguageForSourceRoot(sourceRoot: VirtualFile): Language? {
        //try to extract language from path
        if (sourceRoot.path.contains("src/main/")) {
            val lang = sourceRoot.path.split("/").last()
            return Language.findLanguageByID(lang)
        }
        return null
    }
}
