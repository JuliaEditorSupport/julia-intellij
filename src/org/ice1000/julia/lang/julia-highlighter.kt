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
	@JvmField val NUMBER = TextAttributesKey.createTextAttributesKey("JULIA_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
	@JvmField val STRING = TextAttributesKey.createTextAttributesKey("JULIA_STRING", DefaultLanguageHighlighterColors.STRING)
	@JvmField val OPERATOR = TextAttributesKey.createTextAttributesKey("JULIA_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
	@JvmField val COMMENT = TextAttributesKey.createTextAttributesKey("JULIA_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)

	private val KEYWORD_KEY = arrayOf(KEYWORD)
	private val STRING_KEY = arrayOf(STRING)
	private val NUMBER_KEY = arrayOf(NUMBER)
	private val OPERATOR_KEY = arrayOf(OPERATOR)
	private val COMMENT_KEY = arrayOf(COMMENT)

	private val KEYWORDS_LIST = listOf(
			JuliaTypes.END_KEYWORD,
			JuliaTypes.MODULE_KEYWORD,
			JuliaTypes.BAREMODULE_KEYWORD,
			JuliaTypes.BREAK_KEYWORD,
			JuliaTypes.CONTINUE_KEYWORD,
			JuliaTypes.INCLUDE_KEYWORD,
			JuliaTypes.EXPORT_KEYWORD,
			JuliaTypes.IMPORT_KEYWORD,
			JuliaTypes.USING_KEYWORD,
			JuliaTypes.IF_KEYWORD,
			JuliaTypes.ELSEIF_KEYWORD,
			JuliaTypes.ELSE_KEYWORD,
			JuliaTypes.FOR_KEYWORD,
			JuliaTypes.WHILE_KEYWORD,
			JuliaTypes.IN_KEYWORD,
			JuliaTypes.RETURN_KEYWORD,
			JuliaTypes.TRY_KEYWORD,
			JuliaTypes.CATCH_KEYWORD,
			JuliaTypes.FINALLY_KEYWORD,
			JuliaTypes.FUNCTION_KEYWORD
	)

	override fun getHighlightingLexer() = JuliaLexerAdapter()
	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		JuliaTypes.STR,
		JuliaTypes.RAW_STR -> STRING_KEY
		JuliaTypes.LINE_COMMENT -> COMMENT_KEY
		JuliaTypes.INT_LITERAL,
		JuliaTypes.FLOAT_LITERAL -> NUMBER_KEY
		in KEYWORDS_LIST -> KEYWORD_KEY
		else -> emptyArray()
	}
}

class JuliaHighlighterFactory : SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = JuliaHighlighter
}
