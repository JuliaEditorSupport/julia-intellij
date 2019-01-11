package org.ice1000.julia.lang.parsing

import com.intellij.lang.*
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.psi.tree.IElementType
import com.intellij.util.containers.Stack
import org.ice1000.julia.lang.JuliaTokenType.TokenHolder.LAZY_PARSEABLE_BLOCK
import org.ice1000.julia.lang.psi.JuliaTypes.*

object JuliaGeneratedParserUtilBase : GeneratedParserUtilBase() {
	/**
	 * @param endEnable [Boolean] means if needs to parse [endTokenTypes] at the end as a part of statment
	 */
	@JvmStatic
	fun parseBlockLazy(builder: PsiBuilder,
										 foldableTokenTypes: List<IElementType> = PAIRS,
										 endTokenTypes: List<IElementType> = END_SET): PsiBuilder.Marker? {
		// ignore itself only once
		val marker = builder.mark()
		var braceCount = 1
		while (braceCount > 0 && !builder.eof()) {
			val tokenType = builder.tokenType
			when (tokenType) {
				in foldableTokenTypes -> {
					braceCount++
					builder.advanceLexer()
				}
				in endTokenTypes -> {
					braceCount--
					advance(braceCount, builder)
				}
				else -> builder.advanceLexer()
			}
		}

		marker.collapse(LAZY_PARSEABLE_BLOCK)
		if (braceCount > 0) {
			marker.setCustomEdgeTokenBinders(null, WhitespacesBinders.GREEDY_RIGHT_BINDER)
		}

		return marker
	}

	private val END_SET = listOf(END_KEYWORD)

	private val PAIRS = listOf(
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

	@JvmStatic
	fun elseIfLazyParseableBlockImpl(builder: PsiBuilder, level: Int): Boolean {
		val foldableTokenTypes = PAIRS
		val endTokenTypes = listOf(ELSEIF_KEYWORD, ELSE_KEYWORD, END_KEYWORD)
		val stack = Stack<IElementType>()

		val marker: PsiBuilder.Marker? = builder.mark()
		var braceCount = 1
		loop@ while (braceCount > 0 && !builder.eof()) {
			val tokenType = builder.tokenType
			when (tokenType) {
				in foldableTokenTypes -> {
					braceCount++
					builder.advanceLexer()
					if (tokenType == IF_KEYWORD) {
						stack.push(tokenType)
					}
				}
				in endTokenTypes -> {
					val top = stack.tryPop()
					if (top == null) {
						braceCount--
						advance(braceCount, builder)
						continue@loop
					}
					if (top == IF_KEYWORD && tokenType == END_KEYWORD) {
						stack.pop()
					}
				}
				else -> builder.advanceLexer()
			}
		}

		marker?.collapse(LAZY_PARSEABLE_BLOCK)
		if (braceCount > 0) {
			marker?.setCustomEdgeTokenBinders(null, WhitespacesBinders.GREEDY_RIGHT_BINDER)
		}

		return marker != null
	}

	private fun advance(braceCount: Int, builder: PsiBuilder) {
		if (braceCount != 0) {
			builder.advanceLexer()
		}
	}
}
