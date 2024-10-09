package dev.voqal.config.settings

import dev.voqal.config.ConfigurableSettings
import dev.voqal.utils.Iso639Language
import io.vertx.core.json.JsonObject

data class SpeechToTextSettings(
    val provider: STTProvider = STTProvider.NONE,
    val providerKey: String = "",
    val orgId: String = "",
    val providerUrl: String = "",
    val modelName: String = "whisper-1",
    val queryParams: String = "",
    val language: Iso639Language = Iso639Language.ENGLISH,
    val streamAudio: Boolean = true
) : ConfigurableSettings {

    /**
     * Need to set defaults so config changes don't reset stored config due to parse error.
     */
    constructor(json: JsonObject) : this(
        provider = STTProvider.lenientValueOf(json.getString("provider") ?: STTProvider.NONE.name),
        providerKey = json.getString("providerKey", ""),
        orgId = json.getString("orgId", ""),
        providerUrl = json.getString("providerUrl", ""),
        modelName = json.getString("modelName", "whisper-1"),
        queryParams = json.getString("queryParams", ""),
        language = Iso639Language.findByCode(json.getString("language", Iso639Language.ENGLISH.code)),
        streamAudio = json.getBoolean("streamAudio", true)
    )

    override fun toJson(): JsonObject {
        return JsonObject().apply {
            put("provider", provider.name)
            put("providerKey", providerKey)
            put("orgId", orgId)
            put("providerUrl", providerUrl)
            put("modelName", modelName)
            put("queryParams", queryParams)
            put("language", language.code)
            put("streamAudio", streamAudio)
        }
    }

    override fun withKeysRemoved(): SpeechToTextSettings {
        return copy(
            providerKey = if (providerKey == "") "" else "***",
            orgId = if (orgId == "") "" else "***"
        )
    }

    override fun withPiiRemoved(): SpeechToTextSettings {
        return withKeysRemoved().copy(
            providerUrl = if (providerUrl == "") "" else "***",
            queryParams = if (queryParams == "") "" else "***"
        )
    }

    enum class STTProvider(val displayName: String) {
        NONE("None"),
        VOQAL_PRO("Voqal (Pro)"),
        DEEPGRAM("Deepgram"),
        GROQ("Groq"),
        OPENAI("OpenAI"),
        ASSEMBLYAI("AssemblyAI"),
        PICOVOICE("Picovoice"),
        WHISPER_ASR("Whisper ASR");

        fun isKeyRequired(): Boolean {
            return this !in setOf(NONE, WHISPER_ASR, VOQAL_PRO)
        }

        fun isOrgIdAvailable(): Boolean {
            return this in setOf(OPENAI)
        }

        fun isUrlRequired(): Boolean {
            return this == WHISPER_ASR
        }

        fun isModelNameRequired(): Boolean {
            return this in setOf(OPENAI, DEEPGRAM, GROQ)
        }

        fun isQueryParamsSupported(): Boolean {
            return this == DEEPGRAM
        }

        fun isLanguageCodeSupported(): Boolean {
            return this in setOf(OPENAI, GROQ, DEEPGRAM, VOQAL_PRO)
        }

        fun isStreamAudioSupported(): Boolean {
            return this in setOf(PICOVOICE, DEEPGRAM)
        }

        companion object {
            @JvmStatic
            fun lenientValueOf(str: String): STTProvider {
                return STTProvider.valueOf(str
                    .replace("(", "").replace(")", "")
                    .replace(" ", "_").uppercase())
            }
        }
    }
}
