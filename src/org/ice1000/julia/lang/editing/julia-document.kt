package org.ice1000.julia.lang.editing

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import org.ice1000.julia.lang.psi.impl.IJuliaFunctionDeclaration
import org.ice1000.julia.lang.psi.impl.docString


class JuliaDocumentProvider : AbstractDocumentationProvider() {

	override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
		val parent = element?.parent
		if (parent is IJuliaFunctionDeclaration) {
			return "function ${parent.toText}\n${parent.docString?.text}"
		}
		return "$element,${element?.text}"
	}
}