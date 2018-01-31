package org.ice1000.julia.lang.psi.impl

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import org.ice1000.julia.lang.psi.JuliaStringContent

class JuliaStringManipulator : AbstractElementManipulator<JuliaStringContent>() {
	override fun getRangeInElement(element: JuliaStringContent) = TextRange(0, element.textLength)
	override fun handleContentChange(
		psi: JuliaStringContent,
		range: TextRange,
		new: String): JuliaStringContent {
		val oldText = psi.text
		return psi.updateText("${oldText.substring(0, range.startOffset)}$new${oldText.substring(range.endOffset)}")
	}
}
