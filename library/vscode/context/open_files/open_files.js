//todo: could just add an active flag, move selectedText here, del active_text_editor
let viewingFilename = "";
const editor = vscode.window.activeTextEditor;
if (editor) {
    viewingFilename = editor.document.fileName;
}

const openFilesInfo = await Promise.all(
    vscode.window.tabGroups.all
        .flatMap(group => group.tabs)
        .filter(tab => tab.input instanceof vscode.TabInputText)
        //skip viewing file
        .filter(tab => tab.input.uri.fsPath !== viewingFilename)
        .map(async tab => {
            const doc = await vscode.workspace.openTextDocument(tab.input.uri);
            const fsPath = doc.uri.fsPath.replace(/\\/g, '/');
            return {
                path: fsPath,
                filename: fsPath.split('/').pop(),
                language: doc.languageId,
                code: doc.getText()
            };
        })
);

const response = {
    status: 'success',
    result: openFilesInfo
};
return response;