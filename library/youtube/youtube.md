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
      activeTab:
        host: www.youtube.com

tools:
  - default/tools/*
  - chrome/tools/*
  - youtube/tools/*
---

# Assistant Instructions

- Todo

## Current Playback

- Current timestamp: {{ library.youtube.current_time }} seconds

{% set segments = slurpUrl("https://sponsor.ajay.app/api/skipSegments?videoID=" + integration.chrome.activeTab.urlParams.v, "5m") %}
{% if segments is not null and segments.size > 0 %}

## Video Sponsor Segments

{% for segment in segments %}

- A {{ segment.category }} segment starts at {{ segment.segment[0] | numberformat("#.##") }} for a duration of
  {{ (segment.segment[1] - segment.segment[0]) | numberformat("#.##") }} seconds

{% endfor %}

{% endif %}
