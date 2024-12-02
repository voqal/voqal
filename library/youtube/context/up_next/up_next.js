const videos = Array.from(document.querySelectorAll('ytd-compact-video-renderer'));
const videoData = videos.map(video => {
    const titleElement = video.querySelector('#video-title');
    const linkElement = video.querySelector('a[href]');
    const title = titleElement?.innerText.trim() || 'No title found';
    const url = 'https://www.youtube.com' + linkElement?.getAttribute('href') || 'No url found';
    const id = url.split('v=')[1];
    return { title, url, id };
});

videoData;