package benchmark.model.context

import com.intellij.openapi.vfs.VirtualFile
import dev.voqal.assistant.context.VoqalContext

class OpenFileContext(val virtualFile: VirtualFile) : VoqalContext
