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

public class GeneratedPromptDialog extends DialogWrapper {

    private final Project project;
    private final CodeContext context;
    private final PromptBuilder promptBuilder;
    private final GeminiClient aiClient; // We need the client to make the call!

    private JTextArea promptArea;
    private JEditorPane aiResponseArea;
    private JBTabbedPane tabbedPane;
    private JComboBox<PromptMode> modeComboBox;
    private AsyncProcessIcon loadingIcon;

    private String lastAiResponse = "";

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
        panel.setBorder(JBUI.Borders.emptyBottom(15));
        panel.add(new JLabel("<html><b>Project:</b> " + context.getProjectName() + "</html>"));
        panel.add(new JLabel("<html><b>File:</b> " + context.getFilePath() + "</html>"));
        panel.add(new JLabel("<html><b>Language:</b> " + context.getLanguage() + "</html>"));
        return panel;
    }

    @Override
    protected Action @NotNull [] createActions() {
        Action copyAction = new AbstractAction("Copy Answer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!lastAiResponse.isEmpty()) {
                    CopyPasteManager.getInstance().setContents(new StringSelection(lastAiResponse));
                }
            }
        };
        return new Action[]{getOKAction(), getCancelAction(), copyAction};
    }

    // --- THE MAGIC HAPPENS HERE ---
    @Override
    protected void doOKAction() {
        String prompt = getGeneratedPrompt();
        if (prompt == null || prompt.isBlank()) return;

        // 1. Lock the UI and show spinner
        getOKAction().setEnabled(false);
        showLoading(true);

        // 2. Run the network request on a background thread so the IDE doesn't freeze
        Task.Backgroundable task = new Task.Backgroundable(project, "Asking Gemini...", true) {
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

    private void updatePrompt() {
        if (promptArea == null || modeComboBox == null) return;
        PromptMode selectedMode = (PromptMode) modeComboBox.getSelectedItem();
        if (selectedMode == null) selectedMode = PromptMode.EXPLAIN;
        String prompt = promptBuilder.buildPrompt(context, selectedMode);
        promptArea.setText(prompt);
        promptArea.setCaretPosition(0);
    }

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

    public void setAiResponse(String rawMarkdown) {
        this.lastAiResponse = rawMarkdown;
        String html = convertMarkdownToHtml(rawMarkdown);
        aiResponseArea.setText(html);
        aiResponseArea.setCaretPosition(0);
        tabbedPane.setSelectedIndex(1); // Auto-switch to the AI Assistant tab
    }

    public String getGeneratedPrompt() {
        return promptArea.getText();
    }

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