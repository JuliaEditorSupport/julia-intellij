package org.ice1000.julia.lang.parsing

import com.intellij.lang.*
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.psi.tree.IElementType
import org.ice1000.julia.lang.JuliaTokenType.TokenHolder.LAZY_PARSEABLE_BLOCK
import org.ice1000.julia.lang.psi.JuliaTypes.*

object JuliaGeneratedParserUtilBase : GeneratedParserUtilBase() {
	/**
	 * @param endEnable [Boolean] means if needs to parse [endTokenTypes] at the end as a part of statment
	 */
	@JvmStatic
	fun parseBlockLazy(builder: PsiBuilder,
										 foldableTokenTypes: Array<IElementType> = PAIRS,
										 endTokenTypes: Array<IElementType> = END_SET,
										 endEnable: Boolean = false): PsiBuilder.Marker? {
		// ignore itself only once
		val marker = builder.mark()
		builder.advanceLexer()
		var braceCount = 1
		var lastEnd = false
		while (braceCount > 0 && !builder.eof()) {
			val tokenType = builder.tokenType
			when (tokenType) {
				in foldableTokenTypes -> {
					braceCount++
				}
				in endTokenTypes -> {
					braceCount--
					if (braceCount == 0) lastEnd = true
				}
				else -> lastEnd = false
			}
			if (endEnable || !lastEnd)
				builder.advanceLexer()
		}
		marker.collapse(LAZY_PARSEABLE_BLOCK)
		if (braceCount > 0) {
			marker.setCustomEdgeTokenBinders(null, WhitespacesBinders.GREEDY_RIGHT_BINDER)
		}

		return marker
	}

	private val END_SET = arrayOf(END_KEYWORD)

	private val PAIRS = arrayOf(
		LET_KEYWORD,
		FOR_KEYWORD,

		MODULE_KEYWORD,
		QUOTE_KEYWORD,
		IF_KEYWORD,
		BEGIN_KEYWORD,
		TRY_KEYWORD,
		DO_KEYWORD,
		WHILE_KEYWORD,
		FUNCTION_KEYWORD,
		TYPE_KEYWORD,
		MACRO_KEYWORD
	)

	@JvmStatic
	fun elseLazyParseableBlockImpl(builder: PsiBuilder, level: Int): Boolean {
		return parseBlockLazy(builder) != null
	}

	@JvmStatic
	fun whileLazyBlockImpl(builder: PsiBuilder, level: Int): Boolean {
		return parseBlockLazy(builder) != null
	}
}
