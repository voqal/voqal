const context = JSON.parse(atob('{{ context_base64 }}'));
const partialName = context.name.replace(/\\/g, '/');
console.log('Closing file: ' + partialName);

try {
    // Collect all open text tabs
    const openTextTabs = vscode.window.tabGroups.all
        .flatMap(group => group.tabs)
        .filter(tab => tab.input instanceof vscode.TabInputText);

    // Search for a tab to close by exact or partial name match
    const tabToClose = openTextTabs.find(tab => {
        const doc = tab.input;
        const tabPath = doc.uri.fsPath.replace(/\\/g, '/');

        // Exact match
        if (tabPath === partialName) return true;

        // Fuzzy match: check if the partial name is part of the full path
        const partialNameLower = partialName.toLowerCase();
        return tabPath.toLowerCase().includes(partialNameLower);
    });

    if (tabToClose) {
        // Close the found tab
        await vscode.window.tabGroups.close(tabToClose);
        console.log('Closed file:', tabToClose.input.uri.fsPath);

        const response = {
            status: 'success',
            result: null
        };
        return response;
    } else {
        throw new Error('File not open or not a text file.');
    }
} catch (error) {
    console.error('Error closing file:', error.message);

    const response = {
        status: 'error',
        result: {
            message: error.message
        }
    };
    return response;
}