const videos = Array.from(document.querySelectorAll('ytd-video-renderer'));
const videoData = videos.map(video => {
    const titleElement = video.querySelector('#video-title');
    const title = titleElement?.innerText.trim() || 'No title found';
    const url = titleElement?.href || 'No URL found';
    return { title, url };
});

videoData;