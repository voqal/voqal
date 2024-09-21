<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Voqal Changelog

## [Unreleased]

### Added

- Cerebras language model provider
- Stream audio setting to Picovoice/Deepgram STT providers
- Open files to edit mode prompt

### Fixed

- Range highlighter tree assertion error
- Invalid indent when completion is longer than edit range

## [2024.11.3] - 2024-09-14

### Added

- SambaNova language model provider

## [2024.11.2] - 2024-09-13

### Fixed

- Disallow quick edit without language model provider
- Properly handle extracting code with "\n" in strings
- Invalid text modification due to incorrect diff offsets
- Invalid text modification due to rename processor auto removing imports
- Invalid text modification when streaming edit last line is empty

## [2024.11.1] - 2024-09-12

### Fixed

- Opening quick edit inlay with one already open causes un-closable inlay
- Memory leak detected warning in UserDirectiveTextArea
- Always allow audio capture while testing config
- No such element exception when using non-existent language model
- EDT threading issues

## [2024.11.0] - 2024-09-11

### Added

- Conversational quick edit inlay
- `None` voice detection provider

### Fixed

- Incorrect text modification when stream editing exact edit range
- Compute icons coloring in light theme

### Removed

- Voqal mode caret

## [2024.10.3] - 2024-09-11

### Fixed

- Smart chunking in chunk text extension erroneously disabled
- Invalid text modification when stream completion contains single line
- Invalid text modification when stream completion adds text above visible range

## [2024.10.2] - 2024-09-09

### Changed

- Display error messages in chat tool window
- Replace logs tab UI JTextPane with OpenAPI Editor

### Fixed

- Revert smart rename affected files on cancel
- Inconsistent spacing in configuration UI
- Invalid revision when cancelling edit mode without changes
- Set prompt library defaults on first language model added
- Unable to cancel changes with active editor closed
- Listener already registered warning when using Voqal VAD
- Invalid argument error when choosing Picovoice VAD

## [2024.10.1] - 2024-09-05

### Fixed

- Incorrect text placement when editing blank files

## [2024.10.0] - 2024-09-04

### Added

- Streaming completions to `Edit Mode`
- Groq distil-whisper-large-v3-en model

### Changed

- Upgrade Picovoice Orca to 1.0.0

## [2024.9.1] - 2024-08-22

### Fixed

- Marketplace version not updating
- Error on microphone line unsupported
- Improved code block extraction for GPT-4o mini
- Issue reverting code changes in RustRover

## [2024.9.0] - 2024-08-12

### Added

- Viewing code problems to `Idle Mode`
- Code smell correction to `Edit Mode`
- `Move Class` Voqal tool
- `Goto Text` Voqal tool
- Rate limit retry loop

### Changed

- Open-source distribution
- Upgraded to IntelliJ Platform Gradle Plugin 2.0.0
- Default to including code with line numbers in prompts

### Removed

- Microphone audio streaming
- Intent detection providers
- Error reporting
- Telemetry data collection
- Trial mode
- EAP distributions
- Proguard obfuscation
- WebXR client support

## [2024.8.4] - 2024-07-22

### Changed

- Improved edit mode directive shortcut

### Fixed

- Infinite loop in `chunkText()` on empty files
- Exclude VCS ignored files from context prompt
- Serenade intent detection configuration preview
- MistralAI token limit defaults

## [2024.8.3] - 2024-06-28

### Added

- Fireworks AI language model provider
- Groq Whisper speech-to-text provider
- Speech-to-text language setting

### Fixed

- Preserve tab spaced indentation in edit mode
- Groq LLM token limit defaults
- Support multiple projects open simultaneously

## [2024.8.2] - 2024-06-24

### Added

- `addUserContext()` context extension function
- `getUserContext()` context extension function
- `slurpUrl()` context extension function

### Changed

- Set Anthropic default model to `claude-3-5-sonnet-20240620`

### Fixed

- Invalid role error in Anthropic language model provider
- Invalid range error in `chunkText()` context extension function

## [2024.8.1] - 2024-06-21

### Added

- `chunkText()` context extension function
- Voice probability indicator in Picovoice VAD settings
- DeepSeek language model provider

### Changed

- Edit mode default to edit chunk size of 200 lines

