function isBinaryFile(filePath) {
    const binaryExtensions = [
        '.class', '.exe', '.dll', '.png', '.jpg', '.gif', '.pdf', '.zip', '.tar',
        '.gz', '.7z', '.mp3', '.mp4', '.avi', '.mov'
    ];
    const ext = filePath.slice(filePath.lastIndexOf('.')).toLowerCase();
    return binaryExtensions.includes(ext);
}
