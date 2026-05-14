package com.koukakios.contextpilot;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Extracts useful IDE context from the current IntelliJ editor action.
 *
 * This class is the bridge between IntelliJ Platform APIs and the plugin's
 * own data model. It collects the active project, file metadata, language,
 * and currently selected code.
 */
public class CodeContextExtractor {

    /**
     * Extracts context from the current action event.
     *
     * @param event the IntelliJ action event triggered by the user
     * @return a CodeContext if a project, editor, and selected code are available;
     *         otherwise null
     */
    public CodeContext extract(AnActionEvent event) {
        Project project = event.getProject();
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);

        if (project == null || editor == null) {
            return null;
        }

        String selectedCode = editor.getSelectionModel().getSelectedText();

        if (selectedCode == null || selectedCode.isBlank()) {
            return null;
        }

        String projectName = project.getName();
        String filePath = file != null ? file.getPath() : "Unknown file";
        String language = file != null ? file.getFileType().getName() : "Unknown language";

        return new CodeContext(projectName, filePath, language, selectedCode);
    }
}