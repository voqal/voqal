import mmarquee.automation.controls.AutomationBase
import mmarquee.automation.controls.Application
import mmarquee.automation.controls.ElementBuilder
import mmarquee.automation.controls.Window
import mmarquee.automation.pattern.Value

val text = toolArgs.getString("text")
val rootElement = automation.rootElement
val handle = applicationManager.getWindowHandleByName("Notepad") //todo: pass window
val application = Application(ElementBuilder(rootElement).handle(handle).attached(true))
val window = Window(ElementBuilder(application.element))
val document: AutomationBase? = try {
    window.getDocument(0) //new notepad
} catch (e: IndexOutOfBoundsException) {
    try {
        window.getEditBox(0) //old notepad
    } catch (e: IndexOutOfBoundsException) {
        log.error("Failed to get edit box") //todo: add exception
        null
    }
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
            log.error("Failed to set text") //todo: add exception
        }
    }
} else if (document != null) {
    log.warn("Value pattern not available")
}