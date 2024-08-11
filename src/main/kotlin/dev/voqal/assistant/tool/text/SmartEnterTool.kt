package dev.voqal.assistant.tool.text

import com.aallam.openai.api.chat.Tool
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.psi.PsiDocumentManager
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.coroutines.runBlocking

class SmartEnterTool : VoqalTool() {

    companion object {
        const val NAME = "smart_enter"

        fun doSmartEnter(project: Project, editor: Editor) {
            val promise = Promise.promise<Void>()
            val actionManager = EditorActionManager.getInstance()
            val actionHandler = actionManager.getActionHandler(
                IdeActions.ACTION_EDITOR_COMPLETE_STATEMENT
            )
            actionHandler.runForAllCarets() //todo: use only carets can find
            WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                actionHandler.execute(
                    editor,
                    null,
                    DataManager.getInstance().dataContext
                )

                val psiFile = editor.document.let { document ->
                    ReadAction.compute(ThrowableComputable {
                        PsiDocumentManager.getInstance(project).getPsiFile(document)
                    })
                }
                if (psiFile?.language?.id?.lowercase() == "swift") {
                    //add tab to complete swift code
                    actionManager.getActionHandler(
                        IdeActions.ACTION_EDITOR_TAB
                    ).execute(
                        editor,
                        null,
                        DataManager.getInstance().dataContext
                    )
                }

                promise.complete()
            })
            runBlocking { promise.future().coAwait() }
        }
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.ide.project
        val log = project.getVoqalLogger(this::class)
        val editor = directive.ide.editor
        if (editor == null) {
            log.warn("No editor found")
            project.service<VoqalStatusService>().updateText("No editor found")
            return
        }

        doSmartEnter(project, editor)
    }

    override fun isVisible(directive: VoqalDirective): Boolean {
        return false
    }

    override fun asTool(directive: VoqalDirective): Tool {
        throw UnsupportedOperationException("Invalid operation")
    }
}
