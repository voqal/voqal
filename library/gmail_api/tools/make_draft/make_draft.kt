val text = toolArgs.getString("text")
val emailId = context.getJsonObject("event").getJsonObject("email").getString("id")
log.info("Making draft - Email: $emailId")
gmailApiConnection.makeDraft(emailId, text)