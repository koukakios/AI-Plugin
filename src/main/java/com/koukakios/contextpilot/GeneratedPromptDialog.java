package com.koukakios.contextpilot;

import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;
import javax.swing.BorderFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

/**
 * Dialog for previewing and copying the generated AI prompt.
 *
 * The dialog shows project/file metadata, lets the user choose a prompt mode,
 * previews the generated prompt, and provides a copy-to-clipboard action.
 */
public class GeneratedPromptDialog extends DialogWrapper {

    private final CodeContext context;
    private final PromptBuilder promptBuilder;

    private JTextArea promptArea;
    private JComboBox<PromptMode> modeComboBox;

    public GeneratedPromptDialog(@Nullable Project project,
                                 CodeContext context,
                                 PromptBuilder promptBuilder) {
        super(project);
        this.context = context;
        this.promptBuilder = promptBuilder;

        setTitle("ContextPilot");
        init();
    }

    /**
     * Creates the main dialog UI: header, metadata section, and prompt preview area.
     */
    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBorder(JBUI.Borders.empty(12));
        rootPanel.setPreferredSize(new Dimension(850, 550));

        JPanel headerPanel = createHeaderPanel();
        JPanel metadataPanel = createMetadataPanel();

        promptArea = new JTextArea();
        promptArea.setEditable(false);
        promptArea.setLineWrap(true);
        promptArea.setWrapStyleWord(true);
        promptArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        promptArea.setMargin(JBUI.insets(10));
        promptArea.setOpaque(true);

        JBScrollPane scrollPane = new JBScrollPane(promptArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Generated prompt"));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(metadataPanel, BorderLayout.CENTER);

        rootPanel.add(topPanel, BorderLayout.NORTH);
        rootPanel.add(scrollPane, BorderLayout.CENTER);

        updatePrompt();

        return rootPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(JBUI.Borders.emptyBottom(10));

        JLabel titleLabel = new JLabel("Generate AI-ready context from selected code");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));

        modeComboBox = new JComboBox<>(PromptMode.values());
        modeComboBox.addActionListener(event -> updatePrompt());

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(modeComboBox, BorderLayout.EAST);

        return panel;
    }

    private JPanel createMetadataPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setBorder(JBUI.Borders.emptyBottom(10));

        panel.add(new JLabel("<html><b>Project:</b> " + context.getProjectName() + "</html>"));
        panel.add(new JLabel("<html><b>File:</b> " + context.getFilePath() + "</html>"));
        panel.add(new JLabel("<html><b>Language:</b> " + context.getLanguage() + "</html>"));

        return panel;
    }


    /**
     * Rebuilds the prompt whenever the selected prompt mode changes.
     */
    private void updatePrompt() {
        if (promptArea == null || modeComboBox == null) {
            return;
        }

        PromptMode selectedMode = (PromptMode) modeComboBox.getSelectedItem();

        if (selectedMode == null) {
            selectedMode = PromptMode.EXPLAIN;
        }

        String prompt = promptBuilder.buildPrompt(context, selectedMode);
        promptArea.setText(prompt);
        promptArea.setCaretPosition(0);
    }

    /**
     * Creates dialog buttons, including the custom copy-to-clipboard action.
     */
    @Override
    protected Action[] createActions() {
        Action copyAction = new AbstractAction("Copy Prompt") {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                String prompt = promptArea.getText();

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