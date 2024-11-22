const contextEncoded = JSON.parse('{{ context_encoded }}');
const line = contextEncoded.line_number;
console.log("Going to line: " + line);

//todo: editor selector
const editor = vscode.window.activeTextEditor;
if (editor) {
    const position = new vscode.Position(line - 1, 0);
    editor.selection = new vscode.Selection(position, position);
    editor.revealRange(new vscode.Range(position, position));

    const response = {
        status: 'success',
        result: null
    }
    return response;
} else {
    console.error('No active text editor');
    const response = {
        status: 'error',
        result: {
            message: 'No active text editor'
        }
    };
    return response;
}