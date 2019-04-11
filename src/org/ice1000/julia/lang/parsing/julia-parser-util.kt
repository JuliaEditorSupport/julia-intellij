package org.ice1000.julia.lang.parsing

import com.intellij.lang.*
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.psi.tree.IElementType
import com.intellij.util.containers.Stack
import org.ice1000.julia.lang.JuliaElementType.Companion.LAZY_PARSEABLE_BLOCK
import org.ice1000.julia.lang.psi.JuliaTypes.*

@Suppress("UNUSED_PARAMETER")
object JuliaGeneratedParserUtilBase : GeneratedParserUtilBase() {

	// unambiguous which must pair with `END`
	private val NORMAL_LEFT = arrayOf(
		BEGIN_KEYWORD,
		DO_KEYWORD,
		FUNCTION_KEYWORD,
		LET_KEYWORD,
		MACRO_KEYWORD,
		MODULE_KEYWORD,
		QUOTE_KEYWORD,
		STRUCT_KEYWORD,
		TRY_KEYWORD,
		WHILE_KEYWORD
	)

	private val PAIRS = arrayOf(
		FOR_KEYWORD,
		IF_KEYWORD
	) + NORMAL_LEFT

	/**
	 * ignore to parse last END_KEYWORD.
	 */
	@JvmStatic
	fun parseBlockLazy(builder: PsiBuilder,
										 foldableTokenTypes: Array<IElementType> = PAIRS,
										 parseEnd: Boolean = false): PsiBuilder.Marker? {
		// ignore itself only once
		val marker = builder.mark()
		val parStack = Stack<IElementType>()
		/**
		 * need the count of ENDs
		 */
		var braceCount = 1
		var lastToken: IElementType? = null
		while (braceCount > 0 && !builder.eof()) {
			val tokenType = builder.tokenType
			when (tokenType) {
				LEFT_M_BRACKET -> { // '['
					parStack.push(tokenType)
					advance(braceCount, builder, parseEnd)
				}
				RIGHT_M_BRACKET -> { // ']'
					val lastIndex = parStack.lastIndexOf(LEFT_M_BRACKET)
					if (lastIndex == parStack.lastIndex) { // '[' is the last
						parStack.tryPop()
					} else if (lastIndex != -1 && tokenType == RIGHT_M_BRACKET) {
						// FOR and IF between [] pairs.
						while (parStack.lastIndex - 1 != lastIndex) {
							parStack.tryPop()
						}
					}
					advance(braceCount, builder, parseEnd)
				}
				in foldableTokenTypes -> {
					when (tokenType) {
						FOR_KEYWORD ->
							if (parStack.contains(LEFT_M_BRACKET)) {// `for` is in `[`
								parStack.push(tokenType) // forComprehension
							} else { // no `[`, so it is simple
								braceCount++
							}
						IF_KEYWORD -> {
							if (parStack.contains(LEFT_M_BRACKET)) {
								if (parStack.lastOrNull() == FOR_KEYWORD) {// for if
									parStack.push(tokenType)
								} else {
									parStack.push(tokenType)// [it is if but it needs `end`]
									braceCount++
								}
							} else {
								braceCount++
							}
						}
						MODULE_KEYWORD -> if (lastToken != DOT_SYM) { // xxx.module
							braceCount++
						}
						in NORMAL_LEFT -> {
							braceCount++
							parStack.push(tokenType)
						}
						else -> {
							braceCount++
						}
					}
					builder.advanceLexer()
				}
				END_KEYWORD -> {
					// arrayIndex[a:end]
					if (parStack.lastOrNull() != LEFT_M_BRACKET) {
						braceCount--
					} else if (parStack.lastOrNull() == IF_KEYWORD) {
						// [it is `if` but it needs `end`]
						braceCount--
						parStack.tryPop()
					}
					advance(braceCount, builder, parseEnd)
				}
				else -> {
					advance(braceCount, builder, parseEnd)
				}
			}
			lastToken = tokenType
		}

		marker.collapse(LAZY_PARSEABLE_BLOCK)
		if (braceCount > 0) {
			marker.setCustomEdgeTokenBinders(null, WhitespacesBinders.GREEDY_RIGHT_BINDER)
		}

		return marker
	}


	@JvmStatic
	fun lazyBlockNotParseEndImpl(builder: PsiBuilder, level: Int): Boolean {
		return parseBlockLazy(builder) != null
	}

	@JvmStatic
	fun lazyBlockParseEndImpl(builder: PsiBuilder, level: Int): Boolean {
		return parseBlockLazy(builder, parseEnd = true) != null
	}

	private fun advance(braceCount: Int, builder: PsiBuilder, parseEnd: Boolean = false) {
		if (parseEnd || braceCount != 0) {
			builder.advanceLexer()
		}
	}
}
