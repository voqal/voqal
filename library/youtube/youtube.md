---
promptSettings:
  languageModel: "VOQAL_PRO"
  streamCompletions: true
  functionCalling: "NATIVE"
  separateInitialUserMessage: true

selector:
  computer:
    activeApplication:
      processName:
        - chrome.exe
        - brave.exe
  integration:
    chrome:
      connected: true
      activeTab:
        host: www.youtube.com

tools:
  - voqal/tools/*
  - chrome/tools/*
  - youtube/tools/*
---

# Assistant Instructions

- Todo

{% include "computer/_computer-info.md" %}

{% include "chrome/_chrome-info.md" %}

## Current Playback

- Current timestamp: {{ library.youtube.current_time }} seconds
- Total duration: {{ library.youtube.video_duration }}

{% set segments = slurpUrl("https://sponsor.ajay.app/api/skipSegments?videoID=" + integration.chrome.activeTab.urlParams.v, "5m") %}
{% if segments is not null and segments.size > 0 %}

## Video Sponsor Segments

{% for segment in segments %}

- A {{ segment.category }} segment starts at {{ segment.segment[0] | numberformat("#.##") }} for a duration of
  {{ (segment.segment[1] - segment.segment[0]) | numberformat("#.##") }} seconds

{% endfor %}

{% endif %}

{% if library.youtube.search_results is not empty %}

## Search Results

{% for result in library.youtube.search_results %}

- Title: {{ result.title }} | Id: {{ result.id }}

{% endfor %}

{% endif %}

{% if library.youtube.up_next is not empty %}

## Up Next

> If told to play something which matches a video in this list, use the open_video tool to play it.

{% for result in library.youtube.up_next %}

- Title: {{ result.title }} | Id: {{ result.id }}

{% endfor %}

{% endif %}
