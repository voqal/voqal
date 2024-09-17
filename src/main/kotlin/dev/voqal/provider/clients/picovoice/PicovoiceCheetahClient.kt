package dev.voqal.provider.clients.picovoice

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.provider.SttProvider
import dev.voqal.provider.clients.picovoice.natives.CheetahNative
import dev.voqal.provider.clients.picovoice.natives.PicovoiceNative
import dev.voqal.services.*
import dev.voqal.utils.SharedAudioCapture
import dev.voqal.utils.SharedAudioCapture.Companion.convertBytesToShorts
import kotlinx.coroutines.launch
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.util.concurrent.LinkedBlockingQueue

class PicovoiceCheetahClient(
    private val project: Project,
    picovoiceKey: String
) : SttProvider, Thread(), SharedAudioCapture.AudioDataListener {

    private val log = project.getVoqalLogger(this::class)
    private val native: CheetahNative
    private val cheetah: Pointer
    private val audioQueue = LinkedBlockingQueue<ByteArray>()
    private val currentTranscription = StringBuilder()
    private var capturing = false

    init {
        require(currentThread().isDaemon)

        NativesExtractor.extractNatives(project)
        val pvcheetahLibraryPath = if (SystemUtils.IS_OS_WINDOWS) {
            File(
                NativesExtractor.workingDirectory,
                "pvcheetah/lib/windows/amd64/libpv_cheetah.dll".replace("/", "\\")
            )
        } else if (SystemUtils.IS_OS_LINUX) {
            File(NativesExtractor.workingDirectory, "pvcheetah/lib/linux/x86_64/libpv_cheetah.so")
        } else {
            val arch = NativesExtractor.getMacArchitecture()
            File(NativesExtractor.workingDirectory, "pvcheetah/lib/mac/$arch/libpv_cheetah.dylib")
        }
        val cheetahModelPath = File(
            NativesExtractor.workingDirectory,
            "pvcheetah/lib/common/cheetah_params.pv".replace("/", File.separator)
        )
        native = CheetahNative.getINSTANCE(pvcheetahLibraryPath)
        log.debug("Cheetah version: " + native.pv_cheetah_version())

        val sampleRate = native.pv_sample_rate()
        val frameLength = native.pv_cheetah_frame_length()
        log.debug("Sample rate: $sampleRate - Frame length: $frameLength")

        val cheetahRef = PointerByReference()
        val status = native.pv_cheetah_init(
            picovoiceKey,
            cheetahModelPath.absolutePath,
            1.0f, //todo: get from config
            true,
            cheetahRef
        )
        PicovoiceNative.throwIfError(log, native, status)
        cheetah = cheetahRef.value

        project.audioCapture.registerListener(this)
        start()
    }

    override fun run() {
        try {
            log.info("Waiting for audio data...")
            while (true) {
                val buffer = try {
                    audioQueue.take()
                } catch (_: InterruptedException) {
                    break
                }
                val pcm = convertBytesToShorts(buffer)

                val partialTranscriptRef = PointerByReference()
                val isEndpointRef = IntByReference()
                PicovoiceNative.throwIfError(
                    log, native, native.pv_cheetah_process(cheetah, pcm, partialTranscriptRef, isEndpointRef)
                )

                val partialTranscript = partialTranscriptRef.value.getString(0)
                if (partialTranscript.isNotEmpty()) {
                    log.debug("Partial transcript: $partialTranscript")
                    currentTranscription.append(partialTranscript)
                    dispatchPartialTranscript()
                }
                native.pv_cheetah_transcript_delete(partialTranscriptRef.value)

                if (isEndpointRef.value == 1) {
                    val finalTranscriptRef = PointerByReference()
                    PicovoiceNative.throwIfError(
                        log, native, native.pv_cheetah_flush(cheetah, finalTranscriptRef)
                    )

                    val finalTranscript = finalTranscriptRef.value.getString(0)
                    if (finalTranscript.isNotEmpty()) {
                        log.debug("Final transcript: $finalTranscript")
                        currentTranscription.append(finalTranscript)
                        dispatchFinalTranscript()
                    }
                    native.pv_cheetah_transcript_delete(finalTranscriptRef.value)
                } else if (buffer === SharedAudioCapture.EMPTY_BUFFER) {
                    val flushedTranscriptRef = PointerByReference()
                    PicovoiceNative.throwIfError(
                        log, native, native.pv_cheetah_flush(cheetah, flushedTranscriptRef)
                    )

                    val flushedTranscript = flushedTranscriptRef.value.getString(0)
                    if (flushedTranscript.isNotEmpty()) {
                        log.debug("Flushed transcript: $flushedTranscript")
                        currentTranscription.append(flushedTranscript)
                        dispatchFinalTranscript()
                    }
                }
            }
        } catch (e: Exception) {
            log.error("Error processing audio: ${e.message}", e)
        } finally {
            native.pv_cheetah_delete(cheetah)
        }
    }

    private fun dispatchPartialTranscript() {
        val partialTranscript = currentTranscription.toString()
        project.scope.launch {
            val aiProvider = project.service<VoqalConfigService>().getAiProvider()
            val speechId = aiProvider.asVadProvider().speechId
            val spokenTranscript = SpokenTranscript(partialTranscript, speechId, isFinal = true)
            project.service<VoqalDirectiveService>().handlePartialTranscription(spokenTranscript)
        }
    }

    private fun dispatchFinalTranscript() {
        val fullTranscript = currentTranscription.toString()
        currentTranscription.clear()

        log.info("Transcript: $fullTranscript")
        project.scope.launch {
            val aiProvider = project.service<VoqalConfigService>().getAiProvider()
            val speechId = aiProvider.asVadProvider().speechId
            val spokenTranscript = SpokenTranscript(fullTranscript, speechId, isFinal = true)
            project.service<VoqalDirectiveService>().handleTranscription(spokenTranscript)
        }
    }

    override fun onAudioData(data: ByteArray, detection: SharedAudioCapture.AudioDetection) {
        if (detection.speechDetected.get()) {
            detection.framesBeforeVoiceDetected.forEach {
                audioQueue.put(it.data)
            }
            audioQueue.put(data)
            capturing = true
        } else if (capturing && !detection.speechDetected.get()) {
            audioQueue.put(SharedAudioCapture.EMPTY_BUFFER)
            capturing = false
        }
    }

    override fun dispose() {
        project.audioCapture.removeListener(this)
        interrupt()
    }

    override suspend fun transcribe(speechFile: File, modelName: String): String {
        throw IllegalStateException("This provider does not support file transcriptions")
    }

    override fun isLiveDataListener() = true
}
