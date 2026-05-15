# ContextPilot

ContextPilot is an ultra-lightweight IntelliJ Platform plugin that turns selected editor code into structured context and can now send that context directly to Gemini for fast explanations, reviews, refactoring suggestions, and test-generation guidance.

This major version moves ContextPilot beyond a copy-only prompt generator. The plugin now includes native Gemini AI integration, a tabbed IDE-native response workflow, asynchronous API calls, and readable rich text rendering inside the dialog.

## What's New

- **Native Gemini AI integration**: ContextPilot connects directly to the Gemini 2.5 Flash API for lightning-fast code explanations, reviews, refactoring suggestions, and generated test guidance.
- **Tabbed AI workflow**: `GeneratedPromptDialog` has been revamped with `JBTabbedPane`, separating the generated **Context Prompt** from the **AI Assistant** response.
- **Async processing**: Gemini requests run through `Task.Backgroundable`, with an `AsyncProcessIcon` spinner to show progress while keeping the IDE responsive during network calls.
- **Rich text rendering**: AI responses are converted from Markdown to HTML for clean in-dialog reading, including formatted inline code and fenced code blocks.
- **Lean Java implementation**: The plugin uses Java 17 and the native `HttpClient` API with zero heavy external dependencies. There is no Gson, Jackson, or additional AI SDK layer.

## Quick Start

### 1. Set Your Gemini API Key

ContextPilot reads the Gemini key from the `GEMINI_API_KEY` system environment variable.

On Windows PowerShell:

```powershell
setx GEMINI_API_KEY "your-api-key-here"
```

Restart IntelliJ IDEA after setting the variable so the IDE process can read the updated environment.

### 2. Run the Plugin

Requirements:

- JDK 17
- IntelliJ IDEA
- Gradle wrapper included in this repository

On Windows:

```powershell
.\gradlew.bat runIde
```

This opens a second IntelliJ IDEA instance with ContextPilot installed.

### 3. Generate AI Context

1. Select code in the IntelliJ editor.
2. Right-click in the editor.
3. Choose **Generate AI Context**.
4. Select a prompt mode.
5. Review the **Context Prompt** tab.
6. Click **Ask AI**.
7. Read the rendered Gemini response in the **AI Assistant** tab.

## Features

- Extracts selected code from the active IntelliJ editor.
- Captures project name, file path, and language metadata.
- Supports focused prompt modes:
  - Explain Code
  - Review for Bugs
  - Suggest Refactor
  - Generate Tests
- Sends structured prompts directly to Gemini 2.5 Flash.
- Displays prompt and response content in separate tabs.
- Keeps the IDE responsive with background execution.
- Renders Markdown responses as HTML for readable output.
- Copies the latest AI response to the clipboard.
- Available from the editor right-click menu.

## Architecture

- `GenerateContextAction`: IntelliJ action entry point for the editor context menu.
- `CodeContextExtractor`: Extracts project, editor, file, language, and selected-code context through IntelliJ APIs.
- `CodeContext`: Immutable holder for extracted IDE context.
- `PromptBuilder`: Builds structured prompts for each selected workflow.
- `PromptMode`: Enum for supported AI coding workflows.
- `GeneratedPromptDialog`: Swing dialog that hosts prompt mode selection, `JBTabbedPane`, the loading spinner, and rendered AI responses.
- `GeminiClient`: Java 17 `HttpClient` integration for the Gemini 2.5 Flash API.

## Design Philosophy

ContextPilot is intentionally small and dependency-light. The plugin favors IntelliJ Platform APIs, Swing components, and standard Java 17 networking over heavyweight frameworks or SDKs.

Key decisions:

- Native Java 17 `HttpClient` for API calls.
- Simple response extraction to avoid large JSON dependencies.
- No Gson or Jackson.
- No external AI SDK dependency.
- IDE-native UI components such as `JBTabbedPane`, `JBScrollPane`, `Task.Backgroundable`, and `AsyncProcessIcon`.
- Focused prompt construction instead of broad project indexing.

## Project Structure

```text
AI-Plugin/
|-- build.gradle.kts
|-- settings.gradle.kts
|-- src/main/java/com/koukakios/contextpilot/
|   |-- AiClient.java
|   |-- CodeContext.java
|   |-- CodeContextExtractor.java
|   |-- GeminiClient.java
|   |-- GenerateContextAction.java
|   |-- GeneratedPromptDialog.java
|   |-- PromptBuilder.java
|   `-- PromptMode.java
`-- src/main/resources/META-INF/plugin.xml
```

## Current Scope

- ContextPilot works with the user's selected code.
- Surrounding method/class context and import expansion are not included yet.
- API configuration is environment-variable based.
- Prompt history and a persistent settings page are not included yet.
