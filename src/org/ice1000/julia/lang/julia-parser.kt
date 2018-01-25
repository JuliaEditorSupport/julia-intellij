package org.ice1000.julia.lang

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.julia.lang.psi.JuliaTypes

class JuliaTokenType(debugName: String) : IElementType(debugName, JuliaLanguage) {
	companion object {
		@JvmField val COMMENTS = TokenSet.create(JuliaTypes.COMMENT)
		@JvmField val STRINGS = TokenSet.create(JuliaTypes.STR)
	}
}

class JuliaElementType(debugName: String) : IElementType(debugName, JuliaLanguage)
