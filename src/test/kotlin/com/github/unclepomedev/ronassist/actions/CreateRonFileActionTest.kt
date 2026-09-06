package com.github.unclepomedev.ronassist.actions

import com.github.unclepomedev.ronassist.icons.RonIcons
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class CreateRonFileActionTest : BasePlatformTestCase() {

    fun testActionProperties() {
        val action = CreateRonFileAction()
        val templatePresentation = action.templatePresentation
        assertEquals("RON File", templatePresentation.text)
        assertEquals("Create a new RON file", templatePresentation.description)
        assertEquals(RonIcons.FILE, templatePresentation.icon)
    }

    fun testGetActionName() {
        val action = CreateRonFileAction()
        val dir = myFixture.psiManager.findDirectory(myFixture.tempDirFixture.getFile(".")!!)!!
        val method: Method =
            CreateRonFileAction::class
                .java
                .getDeclaredMethod(
                    "getActionName",
                    PsiDirectory::class.java,
                    String::class.java,
                    String::class.java,
                )
        method.isAccessible = true
        val result = method.invoke(action, dir, "sample", "RON File")
        assertEquals("Create RON File", result)
    }

    fun testBuildDialog() {
        val action = CreateRonFileAction()
        val dir = myFixture.psiManager.findDirectory(myFixture.tempDirFixture.getFile(".")!!)!!

        val method: Method =
            CreateRonFileAction::class
                .java
                .getDeclaredMethod(
                    "buildDialog",
                    Project::class.java,
                    PsiDirectory::class.java,
                    CreateFileFromTemplateDialog.Builder::class.java,
                )
        method.isAccessible = true

        var capturedTitle: String? = null
        var capturedKind: String? = null
        var capturedTemplate: String? = null

        val invocationHandler = InvocationHandler { proxy, calledMethod, args ->
            when (calledMethod.name) {
                "setTitle" -> {
                    capturedTitle = args[0] as String
                    proxy
                }
                "addKind" -> {
                    capturedKind = args[0] as String
                    capturedTemplate = args[2] as String
                    proxy
                }
                else -> null
            }
        }

        val proxyBuilder =
            Proxy.newProxyInstance(
                CreateFileFromTemplateDialog.Builder::class.java.classLoader,
                arrayOf(CreateFileFromTemplateDialog.Builder::class.java),
                invocationHandler,
            ) as CreateFileFromTemplateDialog.Builder

        method.invoke(action, project, dir, proxyBuilder)

        assertEquals("New RON File", capturedTitle)
        assertEquals("Empty RON file", capturedKind)
        assertEquals("RON File", capturedTemplate)
    }
}
