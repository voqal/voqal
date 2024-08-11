package dev.voqal.provider

import com.intellij.openapi.project.Project
import dev.voqal.services.getVoqalLogger
import dev.voqal.utils.SharedAudioCapture
import java.util.*

/**
 * Provider that offers voice activity detection.
 */
abstract class VadProvider : AiProvider, SharedAudioCapture.AudioDataListener {

    abstract var sustainedDurationMillis: Long
    abstract var amnestyPeriodMillis: Long
    abstract var voiceSilenceThreshold: Long
    abstract var speechSilenceThreshold: Long
    abstract var testMode: Boolean

    @Volatile
    var speechId: String = "n/a"

    @Volatile
    var isVoiceCaptured = false

    @Volatile
    var isVoiceDetected = false

    @Volatile
    var isSpeechDetected = false

    @Volatile
    var voiceProbability: Double = 0.0

    private var voiceFirstDetectedAt = 0L
    private var voiceLastDetectedAt = 0L
    private var begunTalkingTimeMillis = 0L

    override fun isVadProvider() = true
    override fun isLiveDataListener() = true

    fun handleVoiceNotDetected(project: Project) {
        val silenceTime = System.currentTimeMillis() - voiceLastDetectedAt
        val overVoiceSilenceTime = (silenceTime > voiceSilenceThreshold
                && begunTalkingTimeMillis != voiceLastDetectedAt)
        val overSpeechSilenceTime = (silenceTime > speechSilenceThreshold
                && begunTalkingTimeMillis != voiceLastDetectedAt)
        val overAmnestyTime = (begunTalkingTimeMillis == voiceLastDetectedAt
                && silenceTime > amnestyPeriodMillis)

        if (isSpeechDetected && (overSpeechSilenceTime || overAmnestyTime)) {
            isVoiceDetected = false
            isSpeechDetected = false
            begunTalkingTimeMillis = 0L
        } else if (isVoiceDetected && overVoiceSilenceTime) {
            isVoiceDetected = false
        }
        voiceFirstDetectedAt = 0
        isVoiceCaptured = false
    }

    fun handleVoiceDetected(project: Project) {
        val log = project.getVoqalLogger(this::class)
        if (voiceFirstDetectedAt == 0L) {
            voiceFirstDetectedAt = System.currentTimeMillis()
        }
        voiceLastDetectedAt = System.currentTimeMillis()
        isVoiceCaptured = true

        if (voiceLastDetectedAt - voiceFirstDetectedAt >= sustainedDurationMillis) {
            isVoiceDetected = true

            if (!isSpeechDetected) {
                isSpeechDetected = true
                speechId = UUID.randomUUID().toString()
                log.info("Using speech id: $speechId")

                if (begunTalkingTimeMillis == 0L) {
                    begunTalkingTimeMillis = voiceLastDetectedAt
                }
            }
        }
    }
}
