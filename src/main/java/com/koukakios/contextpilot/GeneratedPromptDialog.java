package com.koukakios.contextpilot;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.AsyncProcessIcon;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

/**
 * Displays the generated prompt and AI response workflow in an IntelliJ dialog.
 *
 * The dialog lets users review generated context, switch prompt modes, send the
 * prompt to Gemini, and copy the returned answer.
 */
public class GeneratedPromptDialog extends DialogWrapper {

    /** IntelliJ project associated with the dialog, if available. */
    private final Project project;
    /** Extracted code context used to build prompts. */
    private final CodeContext context;
    /** Prompt builder used to render mode-specific prompts. */
    private final PromptBuilder promptBuilder;
    /** Gemini client used to request AI responses. */
    private final GeminiClient aiClient;

    /** Text area containing the generated prompt. */
    private JTextArea promptArea;
    /** HTML-capable pane used to render AI responses. */
    private JEditorPane aiResponseArea;
    /** Tab container for prompt preview and AI response views. */
    private JBTabbedPane tabbedPane;
    /** Prompt mode selector shown in the dialog header. */
    private JComboBox<PromptMode> modeComboBox;
    /** Loading indicator displayed while the AI request is running. */
    private AsyncProcessIcon loadingIcon;

    /** Last raw AI response available for copying. */
    private String lastAiResponse = "";

    /**
     * Creates the prompt dialog for the provided project context.
     *
     * @param project IntelliJ project used as the dialog parent
     * @param context extracted IDE context used for prompt generation
     * @param promptBuilder builder that creates prompts for each selected mode
     */
    public GeneratedPromptDialog(@Nullable Project project, CodeContext context, PromptBuilder promptBuilder) {
        super(project);
        this.project = project;
        this.context = context;
        this.promptBuilder = promptBuilder;
        this.aiClient = new GeminiClient(); // Instantiate your client

        setTitle("ContextPilot");
        setOKButtonText("Ask AI");
        init();
    }

    /**
     * Creates the main dialog content panel.
     *
     * @return center panel containing prompt and AI response tabs
     */
    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBorder(JBUI.Borders.empty(15));
        rootPanel.setPreferredSize(new Dimension(900, 650));

        // TAB 1: PROMPT
        promptArea = new JTextArea();
        promptArea.setEditable(false);
        promptArea.setLineWrap(true);
        promptArea.setWrapStyleWord(true);
        promptArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        promptArea.setMargin(JBUI.insets(10));
        JBScrollPane promptScroll = new JBScrollPane(promptArea);

        // TAB 2: AI ASSISTANT
        aiResponseArea = new JEditorPane();
        aiResponseArea.setEditable(false);
        aiResponseArea.setContentType("text/html");
        aiResponseArea.setEditorKit(new HTMLEditorKit());
        aiResponseArea.setText("<html><body style='font-family:sans-serif; padding:10px; color:#888;'><i>Click 'Ask AI' to generate a response...</i></body></html>");
        JBScrollPane aiScroll = new JBScrollPane(aiResponseArea);

        tabbedPane = new JBTabbedPane();
        tabbedPane.addTab("Context Prompt", promptScroll);
        tabbedPane.addTab("AI Assistant", aiScroll);

        JPanel headerAndMetadata = new JPanel(new BorderLayout());
        headerAndMetadata.add(createHeaderPanel(), BorderLayout.NORTH);
        headerAndMetadata.add(createMetadataPanel(), BorderLayout.CENTER);

        // Bottom panel for the loading spinner
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottomPanel.setBorder(JBUI.Borders.emptyTop(5));
        loadingIcon = new AsyncProcessIcon("AI_Loading");
        loadingIcon.setVisible(false);
        bottomPanel.add(loadingIcon);

        rootPanel.add(headerAndMetadata, BorderLayout.NORTH);
        rootPanel.add(tabbedPane, BorderLayout.CENTER);
        rootPanel.add(bottomPanel, BorderLayout.SOUTH);

