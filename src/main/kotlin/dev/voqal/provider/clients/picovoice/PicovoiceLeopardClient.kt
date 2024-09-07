package dev.voqal.provider.clients.picovoice

import com.intellij.openapi.project.Project
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import dev.voqal.provider.SttProvider
import dev.voqal.provider.clients.picovoice.natives.LeopardNative
import dev.voqal.provider.clients.picovoice.natives.PicovoiceNative
import dev.voqal.services.getVoqalLogger
import org.apache.commons.lang3.SystemUtils
import java.io.File

class PicovoiceLeopardClient(
    project: Project,
    picovoiceKey: String
) : SttProvider {

    private val log = project.getVoqalLogger(this::class)
    private val native: LeopardNative
    private var leopard: Pointer

    init {
        NativesExtractor.extractNatives(project)
        val pvleopardLibraryPath = if (SystemUtils.IS_OS_WINDOWS) {
            File(
                NativesExtractor.workingDirectory,
                "pvleopard/lib/windows/amd64/libpv_leopard.dll".replace("/", "\\")
            )
        } else if (SystemUtils.IS_OS_LINUX) {
            File(NativesExtractor.workingDirectory, "pvleopard/lib/linux/x86_64/libpv_leopard.so")
        } else {
            val arch = NativesExtractor.getMacArchitecture()
            File(NativesExtractor.workingDirectory, "pvleopard/lib/mac/$arch/libpv_leopard.dylib")
        }
        val leopardModelPath = File(
            NativesExtractor.workingDirectory,
            "pvleopard/lib/common/leopard_params.pv".replace("/", File.separator)
        )
        native = LeopardNative.getINSTANCE(pvleopardLibraryPath)
        log.debug("Leopard version: " + native.pv_leopard_version())

        val leopardRef = PointerByReference()
        val status = native.pv_leopard_init(
            picovoiceKey,
            leopardModelPath.absolutePath,
            false, //todo: get from config
            false, //todo: get from config
            leopardRef
        )
        PicovoiceNative.throwIfError(log, native, status)
        leopard = leopardRef.value
    }

    override suspend fun transcribe(speechFile: File, modelName: String): String {
        val transcriptRef = PointerByReference()
        val numWordsRef = IntByReference()
        val wordsRef = PointerByReference()

        val status = native.pv_leopard_process_file(
            leopard,
            speechFile.absolutePath,
            transcriptRef,
            numWordsRef,
            wordsRef
        )
        PicovoiceNative.throwIfError(log, native, status)

        val transcript = transcriptRef.value.getString(0)
        native.pv_leopard_transcript_delete(transcriptRef.value)
        if (wordsRef.value != null) {
            native.pv_leopard_words_delete(wordsRef.value)
        }
        return transcript
    }

    override fun dispose() {
        native.pv_leopard_delete(leopard)
    }
}
