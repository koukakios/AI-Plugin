package com.koukakios.intellijassist

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.content.ContentFactory

class IntelliAssistToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val label = JBLabel("IntelliAssist AI is ready. Select code and run Explain Selection.")
        val content = ContentFactory.SERVICE.getInstance().createContent(label, "Overview", false)
        toolWindow.contentManager.addContent(content)
    }
}
