const files = await vscode.workspace.findFiles('**/*');
const namesAndType = await Promise.all(files.map(async (file) => {
    const path = file.path;
    const type = (await vscode.workspace.fs.stat(file)).type;
    const typeString = type === vscode.FileType.Directory ? 'directory' : 'file';
    return {path, type: typeString};
}));

// Remove .class files
let i = namesAndType.length;
while (i--) {
    if (namesAndType[i].path.includes('.class')) {
        namesAndType.splice(i, 1);
    }
}

namesAndType.sort((a, b) => a.path.localeCompare(b.path));

const response = {
    status: 'success',
    result: namesAndType
};
return response;