package com.koukakios.contextpilot;

public class CodeContext {

    private final String projectName;
    private final String filePath;
    private final String language;
    private final String selectedCode;

    public CodeContext(String projectName, String filePath, String language, String selectedCode) {
        this.projectName = projectName;
        this.filePath = filePath;
        this.language = language;
        this.selectedCode = selectedCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getLanguage() {
        return language;
    }

    public String getSelectedCode() {
        return selectedCode;
    }
}