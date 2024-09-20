<img src='.github/media/logo-horizontal-text.svg' width='275'>

[![Version](https://img.shields.io/jetbrains/plugin/v/23086-voqal-assistant.svg)](https://plugins.jetbrains.com/plugin/23086-voqal-assistant)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/23086-voqal-assistant.svg)](https://plugins.jetbrains.com/plugin/23086-voqal-assistant)
[![Discord](https://img.shields.io/discord/1171831108313301034)](https://discord.gg/KgTkR5Rffz)

## Introduction

<!-- Plugin description -->

Voqal is a programming assistant built for software developers looking to enhance their productivity with natural speech
programming. Using Voqal, you can navigate, write, run, and debug software in JetBrains IDEs using your voice. Write
code faster, reduce repetitive strain injuries, and improve focus and productivity. Voqal
is [promptable](https://docs.voqal.dev/directing/overview) and [privacy-focused](https://docs.voqal.dev/privacy),
allowing you to customize your experience and control your data.

<!-- Plugin description end -->

## Demonstration

<video src="https://github.com/user-attachments/assets/f9d93199-adc6-4081-8b33-daf9c7dc173b"></video>

## Code Structure

The codebase is structured as follows:

- `assistant` internal logic which controls the assistant
    - `context` data used to populate the prompts sent to the LLM
    - `memory` holds the assistant's memory
    - `processing` logic used to parse LLMs responses
    - `template` extension functions for templating prompts
    - `tool` contains the tools the assistant uses to interact with the IDE
- `ide` contains IDE-specific implementations
- `provider` interfaces with the supported AI providers
- `services` contains the services used to interact with the assistant
- `status` holds the status of the assistant
- `utils` various utility functions

## Benchmarks

The following benchmarks were produced via these [suites](./src/test/kotlin/benchmark/suites).

|                                                 Idle Mode                                                  |                                            Edit Mode                                             |
|:----------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------:|
| ![](.github/media/benchmark/idle_dark.svg#gh-dark-mode-only)![](.github/media/idle.svg#gh-light-mode-only) | ![](.github/media/edit_dark.svg#gh-dark-mode-only)![](.github/media/edit.svg#gh-light-mode-only) |

> Legend: X-axis: Time (ms), Y-axis: Accuracy (%), Size: Cost ($)

### Commentary

The benchmarks show that the Voqal Assistant works best with `Meta-Llama-3.1-405B-Instruct-Turbo` in Edit Mode
and `gemini-1.5-flash-latest` in Idle Mode. Further work also needs to be done to improve the performance
of the unified diff format-based editing as diff-based editing enables faster and more cost-effective editing.
Diff-based editing is currently disabled as it is not yet production-ready.
