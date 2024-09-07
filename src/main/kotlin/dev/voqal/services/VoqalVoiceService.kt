package dev.voqal.services

import com.aallam.openai.api.audio.SpeechRequest
import com.aallam.openai.api.audio.Voice
import com.aallam.openai.api.exception.OpenAIException
import com.aallam.openai.api.model.ModelId
import com.google.common.io.Resources
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import dev.voqal.config.settings.TextToSpeechSettings
import dev.voqal.provider.TtsProvider
import dev.voqal.utils.SonicSpeechModifier
import io.ktor.util.*
import io.ktor.utils.io.*
import javazoom.jl.converter.Converter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.math.BigInteger
import java.nio.channels.UnresolvedAddressException
import java.nio.file.Files
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicBoolean
import javax.sound.sampled.*
import kotlin.coroutines.cancellation.CancellationException

/**
 * Used for TTS.
 */
@Service(Service.Level.PROJECT)
class VoqalVoiceService(private val project: Project) {

    private val log = project.getVoqalLogger(this::class)
    private var activeAudio = AtomicBoolean()

    fun playSound(input: String, tts: TextToSpeechSettings? = null) {
        project.scope.launch {
            playSoundAndWait(input, tts)
        }
    }

    private suspend fun playSoundAndWait(input: String, tts: TextToSpeechSettings?) {
        if (System.getProperty("VQL_TEST_MODE") == "true") return
        try {
            log.info("Playing resource: $input")
            val config = project.service<VoqalConfigService>().getConfig()

            BufferedInputStream(
                withContext(Dispatchers.IO) {
                    Resources.getResource(VoqalVoiceService::class.java, "/sounds/$input.wav").openStream()
                }
            ).use { playWavFile(it, tts ?: config.textToSpeechSettings) }
        } catch (_: CancellationException) {
            log.warn("Play resource canceled")
        } catch (e: Exception) {
            log.error("Failed to play resource", e)
        }
    }

    suspend fun playVoiceAndWait(input: String, tts: TextToSpeechSettings? = null) {
        if (System.getProperty("VQL_TEST_MODE") == "true") {
            log.debug("Skipping TTS audio for: $input")
            return
        }
        try {
            val aiProvider = project.service<VoqalConfigService>().getAiProvider()
            if (aiProvider.isTtsProvider()) {
                log.info("Voicing: $input")
                val config = project.service<VoqalConfigService>().getConfig()
                val ttsProvider = aiProvider.asTtsProvider()
                if (ttsProvider.isRawOutput()) {
                    val stream = ttsProvider.speech(
                        SpeechRequest(
                            model = ModelId(config.textToSpeechSettings.modelName),
                            input = input,
                            voice = Voice(config.textToSpeechSettings.voice)
                        )
                    )
                    playRawAudio(stream)
                } else {
                    playWavFile(getVoice(input), tts ?: config.textToSpeechSettings)
                }
            } else {
                log.warn("No text-to-speech provider available")
            }
        } catch (_: CancellationException) {
            log.warn("Play voice canceled")
        } catch (e: OpenAIException) {
            log.warn(e.message ?: "An unknown error occurred while voicing: $input")
            project.service<VoqalDirectiveService>().handleResponse(
                "Failed to voice: $input",
                isTextOnly = true
            )
        } catch (e: UnresolvedAddressException) {
            log.warn("Failed to invoke network operation on unresolved address")
            project.service<VoqalDirectiveService>().handleResponse(
                "Failed to voice: $input",
                isTextOnly = true
            )
        } catch (e: Exception) {
            log.error("Failed to play voice", e)
        }
    }

    private fun playRawAudio(audioData: TtsProvider.RawAudio) {//todo: sonic
        val af = AudioFormat(audioData.sampleRate, audioData.bitsPerSample, audioData.channels, true, false)
        val info = DataLine.Info(SourceDataLine::class.java, af)
        val line = AudioSystem.getLine(info) as SourceDataLine
        line.open(af)
        line.start()
        runBlocking {
            val buffer = ByteArray(1024)
            while (!audioData.audio.isClosedForRead) {
                val read = audioData.audio.readAvailable(buffer)
                if (read > 0) {
                    line.write(buffer, 0, read)
                }
            }
            line.drain()
            line.stop()
            line.close()
        }
    }

