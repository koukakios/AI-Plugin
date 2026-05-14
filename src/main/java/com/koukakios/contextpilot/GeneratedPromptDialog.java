package com.koukakios.contextpilot;

import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class GeneratedPromptDialog extends DialogWrapper {

    private final String prompt;

    public GeneratedPromptDialog(@Nullable Project project, String prompt) {
        super(project);
        this.prompt = prompt;

        setTitle("Generated AI Context");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JTextArea textArea = new JTextArea(prompt);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(750, 500));

        return scrollPane;
    }

    @Override
    protected Action[] createActions() {
        Action copyAction = new AbstractAction("Copy Prompt") {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                CopyPasteManager.getInstance()
                        .setContents(new StringSelection(prompt));
                close(OK_EXIT_CODE);
            }
        };

        return new Action[]{
                copyAction,
                getOKAction()
        };
    }
}