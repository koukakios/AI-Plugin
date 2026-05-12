package com.koukakios.intellijassist

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages

class ExplainSelectionAction : AnAction() {
    private val aiClient: AiClient = MockAiClient()

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val project = e.project

        if (project == null || editor == null) {
            Messages.showInfoMessage("Open an editor and select some code to explain.", "IntelliAssist")
            return
        }

        val selectedText = editor.selectionModel.selectedText
        if (selectedText.isNullOrBlank()) {
            Messages.showInfoMessage("Please select a block of code before using Explain Selection.", "IntelliAssist")
            return
        }

        val prompt = PromptTemplates.explainSelectionPrompt(selectedText.trim())
        val response = aiClient.ask(prompt)

        Messages.showInfoMessage(response, "Explain Selection")
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabled = editor?.selectionModel?.hasSelection() == true
    }
}
