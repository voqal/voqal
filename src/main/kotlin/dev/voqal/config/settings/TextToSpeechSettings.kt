package dev.voqal.config.settings

import dev.voqal.config.ConfigurableSettings
import io.vertx.core.json.JsonObject

data class TextToSpeechSettings(
    val voice: String = "onyx",
    val speed: Int = 100,
    val pitch: Int = 100,
    val rate: Int = 100,
    val volume: Int = 100,
    val emulateChordPitch: Boolean = false,
    val quality: Int = 0,
    val provider: TTSProvider = TTSProvider.NONE,
    val providerKey: String = "",
    val orgId: String = "",
    val modelName: String = "tts-1"
) : ConfigurableSettings {

    /**
     * Need to set defaults so config changes don't reset stored config due to parse error.
     */
    constructor(json: JsonObject) : this(
        voice = json.getString("voice") ?: "onyx",
        speed = json.getInteger("speed") ?: 100,
        pitch = json.getInteger("pitch") ?: 100,
        rate = json.getInteger("rate") ?: 100,
        volume = json.getInteger("volume") ?: 100,
        emulateChordPitch = json.getBoolean("emulateChordPitch") ?: false,
        quality = json.getInteger("quality") ?: 0,
        provider = TTSProvider.lenientValueOf(json.getString("provider") ?: TTSProvider.NONE.name),
        providerKey = json.getString("providerKey", ""),
        orgId = json.getString("orgId", ""),
        modelName = json.getString("modelName", "tts-1")
    )

    override fun toJson(): JsonObject {
        return JsonObject().apply {
            put("voice", voice)
            put("speed", speed)
            put("pitch", pitch)
            put("rate", rate)
            put("volume", volume)
            put("emulateChordPitch", emulateChordPitch)
            put("quality", quality)
            put("provider", provider.name)
            put("providerKey", providerKey)
            put("orgId", orgId)
            put("modelName", modelName)
        }
    }

    override fun withKeysRemoved(): TextToSpeechSettings {
        return copy(
            providerKey = if (providerKey == "") "" else "***",
            orgId = if (orgId == "") "" else "***"
        )
    }

    override fun withPiiRemoved(): TextToSpeechSettings {
        return withKeysRemoved()
    }

    enum class TTSProvider(val displayName: String) {
        NONE("None"),
        VOQAL_PRO("Voqal (Pro)"),
        OPENAI("OpenAI"),
        DEEPGRAM("Deepgram"),
        PICOVOICE("Picovoice");

        fun isKeyRequired(): Boolean {
            return this !in setOf(NONE, VOQAL_PRO)
        }

        fun isOrgIdAvailable(): Boolean {
            return this in setOf(OPENAI)
        }

        fun isModelRequired(): Boolean {
            return this == OPENAI
        }

        fun isVoiceNameRequired(): Boolean {
            return this in setOf(OPENAI, DEEPGRAM, PICOVOICE, VOQAL_PRO)
        }

        fun isSonicSupported(): Boolean {
            return this in setOf(OPENAI)
        }

        companion object {
            @JvmStatic
            fun lenientValueOf(str: String): TTSProvider {
                return TTSProvider.valueOf(str
                    .replace("(", "").replace(")", "")
                    .replace(" ", "_").uppercase())
            }
        }
    }
}
