import java.util.HashMap;

class ImportTest {
    fun doRename() {
        val log = project.getVoqalLogger(this::class)
        if (editRange == null) {
            var visibleRange: TextRange? = null
            ApplicationManager.getApplication().invokeAndWait {
                visibleRange = editor.calculateVisibleRange()
            }
            editRange = visibleRange!!
            log.debug("1")

            val file = directive.developer.viewingFile
            if (file != null && limitType == "LINES") {
                if (smartEditRange != editRange) {
                    log.debug("2")
                } else if (smartEditRange != editRange) {
                    log.debug("3")
                }
            }
        }
        log.debug("4")
    }
}
