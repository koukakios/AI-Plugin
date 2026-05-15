package com.koukakios.contextpilot;
/**
 * Immutable data holder for the IDE context used to build an AI prompt.
 *
 * This object keeps the extracted project metadata and selected code together,
 * so the prompt-building logic does not need to know anything about IntelliJ APIs.
 */
public class CodeContext {

    /** Name of the active IntelliJ project. */
    private final String projectName;
    /** Path of the file that contains the selected code. */
    private final String filePath;
    /** IntelliJ file type or language name for the selected file. */
    private final String language;
    /** Code selected by the user in the active editor. */
    private final String selectedCode;

    /**
     * Creates an immutable context snapshot for prompt generation.
     *
     * @param projectName name of the active project
     * @param filePath path of the selected file
     * @param language language or file type of the selected file
     * @param selectedCode code selected in the editor
     */
    public CodeContext(String projectName, String filePath, String language, String selectedCode) {
        this.projectName = projectName;
        this.filePath = filePath;
        this.language = language;
        this.selectedCode = selectedCode;
    }

    /**
     * Returns the active project name.
     *
     * @return project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Returns the selected file path.
     *
     * @return file path or a fallback value when the file is unknown
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Returns the selected file language or file type.
     *
     * @return language or file type name
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Returns the code selected in the editor.
     *
     * @return selected source code
     */
    public String getSelectedCode() {
        return selectedCode;
    }
}
