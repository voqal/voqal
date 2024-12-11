val application_name = toolArgs.getString("application_name")
log.info("Opening application: $application_name")
val application = automation.launch(application_name)