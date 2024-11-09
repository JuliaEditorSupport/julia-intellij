package org.ice1000.julia.lang.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import org.ice1000.julia.lang.JuliaTokenType

class JuliaStringManipulator : AbstractElementManipulator<JuliaString>() {
	override fun handleContentChange(psi: JuliaString, range: TextRange, new: String): JuliaString {
		val oldText = psi.text
		val newText = "${oldText.substring(0, range.startOffset)}$new${oldText.substring(range.endOffset)}"
		val after = JuliaTokenType.fromText(newText, psi.project) as? JuliaString ?: return psi
		psi.replace(after)
		return after
	}

	override fun getRangeInElement(element: JuliaString) = element.firstChild.textLength.let {
		TextRange(it, element.textLength - it)
	}
}

class JuliaRegexManipulator : AbstractElementManipulator<JuliaRegex>() {
	override fun handleContentChange(psi: JuliaRegex, range: TextRange, new: String): JuliaRegex {
		val oldText = psi.text
		val newText = "${oldText.substring(0, range.startOffset)}$new${oldText.substring(range.endOffset)}"
		val after = JuliaTokenType.fromText(newText, psi.project) as? JuliaRegex ?: return psi
		psi.replace(after)
		return after
	}

	override fun getRangeInElement(element: JuliaRegex) = TextRange(2, element.textLength - 1)
}
