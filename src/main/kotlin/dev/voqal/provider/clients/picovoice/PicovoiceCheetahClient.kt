package dev.voqal.provider.clients.picovoice

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.focus.SpokenWord
import dev.voqal.provider.SttProvider
import dev.voqal.provider.clients.picovoice.natives.CheetahNative
import dev.voqal.provider.clients.picovoice.natives.PicovoiceNative
import dev.voqal.services.*
import dev.voqal.status.VoqalStatus
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

    private val native: CheetahNative
    private val cheetah: Pointer
    private val audioQueue = LinkedBlockingQueue<ByteArray>()
    private var editMode = false
    private val currentTranscription = StringBuilder()
    private var wordIndex = 0

    init {
        require(currentThread().isDaemon)
        val log = project.getVoqalLogger(this::class)

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
        val log = project.getVoqalLogger(this::class)
        project.service<VoqalStatusService>().onStatusChange(this) { status, _ ->
            editMode = status == VoqalStatus.EDITING
//            if (editMode) {
//                audioQueue.clear()
//                val ignoreTranscriptRef = PointerByReference()
//                PicovoiceNative.throwIfError(
//                    log, native, native.pv_cheetah_flush(cheetah, ignoreTranscriptRef)
//                )
//                val ignoreTranscript = ignoreTranscriptRef.value.getString(0)
//                if (ignoreTranscript.isNotEmpty()) {
//                    log.debug("Ignoring transcript: $ignoreTranscript")
//                }
//            }
        }

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

                    if (editMode) {
                        project.scope.launch {
                            val aiProvider = project.service<VoqalConfigService>().getAiProvider()
                            val speechId = aiProvider.asVadProvider().speechId
                            val spokenTranscript = SpokenTranscript(
                                currentTranscription.toString(),
                                speechId,
                                words = currentTranscription.toString().split(" ").filter { it.isNotBlank() }.map {
                                    SpokenWord(
                                        it,
                                        wordIndex++.toDouble(),
                                        wordIndex++.toDouble(),
                                        -1.0
                                    )
                                }
                            )
                            log.info("Transcript: " + spokenTranscript)
//                            project.service<VoqalTranscribeService>().addTranscription(spokenTranscript)
                        }
                    }
                }
                native.pv_cheetah_transcript_delete(partialTranscriptRef.value)

                if (isEndpointRef.value == 1) {
                    val finalTranscriptRef = PointerByReference()
                    PicovoiceNative.throwIfError(
                        log, native, native.pv_cheetah_flush(cheetah, finalTranscriptRef)
                    )

                    val finalTranscript = finalTranscriptRef.value.getString(0)
                    if (finalTranscript != null) {
                        log.debug("Final transcript: $finalTranscript")
                        currentTranscription.append(finalTranscript)

                        val transcript = currentTranscription.toString()
                        currentTranscription.clear()

                        log.info("Transcript: $transcript")
                        if (transcript.isNotBlank()) {
                            project.scope.launch {
                                val aiProvider = project.service<VoqalConfigService>().getAiProvider()
                                val speechId = aiProvider.asVadProvider().speechId
//                                project.service<VoqalTranscribeService>()
//                                    .addTranscription(SpokenTranscript(transcript, speechId, isFinal = true))
                                log.info("Transcript: ${SpokenTranscript(transcript, speechId, isFinal = true)}")
                            }
                        }
                    }
                    native.pv_cheetah_transcript_delete(finalTranscriptRef.value)
                }
            }
        } catch (e: Exception) {
            log.error("Error processing audio: ${e.message}", e)
        } finally {
            native.pv_cheetah_delete(cheetah)
        }
    }

    override fun onAudioData(data: ByteArray, detection: SharedAudioCapture.AudioDetection) {
        audioQueue.put(data)
    }

    override fun dispose() {
        project.audioCapture.removeListener(this)
        interrupt()
    }

    override suspend fun transcribe(speechFile: File, modelName: String): String {
        throw IllegalStateException("This provider does not support file transcriptions")
    }
}
