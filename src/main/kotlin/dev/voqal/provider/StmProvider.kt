package dev.voqal.provider

/**
 * Provider that offers speech-to-model.
 */
interface StmProvider : AiProvider {
    val name: String
    override fun isStmProvider() = true
}
