package dev.voqal.provider.clients.voqal

import com.intellij.openapi.project.Project
import dev.voqal.provider.VadProvider
import dev.voqal.services.audioCapture
import dev.voqal.services.getVoqalLogger
import dev.voqal.utils.SharedAudioCapture
import dev.voqal.utils.SharedAudioCapture.Companion.convertBytesToShorts
import io.github.givimad.libfvadjni.VoiceActivityDetector

class VoqalVadClient(
    private val project: Project,
    sensitivity: Int,
    override var voiceSilenceThreshold: Long,
    override var speechSilenceThreshold: Long,
    override var sustainedDurationMillis: Long,
    override var amnestyPeriodMillis: Long = speechSilenceThreshold * 2,
    override var testMode: Boolean = false
) : VadProvider(project) {

    companion object {
        private const val SAMPLE_RATE = 16000

        //todo: can't be static because multiple projects can be open
        @JvmStatic
        val vad: VoiceActivityDetector? by lazy {
            try {
                VoiceActivityDetector.loadLibrary()
                val instance = VoiceActivityDetector.newInstance()
                instance.setSampleRate(VoiceActivityDetector.SampleRate.fromValue(SAMPLE_RATE))
                instance
            } catch (e: UnsatisfiedLinkError) {
                //todo: not println
                println("VAD library not found: ${e.message}")
                null
            }
        }
    }

    private val log = project.getVoqalLogger(this::class)

    init {
        vad?.setMode(VoiceActivityDetector.Mode.entries[sensitivity])
        log.debug("VAD sensitivity: $sensitivity")

        project.audioCapture.registerListener(this)
    }

    override fun onAudioData(data: ByteArray, detection: SharedAudioCapture.AudioDetection) {
        val samples = convertBytesToShorts(data)
        val samplesLength = samples.size
        val step = (SAMPLE_RATE / 1000) * 10 // 10ms step (only allows 10, 20 or 30ms frame)
        for (i in 0 until samplesLength - step step step) {
            val frame = samples.copyOfRange(i, i + step)
            if (vad?.process(frame) == true) {
                handleVoiceDetected()
            } else {
                handleVoiceNotDetected()
            }
            detection.voiceDetected.set(isVoiceDetected)
            detection.speechDetected.set(isSpeechDetected)
        }
    }

    override fun dispose() = project.audioCapture.removeListener(this)
}
