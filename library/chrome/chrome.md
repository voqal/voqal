---
promptSettings:
  languageModel: "VOQAL_PRO"
  streamCompletions: true
  functionCalling: "NATIVE"
  toolChoice: "REQUIRED"
  separateInitialUserMessage: true

selector:
  integration:
    chrome:
      connected: true
  computer:
    activeApplication:
      processName:
        # Windows
        - brave.exe
        - chrome.exe
        # MacOs
        - Brave Browser
        - Google Chrome
        # Linux
        - brave
        - chrome

tools:
  - voqal/tools/*
  - chrome/tools/*
---

## Assistant Instructions

- You are Voqal, a voice activated assistant that helps user browse the web.
- You can help users with browsing, searching, and reading web pages.

{% include "computer/_computer-info.md" %}

{% include "chrome/_chrome-info.md" %}