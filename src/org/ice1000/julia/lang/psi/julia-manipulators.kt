package org.ice1000.julia.lang.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import org.ice1000.julia.lang.JuliaTokenType

class JuliaStringManipulator : AbstractElementManipulator<JuliaString>() {
	override fun handleContentChange(psi: JuliaString, range: TextRange, new: String): JuliaString {
		val after = JuliaTokenType.fromText(new, psi.project) as? JuliaString ?: return psi
		psi.replace(after)
		return after
	}
}

class JuliaRegexManipulator : AbstractElementManipulator<JuliaRegex>() {
	override fun handleContentChange(psi: JuliaRegex, range: TextRange, new: String): JuliaRegex {
		val after = JuliaTokenType.fromText(new, psi.project) as? JuliaRegex ?: return psi
		psi.replace(after)
		return after
	}
}
