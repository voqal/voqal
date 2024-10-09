package dev.voqal.provider.clients.voqal

import com.intellij.openapi.project.Project
import dev.voqal.provider.clients.azure.AzureClient

class VoqalProClient(
    override val name: String,
    project: Project,
    providerKey: String,
    deployment: String,
    audioModality: Boolean = false
) : AzureClient(
    name,
    project,
    "https://voqal-proxy.voqaldev.workers.dev/",
    providerKey,
    deployment,
    audioModality,
    wssHeaders = mapOf(
        "api-key" to providerKey,
        "voqal-model-name" to deployment
    )
) {

    companion object {
        const val DEFAULT_MODEL = "gpt-4o-mini"

        @JvmStatic
        fun getTokenLimit(modelName: String): Int {
            return when {
                modelName == "Llama-3.2-90B-Vision-Instruct" -> 8192
                else -> -1
            }
        }

        @JvmStatic
        val MODELS = listOf(
            "gpt-4o-realtime-preview",
            "gpt-35-turbo",
            "gpt-4o",
            "gpt-4o-mini",
            "Meta-Llama-3.1-405B-Instruct",
            "Llama-3.2-90B-Vision-Instruct"
        )
    }
}
