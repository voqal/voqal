package dev.voqal.services

import com.intellij.history.LocalHistory
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import dev.voqal.assistant.memory.MemorySlice
import dev.voqal.assistant.memory.MemorySystem
import dev.voqal.assistant.memory.local.LocalMemorySystem
import dev.voqal.assistant.memory.thread.ThreadMemorySystem
import dev.voqal.config.settings.PromptSettings
import dev.voqal.status.VoqalStatus

/**
 * Manages persistent and temporary memory for Voqal.
 */
@Service(Service.Level.PROJECT)
class VoqalMemoryService(private val project: Project) : Disposable {

    //todo: memory system per mode?
    private var memorySystem: MemorySystem
    private var memory: MemorySlice? = null
    private val savedLabels = mutableSetOf<String>()
    private val userData = mutableMapOf<String, MutableMap<String, Any>>()
    private val longTermUserData = mutableMapOf<String, Any?>()

    init {
        val configService = project.service<VoqalConfigService>()
        configService.onConfigChange(this) {
            memorySystem = setupMemorySystem()
        }
        memorySystem = setupMemorySystem()
    }

    private fun setupMemorySystem(): MemorySystem {
        val log = project.getVoqalLogger(this::class)
        return if (false) {
            log.info("Using thread memory system")
            ThreadMemorySystem(project)
        } else {
            log.info("Using local memory system")
            return LocalMemorySystem(project)
        }
    }

    //todo: does nothing for thread memory system
    fun resetMemory() {
        memory = memorySystem.getMemorySlice()
    }

    fun getCurrentMemory(promptSettings: PromptSettings? = null): MemorySlice {
        if (promptSettings?.promptName == "Search Mode") {
            return ThreadMemorySystem(project).getMemorySlice()
        }//todo: smarter

        val localMemory = memory
        return if (localMemory != null && project.service<VoqalStatusService>().getStatus() == VoqalStatus.EDITING) {
            localMemory
        } else {
            val newMemory = memorySystem.getMemorySlice()
            this.memory = newMemory
            newMemory
        }
    }

    fun saveEditLabel(memoryId: String, editor: Editor) {
        if (savedLabels.contains(memoryId)) return
        savedLabels.add(memoryId)

        //ensure any active changes are saved before saving label
        ApplicationManager.getApplication().invokeAndWait {
            FileDocumentManager.getInstance().saveDocument(editor.document)
        }

        //invoking cancel during edit mode will now revert up to this point
        putLongTermUserData(
            "voqal.edit.$memoryId",
            LocalHistory.getInstance().putSystemLabel(project, "voqal.edit.$memoryId")
        )
        putLongTermUserData(
            "voqal.edit.action.$memoryId",
            LocalHistory.getInstance().startAction("voqal.edit.action.$memoryId")
        )
    }

    fun putUserData(key: String, data: Any) {
        val memoryId = getCurrentMemory().id
        userData.getOrPut(memoryId) { mutableMapOf() }[key] = data
    }

    fun getUserData(key: String): Any? {
        val memoryId = getCurrentMemory().id
        return userData[memoryId]?.get(key)
    }

    fun putLongTermUserData(key: String, data: Any?) {
        longTermUserData[key] = data
    }

    fun getLongTermUserData(key: String): Any? {
        return longTermUserData[key]
    }

    fun removeLongTermUserData(key: String): Any? {
        return longTermUserData.remove(key)
    }

    override fun dispose() {
        savedLabels.clear()
        memory = null
    }
}