    private suspend fun getVoice(input: String): File {
        val configService = project.service<VoqalConfigService>()
        val textToSpeechSettings = configService.getConfig().textToSpeechSettings

        val inputMd5 = md5(input + "-" + textToSpeechSettings.voice)
        val tmpDir = File(File(System.getProperty("java.io.tmpdir")), "voqal-natives")
        val speechDir = File(tmpDir, "speech")
        speechDir.mkdirs()
        val convertedFile = File(speechDir, "$inputMd5.wav")
        if (convertedFile.exists()) {
            log.debug("Reusing existing speech file: $convertedFile")
            return convertedFile
        }

        val ttsProvider = configService.getAiProvider().asTtsProvider()
        val data = ttsProvider.speech(
            SpeechRequest(
                model = ModelId(textToSpeechSettings.modelName),
                input = input,
                voice = Voice(textToSpeechSettings.voice)
            )
        ).audio.toByteArray()
        if (ttsProvider.isWavOutput()) {
            val speechFile = File(speechDir, "$inputMd5.wav")
            withContext(Dispatchers.IO) { Files.write(speechFile.toPath(), data) }
            return speechFile
        } else {
            val speechFile = File(speechDir, "$inputMd5.mp3")
            withContext(Dispatchers.IO) { Files.write(speechFile.toPath(), data) }

            val converter = Converter()
            converter.convert(speechFile.absolutePath, convertedFile.absolutePath)
            return convertedFile
        }
    }

    private fun playAudio(stream: AudioInputStream, tts: TextToSpeechSettings) {
        val format = stream.format
        val sampleRate = format.sampleRate.toInt()
        val numChannels = format.channels
        val info = DataLine.Info(SourceDataLine::class.java, format, stream.frameLength.toInt() * format.frameSize)

        try {
            activeAudio.set(false)
            activeAudio = AtomicBoolean(true)
            val line = AudioSystem.getLine(info) as SourceDataLine
            line.open(stream.format)
            line.start()
            runSonic(stream, line, sampleRate, numChannels, tts, activeAudio)
            line.drain()
            line.stop()
        } catch (e: Exception) {
            log.error("Failed to play audio", e)
        }
    }

    private fun runSonic(
        audioStream: AudioInputStream,
        line: SourceDataLine,
        sampleRate: Int,
        numChannels: Int,
        textToSpeechSettings: TextToSpeechSettings,
        active: AtomicBoolean
    ) {
        val sonic = SonicSpeechModifier(sampleRate, numChannels)
        val bufferSize = line.bufferSize
        val inBuffer = ByteArray(bufferSize)
        val outBuffer = ByteArray(bufferSize)
        var numRead: Int
        var numWritten: Int

        sonic.speed = textToSpeechSettings.speed / 100.0f
        sonic.pitch = textToSpeechSettings.pitch / 100.0f
        sonic.setRate(textToSpeechSettings.rate / 100.0f)
        sonic.volume = textToSpeechSettings.volume / 100.0f
        sonic.chordPitch = textToSpeechSettings.emulateChordPitch
        sonic.quality = textToSpeechSettings.quality
        do {
            numRead = audioStream.read(inBuffer, 0, bufferSize)
            if (numRead <= 0) {
                sonic.flushStream()
            } else {
                sonic.writeBytesToStream(inBuffer, numRead)
            }
            do {
                numWritten = sonic.readBytesFromStream(outBuffer, bufferSize)
                if (numWritten > 0) {
                    line.write(outBuffer, 0, numWritten)
                }
            } while (active.get() && numWritten > 0)
        } while (active.get() && numRead > 0)
    }

    private suspend fun playWavFile(wavFile: File, tts: TextToSpeechSettings) {
        BufferedInputStream(wavFile.inputStream()).use {
            playWavFile(it, tts)
        }
    }

    private suspend fun playWavFile(inputStream: InputStream, tts: TextToSpeechSettings) {
        val buffer = withContext(Dispatchers.IO) {
            inputStream.readAllBytes()
        }

        val stream = withContext(Dispatchers.IO) {
            AudioSystem.getAudioInputStream(ByteArrayInputStream(buffer))
        }
        playAudio(stream, tts)
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}
