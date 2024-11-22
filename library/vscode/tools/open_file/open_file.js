const contextEncoded = JSON.parse('{{ context_encoded }}');
const partialName = contextEncoded.name;
console.log('Searching for file: ' + partialName);

try {
    let fileUri = null;

    // Perform a search for files in the workspace
    const fileUris = await vscode.workspace.findFiles(
        `**/${partialName}*`, // Match partial names or full paths
        null,                 // No exclusion patterns
        10                    // Limit to a reasonable number of matches
    );
    console.log(`Found ${fileUris.length} matching files`);

    // Prioritize exact matches or the closest partial match
    for (const uri of fileUris) {
        if (isBinaryFile(uri.fsPath)) {
            console.log('Skipping binary file:', uri.fsPath)
            continue; // Skip binary files
        }
        if (uri.fsPath.endsWith(partialName)) {
            fileUri = uri; // Prefer exact matches
            break;
        }
        if (!fileUri) {
            fileUri = uri; // Fallback to the first match
        }
    }

    if (!fileUri) {
        throw new Error("No suitable file found.");
    }

    // Open the found file
    const document = await vscode.workspace.openTextDocument(fileUri);
    await vscode.window.showTextDocument(document, true);

    const response = {
        status: 'success',
        result: [] // Optional: Populate with useful file info
    };
    return response;
} catch (error) {
    console.error('Error opening file:', error);
    const response = {
        status: 'error',
        result: {
            message: error.message || 'Error opening file'
        }
    };
    return response;
}