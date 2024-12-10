const files = await vscode.workspace.findFiles(
    '**/*',
    '**/{node_modules,.venv,.idea,target}/**'
);
const namesAndType = await Promise.all(files.map(async (file) => {
    const path = file.path.replace(vscode.workspace.workspaceFolders[0].uri.path + '/', ''); // Relative path
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

// Sort the paths alphabetically
namesAndType.sort((a, b) => a.path.localeCompare(b.path));

// Generate tree structure
const buildTree = (paths) => {
    const tree = {};
    for (const {path, type} of paths) {
        const parts = path.split('/');
        let current = tree;
        for (let i = 0; i < parts.length; i++) {
            const part = parts[i];
            if (!current[part]) {
                current[part] = i === parts.length - 1 && type === 'file' ? null : {};
            }
            current = current[part];
        }
    }
    return tree;
};

const formatTree = (tree, indent = '') => {
    let result = '';
    for (const key of Object.keys(tree).sort()) {
        result += `${indent}├── ${key}\n`;
        if (tree[key] !== null) {
            result += formatTree(tree[key], `${indent}│   `);
        }
    }
    return result;
};

const tree = buildTree(namesAndType);
const treeString = formatTree(tree);

const response = {
    status: 'success',
    result: treeString
};
return response;