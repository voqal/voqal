package dev.voqal.provider.clients.picovoice

import com.intellij.openapi.project.Project
import com.sun.jna.Pointer
import com.sun.jna.ptr.FloatByReference
import com.sun.jna.ptr.PointerByReference
import dev.voqal.provider.VadProvider
import dev.voqal.provider.clients.picovoice.natives.CobraNative
import dev.voqal.provider.clients.picovoice.natives.PicovoiceNative
import dev.voqal.services.audioCapture
import dev.voqal.services.getVoqalLogger
import dev.voqal.utils.SharedAudioCapture
import dev.voqal.utils.SharedAudioCapture.Companion.convertBytesToShorts
import org.apache.commons.lang3.SystemUtils
import java.io.File

/**
 * Picovoice Cobra voice activity detector.
 */
class PicovoiceCobraClient(
    private val project: Project,
    picovoiceKey: String,
    var voiceDetectionThreshold: Double,
    override var voiceSilenceThreshold: Long,
    override var speechSilenceThreshold: Long,
    override var sustainedDurationMillis: Long,
    override var amnestyPeriodMillis: Long = speechSilenceThreshold * 2,
    override var testMode: Boolean = false
) : VadProvider(project) {

    private val log = project.getVoqalLogger(this::class)
    private val native: CobraNative
    private val cobra: Pointer

    init {
        NativesExtractor.extractNatives(project)
        val pvcobraLibraryPath = if (SystemUtils.IS_OS_WINDOWS) {
            File(
                NativesExtractor.workingDirectory,
                "pvcobra/lib/windows/amd64/libpv_cobra.dll".replace("/", "\\")
            )
        } else if (SystemUtils.IS_OS_LINUX) {
            File(NativesExtractor.workingDirectory, "pvcobra/lib/linux/x86_64/libpv_cobra.so")
        } else {
            val arch = NativesExtractor.getMacArchitecture()
            File(NativesExtractor.workingDirectory, "pvcobra/lib/mac/$arch/libpv_cobra.dylib")
        }

        val cobraRef = PointerByReference()
        native = CobraNative.getINSTANCE(pvcobraLibraryPath)
        log.debug("Cobra version: " + native.pv_cobra_version())
        log.debug(buildString {
            append("Cobra settings: ")
            append(voiceDetectionThreshold)
            append(",")
            append(voiceSilenceThreshold)
            append(",")
            append(speechSilenceThreshold)
            append(",")
            append(sustainedDurationMillis)
            append(",")
            append(amnestyPeriodMillis)
            append(",")
            append(testMode)
        })

        PicovoiceNative.throwIfError(
            log, native, native.pv_cobra_init(picovoiceKey, cobraRef)
        )
        cobra = cobraRef.value

        project.audioCapture.registerListener(this)
    }

    override fun isTestListener(): Boolean {
        return testMode
    }

    override fun onAudioData(data: ByteArray, detection: SharedAudioCapture.AudioDetection) {
        val pcm = convertBytesToShorts(data)
        val isVoicedRef = FloatByReference()
        native.pv_cobra_process(cobra, pcm, isVoicedRef)
        voiceProbability = isVoicedRef.value * 100.00
        if (voiceProbability >= voiceDetectionThreshold) {
            handleVoiceDetected()
        } else {
            handleVoiceNotDetected()
        }
        detection.voiceCaptured.set(isVoiceCaptured)
        detection.voiceDetected.set(isVoiceDetected)
        detection.speechDetected.set(isSpeechDetected)
    }

    override fun dispose() {
        project.audioCapture.removeListener(this)
    }
}
