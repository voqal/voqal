package dev.voqal.assistant.tool.ide.navigation

import ai.grazie.utils.capitalize
import com.aallam.openai.api.chat.FunctionCall
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalSearchService
import dev.voqal.services.getVoqalLogger
import io.vertx.core.json.JsonObject

abstract class NavigateFileTool(private val operation: NavigateOperation) : VoqalTool() {

    enum class NavigateOperation { OPEN, CLOSE }

    private val op = operation.name.lowercase()
    private val opLength = op.length

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.ide.project
        val log = project.getVoqalLogger(this::class)
        var name = args.getString("name") ?: args.getString("directive")

        //todo: canShortcut should have already set this
        //$op 'examples/kotlin/WhatDoesThisDo.kt'
        if (name.startsWith("$op '", true) && name.endsWith("'", true)) {
            name = name.substring(opLength + 2, name.length - 1)
        }
        //$op the file named 'food/apple.py'
        else if (name.startsWith("$op the file named '", true) && name.endsWith("'", true)) {
            name = name.substring(opLength + 17, name.length - 1)
        }
        //$op the file named apple.py
        else if (name.startsWith("$op the file named ", true)) {
            name = name.substring(opLength + 16)
        }
        //$op the file examples/python/food/broccoli.py
        else if (name.startsWith("$op the file ", true)) {
            name = name.substring(opLength + 10)
        }
        //$op file examples/python/food/apple.py
        else if (name.startsWith("$op file ", true)) {
            name = name.substring(opLength + 6)
        }
        //$op file: examples/python/food/apple.py
        else if (name.startsWith("$op file: ", true)) {
            name = name.substring(opLength + 7)
        }
        //$op examples/kotlin/WhatDoesThisDo.kt
        else if (name.startsWith("$op ", true)) {
            name = name.substring(opLength + 1)
        }

        log.info("${op.lowercase().capitalize()}ing file: $name")
        doFileOperation(directive, name)
    }

    abstract suspend fun doFileOperation(directive: VoqalDirective, filename: String?)

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(project, transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    internal fun attemptIntentExtract(project: Project, rawString: String): Pair<String, Map<String, String>>? {
        //do simple regex match
        val regex = Regex("$op (.+)")
        val match = regex.matchEntire(rawString)
        if (match != null) {
            val findFile = match.groupValues[1].lowercase().trim()
            val matchFiles = if (operation == NavigateOperation.OPEN) {
                project.service<VoqalSearchService>().findExactMatches(findFile).map { it.first }
            } else {
                val openFiles = FileEditorManager.getInstance(project).openFiles.toList()
                project.service<VoqalSearchService>().findFiles(openFiles, findFile)
            }
            if (matchFiles.size == 1) {
                return Pair(this.name, mapOf("name" to matchFiles[0].name))
            }
        }
        return null
    }

    override fun canShortcut(project: Project, call: FunctionCall): Boolean {
        val log = project.getVoqalLogger(this::class)
        var findFile: String?
        try {
            findFile = JsonObject(call.arguments).getString("directive")!!
        } catch (_: Exception) {
            return false
        }
        val originalFindFile = findFile

        //$op 'examples/kotlin/WhatDoesThisDo.kt'
        if (findFile.startsWith("$op '", true) && findFile.endsWith("'", true)) {
            findFile = findFile.substring(opLength + 2, findFile.length - 1)
        }
        //$op the file named 'food/apple.py'
        else if (findFile.startsWith("$op the file named '", true) && findFile.endsWith("'", true)) {
            findFile = findFile.substring(opLength + 17, findFile.length - 1)
        }
        //$op the file named apple.py
        else if (findFile.startsWith("$op the file named ", true)) {
            findFile = findFile.substring(opLength + 16)
        }
        //$op the file examples/python/food/broccoli.py
        else if (findFile.startsWith("$op the file ", true)) {
            findFile = findFile.substring(opLength + 10)
        }
        //$op file examples/python/food/apple.py
        else if (findFile.startsWith("$op file ", true)) {
            findFile = findFile.substring(opLength + 6)
        }
        //$op file: examples/python/food/apple.py
        else if (findFile.startsWith("$op file: ", true)) {
            findFile = findFile.substring(opLength + 7)
        }
        //$op examples/kotlin/WhatDoesThisDo.kt
        else if (findFile.startsWith("$op ", true)) {
            findFile = findFile.substring(opLength + 1)
        }

        //remove trailing period/comma
        if (findFile.endsWith(".") || findFile.endsWith(",")) {
            findFile = findFile.substring(0, findFile.length - 1)
        }

        val matchFiles = if (operation == NavigateOperation.OPEN) {
            project.service<VoqalSearchService>().findExactMatches(findFile)
        } else {
            val openFiles = FileEditorManager.getInstance(project).openFiles.toList()
            project.service<VoqalSearchService>().findFiles(openFiles, findFile)
        }
        if (matchFiles.size == 1) {
            log.debug("Shortcutting to $op file: $originalFindFile")
            return true
        } else {
            log.debug("Failed to shortcut to $op file: $originalFindFile")
            return false
        }
    }
}
