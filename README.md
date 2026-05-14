# ContextPilot

ContextPilot is a small IntelliJ Platform plugin that converts selected code and IDE metadata into an AI-ready prompt. The generated prompt can be copied into ChatGPT, Claude, Gemini, or another AI coding assistant.

The MVP is provider-agnostic: it focuses on extracting useful context from the IDE and formatting it into a structured prompt.

## Motivation

AI coding assistants work better when they receive clear, structured context. ContextPilot explores the IDE-specific context extraction part of AI-assisted coding: selected source code, file metadata, project information, and workflow-specific prompt framing.

## Features

- Extracts selected code from the IntelliJ editor
- Captures the project name
- Captures the file path
- Captures the language/file type
- Supports prompt modes:
  - Explain Code
  - Review for Bugs
  - Suggest Refactor
  - Generate Tests
- Shows a scrollable preview dialog
- Copies the generated prompt to the clipboard
- Available from the editor right-click menu

## Demo Flow

1. Select code in the IntelliJ editor.
2. Right click in the editor.
3. Click **Generate AI Context**.
4. Choose a prompt mode.
5. Preview the generated prompt.
6. Copy the prompt.
7. Paste it into an AI assistant.

## How to Run

Requirements:

- JDK 17
- IntelliJ IDEA
- Gradle wrapper

On Windows:

```powershell
.\gradlew.bat runIde
```

This opens a second IntelliJ IDEA instance with the plugin installed.

## Project Structure

```text
AI-Plugin/
|-- build.gradle.kts
|-- settings.gradle.kts
|-- src/main/java/com/koukakios/contextpilot/
|   |-- GenerateContextAction.java
|   |-- CodeContext.java
|   |-- CodeContextExtractor.java
|   |-- PromptBuilder.java
|   |-- PromptMode.java
|   `-- GeneratedPromptDialog.java
`-- src/main/resources/META-INF/plugin.xml
```

## Architecture

- `GenerateContextAction`: IntelliJ action entry point for the editor right-click menu.
- `CodeContextExtractor`: extracts project, editor, file, and selected-code context using IntelliJ APIs.
- `CodeContext`: plain immutable data holder for extracted IDE context.
- `PromptBuilder`: builds structured prompts from code context and the selected prompt mode.
- `PromptMode`: enum representing the supported AI coding workflows.
- `GeneratedPromptDialog`: Swing UI for previewing and copying generated prompts.

## Design Decisions

- Java was used instead of Kotlin because I wanted a readable implementation under a short time constraint.
- No LLM API call is included in the MVP because the goal is provider-agnostic IDE context extraction and prompt construction.
- Prompt modes represent different AI coding workflows rather than a single generic prompt.
- A dialog UI was chosen because it is simple, testable, and enough for an MVP.

## Limitations

- Only selected code is included.
- No surrounding method/class context yet.
- No API integration yet.
- No prompt history.
- No settings page.
- No automated tests yet.

## Future Work

- Include surrounding method/class context using IntelliJ PSI.
- Include imports and package declarations.
- Add configurable prompt templates.
- Add optional AI provider integration.
- Add a persistent tool window or prompt history.
- Add tests for prompt generation.
