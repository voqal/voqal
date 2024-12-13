import mmarquee.automation.controls.Application
import mmarquee.automation.controls.ElementBuilder
import mmarquee.automation.controls.Window
import mmarquee.automation.pattern.Value

val rootElement = automation.rootElement
val handle = applicationManager.getWindowHandleByName("Notepad") //todo: pass window
val documentText: String? = if (handle != null) {
    try {
        val application = Application(ElementBuilder(rootElement).handle(handle).attached(true))
        val window = Window(ElementBuilder(application.element))
        val theText = try {
            window.getDocument(0).text //new notepad
        } catch (_: IndexOutOfBoundsException) {
            window.getEditBox(0).value //old notepad
        }
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