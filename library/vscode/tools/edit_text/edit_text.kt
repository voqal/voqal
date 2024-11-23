import java.io.File

val fileLocation = context
    .getJsonObject("library").getJsonObject("vscode")
    .getJsonObject("active_text_editor").getJsonObject("document").getString("fileName")
log.info("File location: $fileLocation")

val editText = toolArgs.getString("text")
log.info("Setting text: $editText")

//1|public class Test {
//2|}
val lineNumberRegex = Regex("^\\d+\\|")
val hasLineNumbers = editText.lines().all { lineNumberRegex.containsMatchIn(it) }
val codeBlockWithoutLineNumbers = if (hasLineNumbers) {
    editText.lines().joinToString("\n") { it.replaceFirst(lineNumberRegex, "") }
} else {
    editText
}

val file = File(fileLocation)
file.writeText(codeBlockWithoutLineNumbers)