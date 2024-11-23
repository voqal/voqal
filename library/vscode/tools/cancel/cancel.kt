import java.io.File
import io.vertx.core.json.JsonObject

val userInfoFile = File(memoryDataDir, "vscode")
if (!userInfoFile.exists()) {
    userInfoFile.parentFile.mkdirs()
    userInfoFile.createNewFile()
}

val existingData = if (userInfoFile.length() > 0) {
    JsonObject(userInfoFile.readText())
} else {
    JsonObject()
}
userInfoFile.writeText(existingData.apply {
    put("edit_mode", false)
}.toString())
