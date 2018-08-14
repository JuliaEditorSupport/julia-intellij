package org.ice1000.julia.lang.editing

import com.github.rjeschke.txtmark.Processor
import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.psi.PsiElement
import com.intellij.ui.content.ContentFactory
import com.intellij.util.PlatformUtils
import com.petebevin.markdown.MarkdownProcessor
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.module.juliaSettings
import org.ice1000.julia.lang.module.ui.JuliaDocumentWindow
import org.ice1000.julia.lang.module.validateJulia
import org.ice1000.julia.lang.psi.impl.DocStringOwner
import org.ice1000.julia.lang.psi.impl.docString

class JuliaDocumentProvider : AbstractDocumentationProvider() {
	override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
		val symbol = element ?: return null
		val parent = symbol.parent as? DocStringOwner ?: return null
		val name = "<h2>${symbol.text}</h2>"
		val content = parent.docString?.text?.trim('"').let {
			if (PlatformUtils.isGoIde() || ideVersionAfter2017)
				MarkdownProcessor().markdown(it)
			else Processor.process(it)
		}
		return "$name\n$content"
	}
}

class JuliaDocumentWindowImpl : JuliaDocumentWindow(), ToolWindowFactory {
	override fun init(window: ToolWindow) {
		textPane.contentType = "text/html"
		textPane.text = JuliaBundle.message("julia.tool-window.empty")
		// window.title = JuliaBundle.message("julia.tool-window.title")
		super.init(window)
	}

	override fun shouldBeAvailable(project: Project) =
		validateJulia(project.juliaSettings.settings) && super.shouldBeAvailable(project)

	// TODO: change its content like PyCharm Document
	override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
		toolWindow.contentManager.addContent(ContentFactory
			.SERVICE
			.getInstance()
			.createContent(mainPanel,
				JuliaBundle.message("julia.tool-window.title"),
				true))
	}
}

val ideVersionAfter2017
	get() = ApplicationInfoEx.getInstanceEx().majorVersion.toInt() >= 2018
