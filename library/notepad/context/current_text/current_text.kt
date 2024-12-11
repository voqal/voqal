import mmarquee.automation.UIAutomation
import mmarquee.automation.controls.Application
import mmarquee.automation.controls.ElementBuilder
import mmarquee.automation.controls.Search
import mmarquee.automation.controls.Window
import mmarquee.automation.pattern.Value

val rootElement = automation.rootElement
val handle = applicationManager.getWindowHandleByName("Notepad") //todo: pass window
val documentText: String? = if (handle != null) {
    try {
        val application = Application(ElementBuilder(rootElement).handle(handle).attached(true))
        val window = Window(ElementBuilder(application.element))
        val theText = window.getDocument(Search.getBuilder(0).build()).text
        theText
    } catch (e: Exception) {
        log.warn("Failed to get text from Notepad")
        null
    }
} else {
    log.warn("Notepad window not found")
    null
}
documentText