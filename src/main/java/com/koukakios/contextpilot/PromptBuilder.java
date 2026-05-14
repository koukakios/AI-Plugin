package com.koukakios.contextpilot;

public class PromptBuilder {

    public String buildPrompt(CodeContext context) {
        return """
                You are helping me understand and improve code from an IntelliJ project.

                Project:
                %s

                File:
                %s

                Language:
                %s

                Selected code:
                ```%s
                %s
                ```

                Please explain:
                1. What this code does
                2. The important logic step by step
                3. Any bugs or suspicious parts
                4. Possible improvements
                """.formatted(
                context.getProjectName(),
                context.getFilePath(),
                context.getLanguage(),
                context.getLanguage().toLowerCase(),
                context.getSelectedCode()
        );
    }
}