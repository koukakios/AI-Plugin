# IntelliAssist AI

A work-in-progress IntelliJ Platform plugin prototype for AI-assisted code reasoning inside JetBrains IDEs.

This repository is intended to demonstrate an early plugin architecture and developer tooling, not a finished production product. It shows how selected editor text can flow through prompt templates, a configurable AI client abstraction, and lightweight UI integration.

> Status: Early-stage prototype. This repository contains an initial IntelliJ plugin skeleton, a selected-code explain action, prompt templates, a mock AI client, and a basic tool window.

---

## What this project includes

- Kotlin-based IntelliJ plugin skeleton using Gradle Kotlin DSL
- `ExplainSelectionAction` for selected text processing
- `PromptTemplates.kt` for explain/refactor/bug-check/test-case prompts
- `AiClient` abstraction and `MockAiClient` implementation
- `plugin.xml` registration and a simple tool window factory
- A clear work-in-progress README and repository structure

---

## Why this matters

The plugin is designed to support workflows where developers want:

- quick natural-language explanations of selected code
- refactoring ideas for confusing logic
- early bug and edge-case checks
- suggested test cases based on code intent

The current version is intentionally honest about its prototype status.

---

## Repository structure

- `build.gradle.kts` — Gradle Kotlin DSL build setup
- `settings.gradle.kts` — root project name
- `plugin.xml` — IntelliJ plugin metadata and registrations
- `src/main/kotlin/` — plugin Kotlin source files
- `src/main/resources/` — plugin metadata resources

---

## Getting started

1. Open the project in IntelliJ IDEA with the Gradle Kotlin DSL files.
2. Use the IntelliJ Gradle tool window to import the project.
3. Run the plugin from the IDE using the `runIde` Gradle task.

> Note: This is a prototype skeleton. The plugin currently uses `MockAiClient` and does not connect to an actual AI provider.

---

## Next steps

Possible future improvements include:

- real AI backend integration
- safe context filtering for selected code
- richer UI and output panels
- explanations for different languages and file types
- unit tests for prompt generation and action behavior

