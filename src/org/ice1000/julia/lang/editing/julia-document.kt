package org.ice1000.julia.lang.editing

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import com.petebevin.markdown.MarkdownProcessor
import org.ice1000.julia.lang.psi.impl.IJuliaFunctionDeclaration
import org.ice1000.julia.lang.psi.impl.docString


class JuliaDocumentProvider : AbstractDocumentationProvider() {

	override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
		val parent = element?.parent
		if (parent is IJuliaFunctionDeclaration) {
			return "function ${parent.toText}\n${parent.docString?.text?.let{
				//because GoLand has no Markdown4j but MarkdownJ
//				if (PlatformUtils.isGoIde())
					MarkdownProcessor().markdown(it)
//				else Processor.process(it)
			}}"
		}
		return "$element,${element?.text}"
	}
}