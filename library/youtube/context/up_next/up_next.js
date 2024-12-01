const videos = Array.from(document.querySelectorAll('ytd-compact-video-renderer'));
const videoData = videos.map(video => {
    const titleElement = video.querySelector('#video-title');
    const linkElement = video.querySelector('a[href]');
    const title = titleElement?.innerText.trim() || 'No title found';
    const url = linkElement ? new URL(linkElement.href, 'https://www.youtube.com').href : 'No URL found';
    return { title, url };
});

videoData;