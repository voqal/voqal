console.log('Running file');
const pythonExtension = vscode.extensions.getExtension('ms-python.python');
if (!pythonExtension) {
    console.error('Python extension is not installed');
    const response = {
        status: 'error',
        result: {
            message: 'Python extension is not installed'
        }
    }
    return response;
}

if (!pythonExtension.isActive) {
    await pythonExtension.activate();
}

await vscode.commands.executeCommand('python.execInTerminal');

const response = {
    status: 'success',
    result: null
}
return response;