package com.koukakios.intellijassist

object PromptTemplates {
    fun explainSelectionPrompt(selection: String): String {
        return """
            You are an expert developer assisting a JetBrains IDE user.
            Explain the selected code clearly and concisely.
            Mention the purpose of the code, main control flow, inputs, outputs, and any assumptions.

            Selected code:
            $selection
        """.trimIndent()
    }

    fun refactorSelectionPrompt(selection: String): String {
        return """
            You are an expert developer reviewing code for readability and maintainability.
            Suggest practical refactorings for the selected code. Focus on simplification, naming, and structure.

            Selected code:
            $selection
        """.trimIndent()
    }

    fun bugCheckSelectionPrompt(selection: String): String {
        return """
            You are a careful code reviewer.
            Inspect the selected code for likely bugs, edge cases, and unsafe assumptions.
            List any potential issues and explain why they matter.

            Selected code:
            $selection
        """.trimIndent()
    }

    fun testCaseSuggestionPrompt(selection: String): String {
        return """
            You are generating test ideas for a developer.
            Read the selected code and propose useful test cases, including happy-path and edge-case scenarios.

            Selected code:
            $selection
        """.trimIndent()
    }
}
