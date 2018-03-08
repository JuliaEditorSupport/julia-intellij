package org.ice1000.julia.lang.editing

import com.github.rjeschke.txtmark.Processor
import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.psi.PsiElement
import com.intellij.util.PlatformUtils
import com.petebevin.markdown.MarkdownProcessor
import org.ice1000.julia.lang.module.ui.JuliaDocumentWindow
import org.ice1000.julia.lang.psi.impl.IJuliaFunctionDeclaration
import org.ice1000.julia.lang.psi.impl.docString
import com.intellij.ui.content.ContentFactory
import org.ice1000.julia.lang.psi.JuliaSymbol


class JuliaDocumentProvider : AbstractDocumentationProvider() {
	override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
		val symbol = element as? JuliaSymbol ?: return null
		if (symbol.isFunctionName) {
			(symbol.parent as IJuliaFunctionDeclaration).let { parent ->
				parent.docString?.text?.let {
					return "function $parent${
					if (PlatformUtils.isGoIde())
						MarkdownProcessor().markdown(it)
					else Processor.process(it)
					}"
				}
			}
		}
		return null
	}
}

class JuliaDocumentWindowImpl : JuliaDocumentWindow(), ToolWindowFactory {
	init {
		textPane.text = "Nothing to show"
	}

	//TODO: change its content like PyCharm Document

	override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
		val content = ContentFactory
			.SERVICE
			.getInstance()
			.createContent(mainPanel, "", false)
		toolWindow.contentManager.addContent(content)
	}
}