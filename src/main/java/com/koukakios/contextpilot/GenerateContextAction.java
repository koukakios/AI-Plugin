package com.koukakios.contextpilot;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

public class GenerateContextAction extends AnAction {

    private final CodeContextExtractor extractor = new CodeContextExtractor();
    private final PromptBuilder promptBuilder = new PromptBuilder();

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