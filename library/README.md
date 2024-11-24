The Voqal Library is organized by application and service, with each containing a set of contexts
and tools that can be used to interact with the application or service. Contexts provide information about the current
state of the application or service, while tools provide the ability to perform actions within the application or
service.

## YouTube

### Context

- [Current Time](./youtube/context/current_time) - The time at which the video is currently playing
- [Video Duration](./youtube/context/video_duration) - The total duration of the video

### Tools

- [Next Video](./youtube/tools/next_video) - Play the next video in the playlist
- [Previous Video](./youtube/tools/previous_video) - Play the previous video in the playlist
- [Search](./youtube/tools/search) - Search for a video
- [Seek Video](./youtube/tools/seek_video) - Move to a specific time in the video
