const editor = vscode.window.activeTextEditor;
if (editor) {
    const selectedText = editor.selection.isEmpty ? null : editor.document.getText(editor.selection);
    const response = {
        status: 'success',
        result: {
            document: {
                text: editor.document.getText(),
                fileName: editor.document.fileName,
                selectedText: selectedText
            }
        }
    };
    return response;
} else {
    const response = {
        status: 'success',
        result: null
    };
    return response;
}