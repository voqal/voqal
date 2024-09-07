package dev.voqal.assistant.tool.ide.navigation

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollingModel
import com.intellij.openapi.editor.impl.ScrollingModelImpl
import com.intellij.openapi.project.Project
import com.intellij.util.Alarm
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.assistant.tool.ide.navigation.ScrollTool.ScrollDirection.*
import dev.voqal.services.ProjectScopedService
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import dev.voqal.services.invokeLater
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

class ScrollTool : VoqalTool() {

    companion object {
        const val NAME = "scroll"
    }

    override val name = NAME

    enum class ScrollDirection {
        UP, DOWN,
        START, TOP,
        END, BOTTOM,
        MIDDLE
    }

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        val editor = directive.ide.editor
        if (editor != null) {
            val direction = ScrollDirection.valueOf(args.getString("scrollDirection").uppercase())
            val scrollType = args.getString("scrollType") ?: "scroll"
            if (scrollType == "page") {
                ApplicationManager.getApplication().invokeAndWait {
                    scroll(editor, direction, true)
                }
            } else {
                ApplicationManager.getApplication().invokeAndWait {
                    scroll(editor, direction, false)
                }
            }
            project.service<VoqalStatusService>().updateText("Scrolled ${direction.name.lowercase()}")
        } else {
            log.warn("No selected text editor")
            project.service<VoqalStatusService>().updateText("No selected text editor")
        }
    }

    private fun scroll(editor: Editor, direction: ScrollDirection, fullPage: Boolean) {
        val scrollingModel: ScrollingModel = editor.scrollingModel
        val verticalScrollBar = (scrollingModel as ScrollingModelImpl).verticalScrollBar!!
        val visibleArea = scrollingModel.visibleArea
        val pageHeight = if (fullPage) visibleArea.height else visibleArea.height / 2

        val currentScrollPosition = verticalScrollBar.value
        val newScrollPosition = when (direction) {
            UP -> (currentScrollPosition - pageHeight).coerceAtLeast(verticalScrollBar.minimum)
            DOWN -> (currentScrollPosition + pageHeight).coerceAtMost(verticalScrollBar.maximum - verticalScrollBar.visibleAmount)
            START, TOP -> verticalScrollBar.minimum
            END, BOTTOM -> verticalScrollBar.maximum
            MIDDLE -> (verticalScrollBar.minimum + verticalScrollBar.maximum - verticalScrollBar.visibleAmount) / 2
        }

        //smooth scrolling
        val animationDuration = 150
        val frameRate = 10
        val totalFrames = animationDuration / frameRate
        val deltaPerFrame = (newScrollPosition - currentScrollPosition).toFloat() / totalFrames
        val alarm = Alarm(Alarm.ThreadToUse.SWING_THREAD, editor.project!!.service<ProjectScopedService>())
        for (i in 1..totalFrames) {
            alarm.addRequest({
                editor.project!!.invokeLater {
                    val newValue = currentScrollPosition + deltaPerFrame * i
                    verticalScrollBar.value = newValue.toInt().coerceIn(
                        verticalScrollBar.minimum,
                        verticalScrollBar.maximum - verticalScrollBar.visibleAmount
                    )
                }
            }, frameRate * i)
        }
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    internal fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        val regex1 = "^(scroll|page)( up| down)\$".toRegex()
        val matchResult1 = regex1.find(rawString)
        matchResult1?.let { match ->
            val scrollType = match.groups[1]!!.value
            val direction = match.groups[2]!!.value.uppercase().trimStart()
            return Pair(NAME, mapOf("scrollDirection" to direction, "scrollType" to scrollType))
        }

        val regex2 = "^(scroll|page|go)( up| down)? (?:to (?:the )?(top|bottom|start|end|middle))?\$".toRegex()
        val matchResult2 = regex2.find(rawString)
        matchResult2?.let { match ->
            val scrollType = match.groups[1]!!.value
            var direction = when (match.groups[2]?.value) {
                " up" -> "UP"
                " down" -> "DOWN"
                else -> null
            }
            val position = match.groups[3]?.value?.uppercase()
            if (position != null) {
                direction = when (position) {
                    "TOP" -> "TOP"
                    "BOTTOM" -> "BOTTOM"
                    "START" -> "START"
                    "END" -> "END"
                    "MIDDLE" -> "MIDDLE"
                    else -> throw IllegalArgumentException("Invalid scroll position")
                }
            }
            return Pair(NAME, mapOf("scrollDirection" to direction!!, "scrollType" to scrollType))
        }

        return when (rawString) {
            "just go to the bottom" -> Pair(NAME, mapOf("scrollDirection" to "BOTTOM", "scrollType" to "scroll"))
            else -> null
        }
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Scroll the editor",
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("scrollDirection", JsonObject().apply {
                    put("type", "string")
                    put("description", "Direction to scroll")
                    put(
                        "enum", JsonArray()
                            .add("UP").add("DOWN")
                            .add("TOP").add("BOTTOM")
                            .add("START").add("END")
                            .add("MIDDLE")
                    )
                })
                put("scrollType", JsonObject().apply {
                    put("type", "string")
                    put(
                        "description",
                        "Type of scroll. 'scroll' moves half page, 'page' moves full page. Default is 'scroll'"
                    )
                    put("enum", JsonArray().add("scroll").add("page"))
                    //put("default", "scroll")
                })
            })
            put("required", JsonArray().add("scrollDirection"))
        }.toString())
    )
}
