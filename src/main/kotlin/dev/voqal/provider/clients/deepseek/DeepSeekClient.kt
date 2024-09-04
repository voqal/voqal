package dev.voqal.provider.clients.deepseek

import com.aallam.openai.client.OpenAIConfig
import com.intellij.openapi.project.Project
import dev.voqal.provider.clients.openai.OpenAiClient

class DeepSeekClient(
    override val name: String,
    project: Project,
    openAiConfig: OpenAIConfig
) : OpenAiClient(name, project, openAiConfig) {

    companion object {
        const val DEFAULT_MODEL = "deepseek-coder"

        @JvmStatic
        val MODELS = listOf(
            "deepseek-chat",
            "deepseek-coder"
        )

        @JvmStatic
        fun getTokenLimit(modelName: String): Int {
            return when (modelName) {
                "deepseek-chat" -> 16_385
                "deepseek-coder" -> 16_385
                else -> -1
            }
        }
    }

    override fun getAvailableModelNames() = MODELS
}
