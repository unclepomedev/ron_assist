package com.github.unclepomedev.ronassist.actions

import com.github.unclepomedev.ronassist.icons.RonIcons
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory

class CreateRonFileAction :
    CreateFileFromTemplateAction(
        "RON File",
        "Create a new RON file",
        RonIcons.FILE,
    ),
    DumbAware {
    override fun buildDialog(
        project: Project,
        directory: PsiDirectory,
        builder: CreateFileFromTemplateDialog.Builder,
    ) {
        builder.setTitle("New RON File").addKind("Empty RON file", RonIcons.FILE, "RON File")
    }

    override fun getActionName(
        directory: PsiDirectory,
        newName: String,
        templateName: String,
    ): String = "Create RON File"
}
