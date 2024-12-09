val application_id = toolArgs.getString("application_id")
val direction = toolArgs.getString("direction")
val theWindow = applicationManager.getWindowHandleById(application_id)
if (theWindow != null) {
    log.info("Found window: " + theWindow)
    applicationManager.bringApplicationToFrontById(theWindow)
    applicationManager.moveApplicationWindowById(theWindow, direction)
} else {
    log.info("Window not found")
}