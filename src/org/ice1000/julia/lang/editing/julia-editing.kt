package org.ice1000.julia.lang.editing

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import org.ice1000.julia.lang.psi.JuliaTypes

class JuliaBraceMatcher : PairedBraceMatcher {
	private companion object Pairs {
		private val PAIRS = arrayOf(
				BracePair(JuliaTypes.LEFT_BRACKET, JuliaTypes.RIGHT_BRACKET, false),
				BracePair(JuliaTypes.LEFT_B_BRACKET, JuliaTypes.RIGHT_B_BRACKET, false),
				BracePair(JuliaTypes.MODULE_KEYWORD, JuliaTypes.END_KEYWORD, false),
				BracePair(JuliaTypes.FUNCTION_KEYWORD, JuliaTypes.END_KEYWORD, false),
				BracePair(JuliaTypes.IF_KEYWORD, JuliaTypes.END_KEYWORD, false),
				BracePair(JuliaTypes.WHILE_KEYWORD, JuliaTypes.END_KEYWORD, false),
				BracePair(JuliaTypes.FOR_KEYWORD, JuliaTypes.END_KEYWORD, false)
		)
	}

	override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int) = openingBraceOffset
	override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?) = true
	override fun getPairs() = PAIRS
}
