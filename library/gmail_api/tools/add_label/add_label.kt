val label = toolArgs.getString("label")
if (label == "Unknown") {
    log.debug("Ignoring Unknown label")
} else {
    val emailId = context.getJsonObject("event").getJsonObject("email").getString("id")
    log.info("Adding label: $label - Email: $emailId")
    gmailApiConnection.addLabel(emailId, label)
}