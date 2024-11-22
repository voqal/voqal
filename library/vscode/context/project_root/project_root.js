const workspaceFolders = vscode.workspace.workspaceFolders;
if (workspaceFolders && workspaceFolders.length > 0) {
    const rootPath = workspaceFolders[0].uri.fsPath;
    const response = {
        status: 'success',
        result: rootPath
    };
    return response;
} else {
    const response = {
        status: 'error',
        message: 'No workspace folder found'
    };
    return response;
}