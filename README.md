# IntelliAssist AI

Early-stage IntelliJ Platform plugin exploring AI-assisted code understanding directly inside JetBrains IDEs.

The goal of this project is to build a lightweight developer assistant that can work with selected code and provide useful explanations, refactoring ideas, bug checks, and test-case suggestions without forcing the developer to leave the editor.

> Status: Work in progress.  
> This repository currently contains the initial plugin structure, editor action flow, prompt design, API-client abstraction, and a basic tool-window prototype. It is not presented as a finished production plugin.

---

## Motivation

Modern IDEs already provide strong static analysis, navigation, and refactoring support. However, developers often still need help with higher-level reasoning tasks such as:

- understanding unfamiliar code quickly,
- identifying possible edge cases,
- generating meaningful unit-test ideas,
- explaining legacy logic,
- receiving refactoring suggestions in natural language.

This project explores how an IntelliJ plugin can combine IDE context with an AI backend to support those workflows.

The focus is not just “calling an AI API”, but designing a clean plugin architecture around:

- selected editor text,
- prompt templates,
- action handlers,
- UI integration,
- future API-provider flexibility,
- safe handling of incomplete or sensitive code context.

---

## Planned Features

### Code Explanation

Select a block of code and request a plain-English explanation.

Example use cases:

- understand a method quickly,
- explain control flow,
- summarize unfamiliar Java/Kotlin code,
- describe the purpose of a class or function.

---

### Refactoring Suggestions

Generate possible improvements for selected code.

Examples:

- simplify nested conditionals,
- suggest clearer naming,
- identify duplicated logic,
- improve separation of concerns,
- recommend smaller functions.

---

### Bug and Edge-Case Checks

Ask the assistant to inspect selected code for likely issues.

Examples:

- null-handling problems,
- boundary cases,
- exception paths,
- off-by-one mistakes,
- unclear assumptions.

---

### Test-Case Ideas

Generate candidate test cases from selected code.

Examples:

- happy-path cases,
- edge cases,
- invalid input cases,
- regression test ideas,
- branch-coverage suggestions.

---

## Current Scope

The current implementation focuses on the plugin foundation:

- IntelliJ Platform plugin setup
- Gradle-based build configuration
- basic editor action for selected text
- prompt-template layer
- AI client abstraction
- mock AI client for local development
- basic tool-window placeholder

The current version does **not** claim to provide a complete AI assistant yet. The purpose of the repository is to show the implementation direction and the engineering structure behind the project.

---

## Architecture

```txt
Editor Selection
      |
      v
IntelliJ Action Handler
      |
      v
Prompt Template Builder
      |
      v
AI Client Interface
      |
      v
Mock / Future Real API Provider
      |
      v
Tool Window / Notification Output
