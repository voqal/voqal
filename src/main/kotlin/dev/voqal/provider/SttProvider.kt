package dev.voqal.provider

import java.io.File
import javax.sound.sampled.AudioSystem

/**
 * Provider that offers speech-to-text.
 */
interface SttProvider : AiProvider {
    override fun isSttProvider() = true
    suspend fun transcribe(speechFile: File, modelName: String): String

    fun getAudioDuration(speechFile: File): Double {
        return AudioSystem.getAudioInputStream(speechFile).use {
            val format = it.format
            val frames = it.frameLength
            (frames + 0.0) / format.frameRate
        }
    }
}
