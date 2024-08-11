package dev.voqal.provider.clients.picovoice

import com.google.common.io.Resources
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.project.Project
import dev.voqal.services.getVoqalLogger
import org.apache.commons.io.FileUtils
import java.io.BufferedOutputStream
import java.io.File
import java.net.URL
import java.util.jar.JarEntry
import java.util.jar.JarFile

object NativesExtractor {

    private var nativesExtracted = false
    //todo: this is nativesDirectory, make /tmp/voqal the working directory
    val workingDirectory = File(File(System.getProperty("java.io.tmpdir")), "voqal-natives")

    fun getMacArchitecture(): String {
        val arch = System.getProperty("os.arch")
        if (arch.equals("aarch64", true)) {
            return "arm64"
        }
        return arch
    }

    @Synchronized
    fun extractNatives(project: Project) {
        val log = project.getVoqalLogger(this::class)
        if (nativesExtracted) {
            log.debug("Natives already extracted")
            return
        }
        nativesExtracted = true
        try {
            val natives = Resources.getResource("natives")
            extractResource(natives, workingDirectory.absolutePath)
            return
        } catch (_: Exception) {
        }
        val tmpDir = File(File(System.getProperty("java.io.tmpdir")), "voqal-natives")
        if (!tmpDir.exists()) {
            tmpDir.mkdirs()
        }

        val startTime = System.currentTimeMillis()
        JarFile(File(PathManager.getJarPathForClass(NativesExtractor::class.java)!!)).use { jar ->
            val enumEntries = jar.entries()
            while (enumEntries.hasMoreElements()) {
                val file = enumEntries.nextElement() as JarEntry
                if (!file.name.startsWith("natives")) continue

                val f = File(tmpDir, file.name.substringAfter("natives/"))
                if (f.exists() && f.isFile) {
                    log.trace("Deleting existing file: $f")
                    f.delete()
                    if (f.exists()) {
                        log.warn("Failed to delete file: $f")
                        continue
                    }
                } else if (file.isDirectory) {
                    if (!f.exists()) {
                        log.trace("Creating directory: $f")
                        f.mkdirs()
                    }
                    continue
                }
                log.debug("Extracting file: $f")

                jar.getInputStream(file).use { inputStream ->
                    BufferedOutputStream(FileUtils.openOutputStream(f)).use { outputStream ->
                        val buffer = ByteArray(4096)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }
                    }
                }
            }
        }

        log.debug("Extracted natives in ${System.currentTimeMillis() - startTime}ms")
    }

    private fun extractResource(resourceUrl: URL, destinationDir: String) {
        val resourceFile = File(resourceUrl.toURI())
        copyFileOrDirectory(resourceFile, File(destinationDir))
    }

    private fun copyFileOrDirectory(source: File, destination: File) {
        if (source.isDirectory) {
            if (!destination.exists()) {
                destination.mkdirs()
            }
            source.listFiles()?.forEach { file ->
                copyFileOrDirectory(file, File(destination, file.name))
            }
        } else {
            if (!destination.exists()) {
                FileUtils.copyFile(source, destination)
            }
        }
    }
}
