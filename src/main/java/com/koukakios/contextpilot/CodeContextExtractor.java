package com.koukakios.contextpilot;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public class CodeContextExtractor {

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