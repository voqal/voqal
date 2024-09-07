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
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.lang.Language
import com.intellij.lang.LanguageStructureViewBuilder
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
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.readText
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.impl.file.PsiDirectoryFactory
import com.intellij.util.containers.CollectionFactory
import com.knuddels.jtokkit.api.Encoding
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
                if (isFile(psiFile)) {
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
        //consider style diffs (i.e camelCase, snake_case, etc)
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
        val exactMatch = searchFiles.filter { it.name.equals(name, ignoreCase = true) }
        if (exactMatch.isNotEmpty()) {
            if (exactMatch.size == 1 || !exact) {
                return exactMatch
            }
        }

        //check for exact match from root
        val exactMatchFromRoot = searchFiles.filter { it.path.endsWith(name, ignoreCase = true) }
        if (exactMatchFromRoot.isNotEmpty()) {
            return exactMatchFromRoot
        }

        //check for exact match from root (removing extension)
        val exactMatchFromRootNoExt = searchFiles.filter {
            if (it.extension != null) {
                it.path.substringBeforeLast("." + it.extension!!).endsWith(name, ignoreCase = true) ||
                        it.path.substringBeforeLast("." + it.extension!!)
                            .endsWith(name.replace(".", "/"), ignoreCase = true)
            } else false
        }
        if (exactMatchFromRootNoExt.isNotEmpty()) {
            return exactMatchFromRootNoExt
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

    /**
     * Returns markdown representation of the tree.
     * ```
     * ├── src
     * │   ├── main
     * │   │   ├── kotlin
     * ```
     */
    fun getProjectStructureAsMarkdownTree(encoding: Encoding? = null, tokenLimit: Int = -1): String {
        val sb = StringBuilder()
        val projectDir = getProjectRoot()
        sb.append(recursiveMarkdownTree(projectDir.children.toList()))

        //remove from bottom and top (till under tokenLimit then add ... to top and bottom to indicate cropped)
        val tokens = encoding?.countTokens(sb.toString()) ?: -1
        if (tokens > tokenLimit) {
            val lines = sb.toString().lines().toMutableList()
            //take from bottom and top equally till under tokenLimit
            while (encoding!!.countTokens(sb.toString()) > tokenLimit && lines.size > 2) {
                lines.removeAt(0)
                lines.removeAt(lines.size - 1)
            }
            return (listOf("...") + lines + listOf("...")).joinToString("\n")
        }
        return sb.toString()
    }

    private fun recursiveMarkdownTree(children: Collection<VirtualFile>, level: Int = 0): String {
        val ignoreNames = setOf(
            ".git", ".idea", ".gitignore", "build", "out", "target", "node_modules",
            "__pycache__", "venv", ".gradle", "obj"
        )
        val ignoreExtensions = setOf("class")

        val sb = StringBuilder()
        val projectFileIndex = ProjectFileIndex.getInstance(project)
        for (child in children.sortedBy { it.name }) {
            val isInContent = ReadAction.compute(ThrowableComputable {
                projectFileIndex.isInContent(child)
            })
            if (!isInContent || child.name in ignoreNames || child.extension in ignoreExtensions) {
                continue
            } else if (child.name == "obj" && child.children.any { it.name == "Debug" || it.name == "Release" }) {
                continue //.NET build dir
            }

            repeat(level) {
                sb.append("│   ")
            }
            sb.append("├── ")
            sb.append(child.name)
            sb.append("\n")
            if (child.isDirectory) {
                sb.append(recursiveMarkdownTree(child.children.toList(), level + 1))
            }
        }
        return sb.toString()
    }

    fun getLanguageForSourceRoot(sourceRoot: VirtualFile): Language? {
        //try to extract language from path
        if (sourceRoot.path.contains("src/main/")) {
            val lang = sourceRoot.path.split("/").last()
            return Language.findLanguageByID(lang)
        }
        return null
    }

    fun getProjectCodeStructure(encoding: Encoding? = null, tokenLimit: Int = -1): String {
        val sb = StringBuilder()
        val projectDir = getProjectRoot()
        sb.append(recursiveCodeStructure(projectDir.children.toList()))

        //remove final "---" if present
        if (sb.endsWith("---\n")) {
            sb.delete(sb.length - 4, sb.length)
            sb.append("\n")
        }

        //remove from bottom and top (till under tokenLimit then add ... to top and bottom to indicate cropped)
        val tokens = encoding?.countTokens(sb.toString()) ?: -1
        if (tokens > tokenLimit) {
            val lines = sb.toString().lines().toMutableList()
            //take from bottom and top equally till under tokenLimit
            while (encoding!!.countTokens(sb.toString()) > tokenLimit && lines.size > 2) {
                lines.removeAt(0)
                lines.removeAt(lines.size - 1)
            }
            return (listOf("...") + lines + listOf("...")).joinToString("\n")
        }
        return sb.toString()
    }

    private fun recursiveCodeStructure(children: Collection<VirtualFile>, level: Int = 0): String {
        val ignoreNames = setOf(
            ".git", ".idea", ".gitignore", "build", "out", "target", "node_modules",
            "__pycache__", "venv", ".gradle", "obj"
        )
        val ignoreExtensions = setOf("class")

        val sb = StringBuilder()
        val projectFileIndex = ProjectFileIndex.getInstance(project)
        for (child in children.sortedBy { it.name }) {
            val isInContent = ReadAction.compute(ThrowableComputable {
                projectFileIndex.isInContent(child)
            })
            if (!isInContent || child.name in ignoreNames || child.extension in ignoreExtensions) {
                continue
            } else if (child.name == "obj" && child.children.any { it.name == "Debug" || it.name == "Release" }) {
                continue //.NET build dir
            }

            if (child.isDirectory) {
                sb.append(recursiveCodeStructure(child.children.toList(), level + 1))
            } else {
//                val code = summarizeCode(child)
//                if (code != null) {
//                    sb.append(code)
//                    sb.append("---\n")
//                }

                val fileText = ReadAction.compute(ThrowableComputable {
                    child.readText()
                })
                sb.append(fileText).append("---\n")
            }
        }
        return sb.toString()
    }

    /**
     * Summarizes file by returning a string of structure signatures.
     * Example:
     * ```kotlin
     * MyClass.kt
     *   class MyClass
     *     fun add(a: Int, b: Int): Int
     *       ...
     *     fun subtract(a: Int, b: Int): Int
     *       ...
     * ```
     */
    private fun summarizeCode(virtualFile: VirtualFile): String? {
        val psiFile = ReadAction.compute(ThrowableComputable { PsiManager.getInstance(project).findFile(virtualFile) })
        if (psiFile == null) {
            log.warn("Failed to find PSI file for virtual file: $virtualFile")
            return null
        }

        //ignore non-code files
        if (!isFile(psiFile)) {
            log.debug("Ignoring non-code file: $virtualFile")
            return null
        }

        val structure = ReadAction.compute(ThrowableComputable {
            LanguageStructureViewBuilder.INSTANCE.getStructureViewBuilder(psiFile)
        })
        if (structure == null) {
            log.warn("Failed to get structure view builder for file: $virtualFile")
            return null
        }

        val sb = StringBuilder()
        val structureView = structure.createStructureView(null, project)
        ReadAction.compute(ThrowableComputable {
            recursiveSummarizeCode((structureView.treeModel).root, sb)
        })
        Disposer.dispose(structureView)
        return sb.toString()
    }

    private fun recursiveSummarizeCode(element: StructureViewTreeElement, sb: StringBuilder, indent: String = "") {
        val psiElement = element.value as PsiElement
        if (isFile(psiElement)) {
            sb.append((psiElement as PsiNamedElement).name).append("\n")
        } else if (isClass(psiElement)) {
            sb.append(indent)

            val presentationIcon = element.presentation.getIcon(false)
            if (presentationIcon.toString().contains("public.svg")) {
                sb.append("public ")
            } else if (presentationIcon.toString().contains("private.svg")) {
                sb.append("private ")
            }
            //todo: isStatic

            sb.append("class ").append(element.presentation.presentableText).append("\n")
            //sb.append(indent).append(indent).append("...").append("\n")
        } else if (isFunction(psiElement)) {
            sb.append(indent)

            val presentationIcon = element.presentation.getIcon(false)
            if (presentationIcon.toString().contains("public.svg")) {
                sb.append("public ")
            } else if (presentationIcon.toString().contains("private.svg")) {
                sb.append("private ")
            } else if (presentationIcon.toString().contains("protected.svg")) {
                sb.append("protected ")
            } else if (presentationIcon.toString().contains("plocal.svg")) {
                sb.append("package-private ")
            }
            //todo: isStatic

            if (psiElement.isJvm()) {
                sb.append("function ")
            } else if (psiElement.isPython()) {
                sb.append("def ")
            } else if (psiElement.isGo()) {
                sb.append("func ")
            }
            sb.append(element.presentation.presentableText).append("\n")
                .append(indent).append(indent).append("...").append("\n")
        } else {
            sb.append(indent)

            val presentationIcon = element.presentation.getIcon(false)
            if (presentationIcon.toString().contains("public.svg")) {
                sb.append("public ")
            } else if (presentationIcon.toString().contains("private.svg")) {
                sb.append("private ")
            } else if (presentationIcon.toString().contains("protected.svg")) {
                sb.append("protected ")
            } else if (presentationIcon.toString().contains("plocal.svg")) {
                sb.append("package-private ")
            }
            sb.append(element.presentation.presentableText).append("\n")
        }

        for (child in element.children) {
            recursiveSummarizeCode(child as StructureViewTreeElement, sb, "$indent  ")
        }
    }

    fun isFunction(psiElement: PsiElement): Boolean {
        return psiElement::class.java.simpleName.startsWith("KtNamedFunction")
                || psiElement::class.java.simpleName.startsWith("PsiMethodImpl")
                || psiElement::class.java.simpleName.startsWith("PyFunction")
                || psiElement::class.java.simpleName.startsWith("GoFunctionDeclaration")
                || psiElement::class.java.simpleName.startsWith("GoMethodDeclaration")
                || psiElement::class.java.simpleName.startsWith("JSFunctionImpl")
    }

    fun isCodeBlock(psiElement: PsiElement): Boolean {
        return psiElement::class.java.simpleName.startsWith("KtBlockExpression")
                || psiElement::class.java.simpleName.startsWith("PsiCodeBlockImpl")
                || psiElement::class.java.simpleName.startsWith("PyStatementList")
                || psiElement::class.java.simpleName.startsWith("GoBlockImpl")
                || psiElement::class.java.simpleName.startsWith("JSBlockStatementImpl")
    }

    fun isField(psiElement: PsiElement): Boolean {
        return psiElement::class.java.simpleName.startsWith("KtProperty")
                || psiElement::class.java.simpleName.startsWith("PsiFieldImpl")
    }

    fun isClass(psiElement: PsiElement): Boolean {
        return psiElement::class.java.simpleName.startsWith("KtClass")
                || psiElement::class.java.simpleName.startsWith("PsiClass")
                || psiElement::class.java.simpleName.startsWith("PyClass")
                || psiElement::class.java.simpleName.startsWith("GoTypeSpecImpl")
                || psiElement::class.java.simpleName.startsWith("ES6ClassImpl")
    }

    fun isFile(psiElement: PsiElement): Boolean {
        return psiElement::class.java.simpleName.startsWith("KtFile")
                || psiElement::class.java.simpleName.startsWith("PsiJavaFile")
                || psiElement::class.java.simpleName.startsWith("PyFile")
                || psiElement::class.java.simpleName.startsWith("GoFile")
                || psiElement::class.java.simpleName.startsWith("JSFileImpl")
    }

    fun isIdentifier(psiElement: PsiElement): Boolean {
        return psiElement.toString().contains("PsiIdentifier")
                || psiElement.toString().contains("IDENTIFIER")
    }

    fun PsiElement.isJvm(): Boolean {
        return this.language.id.lowercase() == "java" || this.language.id.lowercase() == "kotlin"
    }

    fun PsiElement.isPython(): Boolean {
        return this.language.id.lowercase() == "python"
    }

    fun PsiElement.isGo(): Boolean {
        return this.language.id.lowercase() == "go"
    }
}
