val application_id = toolArgs.getString("application_id")
val window = applicationManager.getWindowHandleById(application_id)
if (window != null) {
    log.info("Found application: " + window)
    applicationManager.bringApplicationToFrontById(window)
} else {
    log.info("Application not found")
}