### Fixed

- Picovoice VAD provider default configuration
- Incorrect indentation edits in edit mode

## [2024.8.0] - 2024-06-13

### Added

- Voqal Idle Mode
- Directive decomposition
- Vertex AI multimodal provider
- Google API multimodal provider
- Anthropic language model provider
- Custom language model provider
- Project file structure to idle mode context
- Custom URL prompt template provider
- Chat window debug messages
- `View Source` Voqal tool
- `Change Theme` Voqal tool
- `Clear Chat` Voqal tool
- `Ignore` Voqal tool

### Changed

- Pause microphone stream on IDE focus loss
- Voqal intents are now Voqal tools
- Upgrade Picovoice Orca to 0.2.0
- Prevent configuration changes in trial mode
- Create file & create class tools auto-start edit mode

### Removed

- Voqal Command/Focus/Dictate Modes
- Wake word detection providers

## [2024.7.0] - 2024-05-21

### Added

- Enhanced code mode
- Enhanced dictation mode
- Focus/Edit mode
- Serenade intent detection provider
- Ollama language model provider
- Increased support for Swift programming language
- `SelectAll` Voqal intent
- `SelectLine` Voqal intent
- `SelectLines` Voqal intent
- `StartOfLine` Voqal intent
- `Unselect` Voqal intent
- `Cancel` Voqal intent
- `Delete` Voqal intent
- `DeleteLines` Voqal intent
- `LooksGood` Voqal intent
- `Tab` Voqal intent
- `PreviousTab` Voqal intent
- `NextTab` Voqal intent

### Changed

- Improved chat tool window
- Ability to set model name per mode
- Ability to see partial mode results
- Ability to set speech silence threshold
- Ability to set LLM temperature
- Ability to set query params for Deepgram STT provider
- Ability to view raw LLM response
- Refactored live transcribe service
- Tool confirm/execution messages

### Fixed

- EDT threading issues
- Redo intent reporting as unavailable too early

### Removed

- Write code tools
- XR tool window
- Code mode keyboard accessibility

## [2024.6.3] - 2024-05-06

### Fixed

