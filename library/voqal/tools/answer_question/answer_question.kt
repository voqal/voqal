import com.aallam.openai.api.audio.Voice
import com.aallam.openai.api.model.ModelId
import dev.voqal.provider.TtsProvider
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

var savedText = ""
val speechQueue = LinkedBlockingQueue<String>()
val firstCall = AtomicBoolean(true)
val request = TtsProvider.SpeechStreamRequest(
    model = ModelId("todo"),
    queue = speechQueue,
    voice = Voice("male")
)

contextManager.registerContextListener {
    val text = it.context["text"] as String? ?: return@registerContextListener
    if (aiProvider.isTtsProvider()) {
        val ttsProvider = aiProvider.asTtsProvider()
        if (ttsProvider.isTtsStreamable()) {
            val newText = text.substring(savedText.length)
            savedText = text
            if (newText.isNotEmpty()) {
                speechQueue.add(newText)
            }
            request.isFinished = it.final
            if (firstCall.getAndSet(false)) {
                voiceService.streamPlayVoice(request)
            }
            if (request.isFinished) {
                firstCall.set(true)
                savedText = ""
            }
        } else if (it.final) {
            voiceService.playVoiceAndWait(text)
        }
    } else if (it.final) {
        log.warn("TTS provider is not available")
        log.info("TTS: $text")
    }
}