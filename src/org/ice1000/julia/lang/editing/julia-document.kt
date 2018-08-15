package org.ice1000.julia.lang.editing

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.psi.PsiElement
import com.intellij.ui.content.ContentFactory
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.module.juliaSettings
import org.ice1000.julia.lang.module.ui.JuliaDocumentWindow
import org.ice1000.julia.lang.module.validateJulia
import org.ice1000.julia.lang.psi.impl.DocStringOwner
import org.ice1000.julia.lang.psi.impl.docString
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

class JuliaDocumentProvider : AbstractDocumentationProvider() {
	override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
		val symbol = element ?: return null
		val parent = symbol.parent as? DocStringOwner ?: return null
		val name = "# ${symbol.text}\n"
		return parent.docString?.text?.trim('"')?.let {
			SimplifyJBMarkdownUtil.generateMarkdownHtml(name + it)
		}
	}
}

@Deprecated("Until we need show LaTeX.")
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

/**
 * https://github.com/JetBrains/intellij-plugins/blob/master/markdown/src/org/intellij/plugins/markdown/ui/preview/MarkdownUtil.java
 * @see [org.intellij.plugins.markdown.ui.preview.MarkdownUtil]
 */
object SimplifyJBMarkdownUtil {
	private val FLAVOUR = CommonMarkFlavourDescriptor()

	fun generateMarkdownHtml(text: String): String {
		return try {
			val parsedTree = MarkdownParser(FLAVOUR).buildMarkdownTreeFromString(text)
			HtmlGenerator(text, parsedTree, FLAVOUR).generateHtml()
		} catch (e: Exception) {
			e.printStackTrace()
			"parse error"
		}
	}
}