package dev.voqal.assistant.memory

import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.VoqalResponse

/**
 * Represents a segment of memory.
 */
interface MemorySlice {

    val id: String
    suspend fun addMessage(
        directive: VoqalDirective,
        addMessage: Boolean = true
    ): VoqalResponse
}