        updatePrompt();
        return rootPanel;
    }

    /**
     * Creates the title and prompt mode selector header.
     *
     * @return header panel for the dialog
     */
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

    /**
     * Creates the metadata panel that displays project, file, and language context.
     *
     * @return metadata panel for the selected code context
     */
    private JPanel createMetadataPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setBorder(JBUI.Borders.emptyBottom(15));
        panel.add(new JLabel("<html><b>Project:</b> " + context.getProjectName() + "</html>"));
        panel.add(new JLabel("<html><b>File:</b> " + context.getFilePath() + "</html>"));
        panel.add(new JLabel("<html><b>Language:</b> " + context.getLanguage() + "</html>"));
        return panel;
    }

    /**
     * Creates the dialog actions used to request AI output, close the dialog, and copy answers.
     *
     * @return available dialog actions
     */
    @Override
    protected Action @NotNull [] createActions() {
        Action copyAction = new AbstractAction("Copy Answer") {
            /**
             * Copies the most recent AI response to the clipboard when available.
             *
             * @param e action event emitted by the Swing button
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!lastAiResponse.isEmpty()) {
                    CopyPasteManager.getInstance().setContents(new StringSelection(lastAiResponse));
                }
            }
        };
        return new Action[]{getOKAction(), getCancelAction(), copyAction};
    }

    /**
     * Sends the current prompt to Gemini in a background task.
     */
    @Override
    protected void doOKAction() {
        String prompt = getGeneratedPrompt();
        if (prompt == null || prompt.isBlank()) return;

        // 1. Lock the UI and show spinner
        getOKAction().setEnabled(false);
        showLoading(true);

        // 2. Run the network request on a background thread so the IDE doesn't freeze
        Task.Backgroundable task = new Task.Backgroundable(project, "Asking Gemini...", true) {
            /**
             * Performs the Gemini request on a background thread.
             *
             * @param indicator progress indicator for the background task
             */
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    String response = aiClient.askAi(prompt);

                    // 3. Update the UI back on the main Event Dispatch Thread
                    ApplicationManager.getApplication().invokeLater(() -> {
                        setAiResponse(response);
                        showLoading(false);
                        getOKAction().setEnabled(true);
                    });
                } catch (Exception e) {
                    // Handle missing API keys or network errors gracefully
                    ApplicationManager.getApplication().invokeLater(() -> {
                        Messages.showErrorDialog(project, "Failed to get AI response:\n" + e.getMessage(), "Gemini API Error");
                        showLoading(false);
                        getOKAction().setEnabled(true);
                    });
                }
            }
        };

        ProgressManager.getInstance().run(task);
    }

    /**
     * Rebuilds the prompt preview for the currently selected prompt mode.
     */
    private void updatePrompt() {
        if (promptArea == null || modeComboBox == null) return;
        PromptMode selectedMode = (PromptMode) modeComboBox.getSelectedItem();
        if (selectedMode == null) selectedMode = PromptMode.EXPLAIN;
        String prompt = promptBuilder.buildPrompt(context, selectedMode);
        promptArea.setText(prompt);
        promptArea.setCaretPosition(0);
    }

    /**
     * Shows or hides the loading indicator.
     *
     * @param isLoading true to show and resume the loading indicator; false to hide and suspend it
     */
    public void showLoading(boolean isLoading) {
        if (loadingIcon != null) {
            loadingIcon.setVisible(isLoading);
            if (isLoading) {
                loadingIcon.resume();
            } else {
                loadingIcon.suspend();
            }
        }
    }

    /**
     * Stores and renders an AI response, then switches to the response tab.
     *
     * @param rawMarkdown raw Markdown response returned by the AI provider
     */
    public void setAiResponse(String rawMarkdown) {
        this.lastAiResponse = rawMarkdown;
        String html = convertMarkdownToHtml(rawMarkdown);
        aiResponseArea.setText(html);
        aiResponseArea.setCaretPosition(0);
        tabbedPane.setSelectedIndex(1); // Auto-switch to the AI Assistant tab
    }

    /**
     * Returns the currently generated prompt text.
     *
     * @return generated prompt shown in the prompt preview area
     */
    public String getGeneratedPrompt() {
        return promptArea.getText();
    }

    /**
     * Converts a small Markdown subset into HTML suitable for the Swing response pane.
     *
     * @param markdown raw Markdown text returned by the AI provider
     * @return HTML document string for rendering in the dialog
     */
    private String convertMarkdownToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) return "";

        String html = markdown.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");

        html = html.replaceAll("(?s)```(.*?)\n(.*?)```", "<pre style='background-color:#2b2b2b; color:#a9b7c6; padding:10px; border-radius:5px;'><code>$2</code></pre>");
        html = html.replaceAll("`(.*?)`", "<code style='background-color:#2b2b2b; color:#a9b7c6; padding:2px 4px; border-radius:3px;'>$1</code>");
        html = html.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");
        html = html.replaceAll("\\*(.*?)\\*", "<i>$1</i>");
        html = html.replace("\n", "<br>");

        return "<html><body style='font-family:sans-serif; padding:10px;'>" + html + "</body></html>";
    }
}
