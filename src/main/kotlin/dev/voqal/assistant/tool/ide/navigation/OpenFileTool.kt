package dev.voqal.assistant.tool.ide.navigation

import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.*
import io.vertx.core.Promise
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.coroutines.launch

class OpenFileTool : VoqalTool() {

    companion object {
        const val NAME = "open_file"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.ide.project
        val log = project.getVoqalLogger(this::class)
        var name = args.getString("name") ?: args.getString("directive")

        //todo: canShortcut should have already set this
        //open 'examples/kotlin/WhatDoesThisDo.kt'
        if (name.startsWith("open '", true) && name.endsWith("'", true)) {
            name = name.substring(6, name.length - 1)
        }
        //Open the file named 'food/apple.py'
        else if (name.startsWith("open the file named '", true) && name.endsWith("'", true)) {
            name = name.substring(21, name.length - 1)
        }
        //open the file named apple.py
        else if (name.startsWith("open the file named ", true)) {
            name = name.substring(20)
        }
        //open the file examples/python/food/broccoli.py
        else if (name.startsWith("open the file ", true)) {
            name = name.substring(14)
        }
        //Open file examples/python/food/apple.py
        else if (name.startsWith("open file ", true)) {
            name = name.substring(10)
        }
        //Open file: examples/python/food/apple.py
        else if (name.startsWith("open file: ", true)) {
            name = name.substring(11)
        }
        //open examples/kotlin/WhatDoesThisDo.kt
        else if (name.startsWith("open ", true)) {
            name = name.substring(5)
        }

        log.info("Opening file: $name")
        doOpenFile(directive, name)
    }

    private suspend fun doOpenFile(directive: VoqalDirective, filename: String) {
        val project = directive.ide.project
        val log = project.getVoqalLogger(this::class)
        val promise = Promise.promise<Void>()
        val file = project.service<VoqalSearchService>().findFile(filename)
        if (file == null) {
            log.warn("Failed to find: $filename")
            val errorMessage = "Failed to find: $filename"
            project.service<VoqalDirectiveService>().handleResponse(errorMessage)
            project.service<VoqalStatusService>().updateText(errorMessage)
            promise.complete()
        } else {
            project.invokeLater {
                log.info("Opening: $file")
                FileEditorManager.getInstance(project).openFile(file, true)
                project.scope.launch {
                    project.service<VoqalStatusService>().updateText("Opened file: $filename")
                    promise.complete()
                }
            }
        }
        promise.future().coAwait()
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(project, transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    fun attemptIntentExtract(project: Project, rawString: String): Pair<String, Map<String, String>>? {
        //do simple regex match
        val regex = Regex("open (.+)")
        val match = regex.matchEntire(rawString)
        if (match != null) {
            val name = match.groupValues[1].lowercase().trim()
            val exactMatches = project.service<VoqalSearchService>().findExactMatches(name)
            if (exactMatches.size == 1) {
                return Pair(NAME, mapOf("name" to exactMatches[0].second))
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

        //open 'examples/kotlin/WhatDoesThisDo.kt'
        if (findFile.startsWith("open '", true) && findFile.endsWith("'", true)) {
            findFile = findFile.substring(6, findFile.length - 1)
        }
        //Open the file named 'food/apple.py'
        else if (findFile.startsWith("open the file named '", true) && findFile.endsWith("'", true)) {
            findFile = findFile.substring(21, findFile.length - 1)
        }
        //open the file named apple.py
        else if (findFile.startsWith("open the file named ", true)) {
            findFile = findFile.substring(20)
        }
        //open the file examples/python/food/broccoli.py
        else if (findFile.startsWith("open the file ", true)) {
            findFile = findFile.substring(14)
        }
        //Open file examples/python/food/apple.py
        else if (findFile.startsWith("open file ", true)) {
            findFile = findFile.substring(10)
        }
        //Open file: examples/python/food/apple.py
        else if (findFile.startsWith("open file: ", true)) {
            findFile = findFile.substring(11)
        }
        //open examples/kotlin/WhatDoesThisDo.kt
        else if (findFile.startsWith("open ", true)) {
            findFile = findFile.substring(5)
        }

        //remove trailing period/comma
        if (findFile.endsWith(".") || findFile.endsWith(",")) {
            findFile = findFile.substring(0, findFile.length - 1)
        }

        val exactMatches = project.service<VoqalSearchService>().findExactMatches(findFile)
        if (exactMatches.size == 1) {
            log.debug("Shortcutting to open file: $originalFindFile")
            return true
        } else {
            log.debug("Failed to shortcut to open file: $originalFindFile")
            return false
        }
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = buildString {
            append("Opens a particular file. Do not use this tool unless specifically requested to open something. ")
            append("Do not include file extension unless provided. Do not make up filenames. ")
        },
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("name", JsonObject().apply {
                    put("type", "string")
                    put(
                        "description",
                        "The name of the file. Be as specific as you can. Do not make up file extensions."
                    )
                })
            })
            put("required", JsonArray().add("name"))
        }.toString())
    )
}
