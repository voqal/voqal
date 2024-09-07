package dev.voqal.assistant.memory.local

import com.intellij.openapi.project.Project
import dev.voqal.assistant.memory.MemorySlice
import dev.voqal.assistant.memory.MemorySystem
import dev.voqal.services.getVoqalLogger

/**
 * Memory system that stores the chat messages in local memory.
 */
class LocalMemorySystem(private val project: Project) : MemorySystem {

    private val log = project.getVoqalLogger(this::class)

    override fun getMemorySlice(): MemorySlice {
        val memorySlice = LocalMemorySlice(project)
        log.debug("Created memory slice id: ${memorySlice.id}")
        return memorySlice
    }
}