- Collection contains no element matching the predicate ([#77](https://github.com/voqal/voqal/issues/77))

## [2024.6.2] - 2024-04-25

### Fixed

- Mode switching failure in non-streaming speech-to-text providers

## [2024.6.1] - 2024-04-05

### Removed

- Automated usage of `InsertText` Voqal tool

### Fixed

- Slow operation error when viewing command prompt

## [2024.6.0] - 2024-04-03

### Added

- Early access distributions
- Prompt library settings
- `Toggle Code Mode` Voqal intent
- `EndOfLine` Voqal intent
- `DeleteLine` Voqal intent
- `InsertText` Voqal tool
- Voqal mode caret icon
- Support for Rust

### Changed

- Default keymapping
- Dictation mode prepends comments in appropriate files
- Improved `RunProgram` Voqal tool
- Renamed `CreateNewClass` Voqal tool to `CreateClass`
- Improved PSI name change propagation

### Fixed

- Empty speech balloons in conversation tab (macOS)

### Removed

- Edit prompt status icon action

## [2024.5.0] - 2024-03-22

### Added

- Voice activity detection settings
- Voqal voice detection provider
- Picovoice voice detection provider
- Language model token limit setting
- Context cropping functionality
- Groq language model provider

### Changed

- Improved error reporting for Helicone observability provider

### Fixed

- Redundant files in command context
- Unsatisfied link error in WebXR client
- Race condition in plugin configuration
- Trace/debug level logging not outputting to log tab
- Empty speech balloons in conversation tab

## [2024.4.1] - 2024-03-18

### Fixed

- Dictation mode does not trigger when using Picovoice intent detection provider

## [2024.4.0] - 2024-03-15

### Added

- `Toggle Dictation Mode` Voqal intent
- Streaming speech-to-text & text-to-speech functionality
- Picovoice streaming speech-to-text & text-to-speech providers
- Deepgram streaming speech-to-text & text-to-speech providers
- Deepgram intent detection provider
- Deepgram wake word detection provider
- Optional organization id setting for OpenAI provider

### Changed

- AI providers pre-initialized on plugin start
- Re-organized plugin configuration settings
- Decreased plugin logs font size
- Improved error handling for network issues

### Fixed

- Add/remove breakpoints not working in non-JVM languages
- Properly close microphone line on configuration change
- Issue causing intents to be triggered on partial matches
- Holding push-to-talk button causing multiple transcriptions
- NPE when using `WriteCode` tools in Python files
- No change on plugin configuration reset

### Known Issues

- Dictation mode does not trigger when using Picovoice intent detection provider

## [2024.3.0] - 2024-03-01

### Added

- WebXR client support
- MistralAI language model provider
- Whisper ASR speech-to-text provider
- Optional data-sharing setting
- Open files to system prompt
- Enable/disable intents feature
- `Add Field` Voqal tool
- 16 new Voqal intents

### Changed

- Logo update
- Improved LLM response parsing

### Fixed

- EDT threading issues

## [2024.2.4] - 2024-02-12

### Fixed

- Plugin crash on Mac (aarch64)

## [2024.2.3] - 2024-02-10

### Added

- Plugin error reporting
- Plugin uninstall feedback form

## [2024.2.2] - 2024-02-03

### Added

- Logs tab for plugin troubleshooting

### Changed

- Intents are now additionally invokable via command

## [2024.2.1] - 2024-01-30

### Fixed

- NoClassDefFoundError in the write code tool

## [2024.2.0] - 2024-01-22

### Added

- Function signature change propagation
- Active microphone configuration

### Fixed

- Issue upgrading Picovoice natives causing IDE crashes

## [2024.1.0] - 2024-01-11

### Added

- Ability to trigger intents (actions without wake words)
- Ability to chat with Voqal via keyboard
- Control language model from status widget
- `Modify Text` Voqal tool
- `Stop Listening` Voqal intent
- `Scroll` Voqal intent
- `Goto Line` Voqal intent
- `Toggle Zen Mode` Voqal intent

### Changed

- Set default OpenAI language model to gpt-4
- Upgrade Picovoice Cobra to 2.0.1
- Upgrade Picovoice Leopard to 2.0.1
- Upgrade Picovoice Porcupine to 3.0.1
- Replace welcome message from voice to chat

### Fixed

- Issue causing audio directly after wake word to be cut off

### Removed

- Closed captioning setting

## [2023.3.1] - 2024-01-03

### Changed

- Updated logo
- Improved LLM response parsing

## [2023.3.0] - 2023-12-26

### Added

- Ability to edit system prompt
- Ability to add custom tools
- Custom wake words
- Hugging Face language model provider
- TogetherAI language model provider
- Helicone observability provider
- `Create New Class` Voqal tool
- `Show Tab` Voqal tool
- `Remove Breakpoint` Voqal tool
- `Play Program` Voqal tool

### Changed

- Improved LLM response parsing
- Reorganized plugin configuration settings
- Changed default wake provider to none
- Increased support for Groovy
- Disabled assistant mode

### Fixed

- Obfuscation bug preventing wake detection
- Push to talk trigger issue

### Removed

- `Unknown Command` Voqal tool

## [2023.2.2] - 2023-12-17

### Changed

- Increased support for multiple code changes per transcription
- Better invalid configuration error messages

### Fixed

- Issue causing configuration changes not to take effect
- Missing pending status triggers

## [2023.2.1] - 2023-12-12

### Fixed

- Picovoice STT provider configuration

## [2023.2.0] - 2023-12-12

### Added

- `Open File` Voqal tool
- `Add Breakpoint` Voqal tool
- Play voice button to TTS settings
- AssemblyAI to Speech-to-Text providers
- Push-to-talk keyboard shortcut (default: CTRL+SHIFT+S)
- Status notification sounds

### Changed

- `Write Code` Voqal tool now uses a PSI-based back buffer

### Fixed

- Erroneous flagging by Windows SmartScreen

## [2023.1.11] - 2023-11-18

### Fixed

- Issue booting Picovoice SST provider

## [2023.1.10] - 2023-11-17

### Changed

- Increased support for macOS

## [2023.1.9] - 2023-11-17

### Changed

- Increased support for macOS
- Use Picovoice as default SST provider

### Fixed

- Issue saving model name configuration changes
- Delayed installation issue

## [2023.1.8] - 2023-11-16

### Added

- Initial release

[Unreleased]: https://github.com/voqal/voqal/compare/v2024.11.3...HEAD
[2024.11.3]: https://github.com/voqal/voqal/compare/v2024.11.2...v2024.11.3
[2024.11.2]: https://github.com/voqal/voqal/compare/v2024.11.1...v2024.11.2
[2024.11.1]: https://github.com/voqal/voqal/compare/v2024.11.0...v2024.11.1
[2024.11.0]: https://github.com/voqal/voqal/compare/v2024.10.3...v2024.11.0
[2024.10.3]: https://github.com/voqal/voqal/compare/v2024.10.2...v2024.10.3
[2024.10.2]: https://github.com/voqal/voqal/compare/v2024.10.1...v2024.10.2
[2024.10.1]: https://github.com/voqal/voqal/compare/v2024.10.0...v2024.10.1
[2024.10.0]: https://github.com/voqal/voqal/compare/v2024.9.1...v2024.10.0
[2024.9.1]: https://github.com/voqal/voqal/compare/v2024.9.0...v2024.9.1
[2024.9.0]: https://github.com/voqal/voqal/compare/v2024.8.4...v2024.9.0
[2024.8.4]: https://github.com/voqal/voqal/compare/v2024.8.3...v2024.8.4
[2024.8.3]: https://github.com/voqal/voqal/compare/v2024.8.2...v2024.8.3
[2024.8.2]: https://github.com/voqal/voqal/compare/v2024.8.1...v2024.8.2
[2024.8.1]: https://github.com/voqal/voqal/compare/v2024.8.0...v2024.8.1
[2024.8.0]: https://github.com/voqal/voqal/compare/v2024.7.0...v2024.8.0
[2024.7.0]: https://github.com/voqal/voqal/compare/v2024.6.3...v2024.7.0
[2024.6.3]: https://github.com/voqal/voqal/compare/v2024.6.2...v2024.6.3
[2024.6.2]: https://github.com/voqal/voqal/compare/v2024.6.1...v2024.6.2
[2024.6.1]: https://github.com/voqal/voqal/compare/v2024.6.0...v2024.6.1
[2024.6.0]: https://github.com/voqal/voqal/compare/v2024.5.0...v2024.6.0
[2024.5.0]: https://github.com/voqal/voqal/compare/v2024.4.1...v2024.5.0
[2024.4.1]: https://github.com/voqal/voqal/compare/v2024.4.0...v2024.4.1
[2024.4.0]: https://github.com/voqal/voqal/compare/v2024.3.0...v2024.4.0
[2024.3.0]: https://github.com/voqal/voqal/compare/v2024.2.4...v2024.3.0
[2024.2.4]: https://github.com/voqal/voqal/compare/v2024.2.3...v2024.2.4
[2024.2.3]: https://github.com/voqal/voqal/compare/v2024.2.2...v2024.2.3
[2024.2.2]: https://github.com/voqal/voqal/compare/v2024.2.1...v2024.2.2
[2024.2.1]: https://github.com/voqal/voqal/compare/v2024.2.0...v2024.2.1
[2024.2.0]: https://github.com/voqal/voqal/compare/v2024.1.0...v2024.2.0
[2024.1.0]: https://github.com/voqal/voqal/compare/v2023.3.1...v2024.1.0
[2023.3.1]: https://github.com/voqal/voqal/compare/v2023.3.0...v2023.3.1
[2023.3.0]: https://github.com/voqal/voqal/compare/v2023.2.2...v2023.3.0
[2023.2.2]: https://github.com/voqal/voqal/compare/v2023.2.1...v2023.2.2
[2023.2.1]: https://github.com/voqal/voqal/compare/v2023.2.0...v2023.2.1
[2023.2.0]: https://github.com/voqal/voqal/compare/v2023.1.11...v2023.2.0
[2023.1.11]: https://github.com/voqal/voqal/compare/v2023.1.10...v2023.1.11
[2023.1.10]: https://github.com/voqal/voqal/compare/v2023.1.9...v2023.1.10
[2023.1.9]: https://github.com/voqal/voqal/compare/v2023.1.8...v2023.1.9
[2023.1.8]: https://github.com/voqal/voqal/commits/v2023.1.8
