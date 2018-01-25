package org.ice1000.julia.lang

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import org.ice1000.julia.lang.psi.JuliaTypes

object JuliaHighlighter : SyntaxHighlighter {
	@JvmField val KEYWORD = TextAttributesKey.createTextAttributesKey("JULIA_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
	private val KEYWORD_KEY = arrayOf(KEYWORD)
	private val KEYWORDS_LIST = listOf(
			JuliaTypes.END_KEYWORD,
			JuliaTypes.MODULE_KEYWORD
	)

	override fun getHighlightingLexer() = JuliaLexerAdapter()
	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		in KEYWORDS_LIST -> KEYWORD_KEY
		else -> emptyArray()
	}
}

class JuliaHighlighterFactory : SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = JuliaHighlighter
}
