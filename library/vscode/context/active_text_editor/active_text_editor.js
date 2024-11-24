const editor = vscode.window.activeTextEditor;
if (editor) {
    const selectedText = editor.selection.isEmpty ? null : editor.document.getText(editor.selection);
    const response = {
        status: 'success',
        result: {
            document: {
                text: editor.document.getText(),
                path: editor.document.fileName.replace(/\\/g, '/'),
                filename: editor.document.fileName.replace(/\\/g, '/').split('/').pop(),
                selectedText: selectedText,
                language: editor.document.languageId
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