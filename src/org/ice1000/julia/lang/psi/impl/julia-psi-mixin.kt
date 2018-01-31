package org.ice1000.julia.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import com.intellij.psi.impl.source.tree.injected.StringLiteralEscaper
import org.ice1000.julia.lang.JuliaTokenType
import org.ice1000.julia.lang.psi.JuliaStringContent

abstract class JuliaStringContentMixin(astNode: ASTNode) : ASTWrapperPsiElement(astNode), JuliaStringContent {
	override fun isValidHost() = true
	override fun createLiteralTextEscaper() = StringLiteralEscaper(this)
	override fun updateText(s: String) = replace(JuliaTokenType.fromText(s, project)) as JuliaStringContentMixin
}

class JuliaStringManipulator : AbstractElementManipulator<JuliaStringContentMixin>() {
	override fun getRangeInElement(element: JuliaStringContentMixin) = TextRange(0, element.textLength)
	override fun handleContentChange(
		psi: JuliaStringContentMixin,
		range: TextRange,
		new: String): JuliaStringContentMixin {
		val oldText = psi.text
		return psi.updateText("${oldText.substring(0, range.startOffset)}$new${oldText.substring(range.endOffset)}")
	}
}
