package dev.voqal.assistant.memory.local

import com.intellij.openapi.project.Project
import dev.voqal.assistant.memory.MemorySlice
import dev.voqal.assistant.memory.MemorySystem
import dev.voqal.services.getVoqalLogger

/**
 * Memory system that stores the chat messages in local memory.
 */
class LocalMemorySystem(private val project: Project) : MemorySystem {

    override fun getMemorySlice(): MemorySlice {
        val log = project.getVoqalLogger(this::class)
        val memorySlice = LocalMemorySlice(project)
        log.debug("Created memory slice id: ${memorySlice.id}")
        return memorySlice
    }
}
