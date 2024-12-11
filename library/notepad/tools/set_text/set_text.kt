import mmarquee.automation.UIAutomation
import mmarquee.automation.controls.Application
import mmarquee.automation.controls.ElementBuilder
import mmarquee.automation.controls.Search
import mmarquee.automation.controls.Window
import mmarquee.automation.pattern.Value

val text = toolArgs.getString("text")
val rootElement = automation.rootElement
val handle = applicationManager.getWindowHandleByName("Notepad") //todo: pass window
val application = Application(ElementBuilder(rootElement).handle(handle).attached(true))
val window = Window(ElementBuilder(application.element))
val document = try {
    window.getDocument(Search.getBuilder(0).build())
} catch (e: IndexOutOfBoundsException) {
    log.error("Failed to get document", e)
    null
}
if (document?.isValuePatternAvailable == true) {
    val valuePattern = document.requestAutomationPattern<Value>(Value::class.java)
    if (valuePattern.isReadOnly) {
        log.warn("The document is read-only")
    } else {
        log.info("Setting text to: $text")
        try {
            valuePattern.setValue(text)
        } catch (e: Exception) {
            log.error("Failed to set text", e)
        }
    }
} else if (document != null) {
    log.warn("Value pattern not available")
}