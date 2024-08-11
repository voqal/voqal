package dev.voqal.assistant.memory

/**
 * Represents a store for chat messages.
 */
interface MemorySystem {

    fun getMemorySlice(): MemorySlice
}
