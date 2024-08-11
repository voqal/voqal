package dev.voqal.provider

import com.aallam.openai.api.audio.SpeechRequest
import io.ktor.utils.io.*

/**
 * Provider that offers text-to-speech.
 */
interface TtsProvider : AiProvider {
    override fun isTtsProvider() = true
    suspend fun speech(request: SpeechRequest): RawAudio
    fun isWavOutput() = false
    fun isRawOutput() = false

    data class RawAudio(
        val audio: ByteReadChannel,
        val sampleRate: Float,
        val bitsPerSample: Int,
        val channels: Int
    )
}
