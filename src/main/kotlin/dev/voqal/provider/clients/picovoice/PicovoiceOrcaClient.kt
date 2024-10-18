package dev.voqal.provider.clients.picovoice

import com.aallam.openai.api.audio.SpeechRequest
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import dev.voqal.provider.TtsProvider
import dev.voqal.provider.clients.picovoice.natives.OrcaNative
import dev.voqal.provider.clients.picovoice.natives.PicovoiceNative
import dev.voqal.services.VoqalConfigService
import dev.voqal.services.getVoqalLogger
import dev.voqal.services.scope
import io.ktor.utils.io.*
import kotlinx.coroutines.launch
import org.apache.commons.lang3.SystemUtils
import java.io.File

class PicovoiceOrcaClient(
    private val project: Project,
    picovoiceKey: String
) : TtsProvider {

    companion object {
        @JvmStatic
        val VOICES = arrayOf("female", "male")
    }

    private val log = project.getVoqalLogger(this::class)
    private val native: OrcaNative
    private val orca: Pointer
    private var synthesizeParams: Pointer
    private var validCharacters: Array<String>
    private var invalidCharactersRegex: Regex

    init {
        NativesExtractor.extractNatives(project)
        val pvorcaLibraryPath = if (SystemUtils.IS_OS_WINDOWS) {
            File(
                NativesExtractor.workingDirectory,
                "pvorca/lib/windows/amd64/libpv_orca.dll".replace("/", "\\")
            )
        } else if (SystemUtils.IS_OS_LINUX) {
            File(NativesExtractor.workingDirectory, "pvorca/lib/linux/x86_64/libpv_orca.so")
        } else {
            val arch = NativesExtractor.getMacArchitecture()
            File(NativesExtractor.workingDirectory, "pvorca/lib/mac/$arch/libpv_orca.dylib")
        }
        val voice = project.service<VoqalConfigService>().getConfig().textToSpeechSettings.voice
        val orcaModelPath = File(
            NativesExtractor.workingDirectory,
            "pvorca/lib/common/orca_params_$voice.pv".replace("/", File.separator)
        )

        native = OrcaNative.getINSTANCE(pvorcaLibraryPath)
        log.debug("Orca version: " + native.pv_orca_version())

        val orcaRef = PointerByReference()
        val status = native.pv_orca_init(
            picovoiceKey,
            orcaModelPath.absolutePath,
            orcaRef
        )
        PicovoiceNative.throwIfError(log, native, status)
        orca = orcaRef.value

        val synthesizeParamsRef = PointerByReference()
        PicovoiceNative.throwIfError(
            log, native, native.pv_orca_synthesize_params_init(synthesizeParamsRef)
        )
        synthesizeParams = synthesizeParamsRef.value

        val validCharactersRef = PointerByReference()
        val numCharactersRef = IntByReference()
        PicovoiceNative.throwIfError(
            log, native, native.pv_orca_valid_characters(orca, numCharactersRef, validCharactersRef)
        )

        validCharacters = validCharactersRef.value.getStringArray(0, numCharactersRef.value)
        native.pv_orca_valid_characters_delete(validCharactersRef.value)
        invalidCharactersRegex = "[^${validCharacters.joinToString("")}]".toRegex()
    }

    override suspend fun speech(request: SpeechRequest): TtsProvider.RawAudio {
        val sampleRateRef = IntByReference()
        val pcmRef = PointerByReference()
        val numAlignmentsRef = IntByReference()
        val alignmentsRef = PointerByReference()
        val status = native.pv_orca_synthesize(
            orca,
            request.input,
            synthesizeParams,
            sampleRateRef,
            pcmRef,
            numAlignmentsRef,
            alignmentsRef
        )
        PicovoiceNative.throwIfError(log, native, status)

        val pcm = pcmRef.value
        val numSamples = sampleRateRef.value
        val chunkSize = 1024
        var offset = 0
        val channel = ByteChannel()

        project.scope.launch {
            try {
                while (offset < numSamples * 2) {
                    val bytesToWrite = minOf(chunkSize, (numSamples * 2) - offset)
                    val pcmChunk = pcm.getByteArray(offset.toLong(), bytesToWrite)
                    offset += bytesToWrite
                    channel.writeFully(pcmChunk, 0, pcmChunk.size)
                }
            } finally {
                native.pv_orca_pcm_delete(pcm)
                native.pv_orca_word_alignments_delete(numAlignmentsRef.value, alignmentsRef.value)
                channel.close()
            }
        }

        return TtsProvider.RawAudio(
            audio = channel,
            sampleRate = 22050f,
            bitsPerSample = 16,
            channels = 1
        )
    }

    override fun isWavOutput() = true
    override fun isRawOutput() = true

    override fun dispose() {
        native.pv_orca_delete(orca)
        native.pv_orca_synthesize_params_delete(synthesizeParams)
    }
}
