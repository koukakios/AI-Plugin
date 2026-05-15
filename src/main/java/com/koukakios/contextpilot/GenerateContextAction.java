package com.koukakios.contextpilot;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

/**
 * IntelliJ action entry point for ContextPilot.
 *
 * This action is triggered from the editor popup menu or Search Everywhere.
 * It extracts the selected code context, builds the prompt UI, and shows it
 * to the user.
 */
public class GenerateContextAction extends AnAction {

    /** Extracts editor and project context from the current IntelliJ action event. */
    private final CodeContextExtractor extractor = new CodeContextExtractor();
    /** Builds AI-ready prompts from extracted code context. */
    private final PromptBuilder promptBuilder = new PromptBuilder();

    /**
     * Runs when the user triggers "Generate AI Context".
     *
     * The method coordinates extraction, validation, prompt building, and UI display.
     *
     * @param event IntelliJ action event triggered by the user
     */
    @Override
    public void actionPerformed(AnActionEvent event) {
        CodeContext context = extractor.extract(event);

        if (context == null) {
            Messages.showWarningDialog(
                    "Please open a file and select some code first.",
                    "ContextPilot"
            );
            return;
        }

        GeneratedPromptDialog dialog =
                new GeneratedPromptDialog(event.getProject(), context, promptBuilder);

        dialog.show();
    }
}
