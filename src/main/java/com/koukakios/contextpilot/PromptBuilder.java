package com.koukakios.contextpilot;

public class PromptBuilder {

    public String buildPrompt(CodeContext context) {
        return """
                You are an AI coding assistant helping with code inside an IntelliJ project.

                IDE context:
                - Project: %s
                - File: %s
                - Language: %s

                Selected code:
                ```%s
                %s
                ```

                Task:
                Explain the selected code clearly.

                Please include:
                1. What the code does
                2. The important logic step by step
                3. Any bugs, suspicious parts, or edge cases
                4. Suggestions for improving readability, correctness, or maintainability
                """.formatted(
                context.getProjectName(),
                context.getFilePath(),
                context.getLanguage(),
                context.getLanguage().toLowerCase(),
                context.getSelectedCode()
        );
    }
}