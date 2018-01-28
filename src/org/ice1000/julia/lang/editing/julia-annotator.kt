package org.ice1000.julia.lang.editing

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.psi.*


class JuliaAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is JuliaTypeName -> holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.TYPE_NAME
			is JuliaFunctionName -> holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.FUNCTION_NAME
			is JuliaAbstractTypeName -> holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.ABSTRACT_TYPE_NAME
			is JuliaModuleName -> holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.MODULE_NAME
			is JuliaCharLit -> char(element, holder)
		// is JuliaApplyIndexOp -> applyIndex(element, holder)
			is JuliaInteger -> integer(element, holder)
			is JuliaString -> string(element, holder)
			is JuliaFloatLit -> holder.createInfoAnnotation(element, null).run {
			}
		}
	}

//	private fun applyIndex(
//		element: JuliaApplyIndexOp,
//		holder: AnnotationHolder) {
//		val list = element.exprList
//		if (list.size == 2 && list[1] is JuliaInteger && list[1].text == "0") holder.createWarningAnnotation(list[1], JuliaBundle.message("julia.lint.array-0"))
//			.registerFix(JuliaReplaceWithTextIntention(list[1], "1", JuliaBundle.message("julia.lint.array-0-replace-1")))
//	}

	private fun char(
		element: JuliaCharLit,
		holder: AnnotationHolder) {
		when (element.textLength) {
		// 0, 1, 2 are impossible, 3: 'a'
			0, 1, 2, 3 -> {
			}
		// '\n'
			4 -> if (element.text[2] !in "ux") holder.createInfoAnnotation(element.textRange.narrow(1, 1), null)
				.textAttributes = JuliaHighlighter.CHAR_ESCAPE
			else holder.createErrorAnnotation(element.textRange.narrow(1, 1),
				JuliaBundle.message("julia.lint.invalid-char-escape"))
				.textAttributes = JuliaHighlighter.CHAR_ESCAPE_INVALID
		// '\x00'
			6 -> {
				if (element.text.trimQuotePair().matches(Regex(JULIA_CHAR_SINGLE_UNICODE_X_REGEX)))
					holder.createInfoAnnotation(element.textRange.narrow(1, 1), null).textAttributes = JuliaHighlighter.CHAR_ESCAPE
				else holder.createErrorAnnotation(element.textRange.narrow(1, 1), JuliaBundle.message("julia.lint.invalid-char-escape"))
					.textAttributes = JuliaHighlighter.CHAR_ESCAPE_INVALID
			}
		// '\u0022'
			8 -> {
				if (element.text.trimQuotePair().matches(Regex(JULIA_CHAR_SINGLE_UNICODE_U_REGEX)))
					holder.createInfoAnnotation(element.textRange.narrow(1, 1), null).textAttributes = JuliaHighlighter.CHAR_ESCAPE
				else holder.createErrorAnnotation(element.textRange.narrow(1, 1), JuliaBundle.message("julia.lint.invalid-char-escape"))
					.textAttributes = JuliaHighlighter.CHAR_ESCAPE_INVALID
			}
		// '\xe5\x86\xb0'
			14 -> {
				if (element.text.trimQuotePair().matches(Regex(JULIA_CHAR_TRIPLE_UNICODE_X_REGEX)))
					holder.createInfoAnnotation(element.textRange.narrow(1, 1), null).textAttributes = JuliaHighlighter.CHAR_ESCAPE
				else holder.createErrorAnnotation(element.textRange.narrow(1, 1), JuliaBundle.message("julia.lint.invalid-char-escape"))
					.textAttributes = JuliaHighlighter.CHAR_ESCAPE_INVALID
			}
			else -> holder.createErrorAnnotation(element.textRange.narrow(1, 1),
				JuliaBundle.message("julia.lint.invalid-char-escape"))
				.textAttributes = JuliaHighlighter.CHAR_ESCAPE_INVALID
		}
	}

	private fun string(
		element: JuliaString,
		holder: AnnotationHolder) {
		val str = element.text.trimQuotePair()
		fun markEscapeChars(escapeString: String, expandSize: Int, matchRegex: String) {
			str.indicesOf(escapeString).forEach continuing@ {
				if (it + expandSize < str.length) {
					val s = str.subSequence(it, it + expandSize)
					if (s.matches(Regex(matchRegex))) holder.createInfoAnnotation(
						element.textRange.subRangeBeginOffsetAndLength(it + 1, expandSize),
						null)
						.textAttributes = JuliaHighlighter.STRING_ESCAPE
					else {
						if (expandSize == 2) return@continuing
						holder.createErrorAnnotation(
							element.textRange.subRangeBeginOffsetAndLength(it + 1, expandSize),
							JuliaBundle.message("julia.lint.invalid-string-escape"))
							.textAttributes = JuliaHighlighter.STRING_ESCAPE_INVALID
					}
				} else holder.createErrorAnnotation(
					element.textRange.narrow(it + 1, 1),//to the end
					JuliaBundle.message("julia.lint.invalid-string-escape"))
					.textAttributes = JuliaHighlighter.STRING_ESCAPE_INVALID
			}
		}
		markEscapeChars("\\", 2, JULIA_CHAR_NOT_UX_REGEX)
		markEscapeChars("\\x", 4, JULIA_CHAR_SINGLE_UNICODE_X_REGEX)
		markEscapeChars("\\u", 6, JULIA_CHAR_SINGLE_UNICODE_U_REGEX)
	}

	private fun integer(
		integer: JuliaInteger,
		holder: AnnotationHolder) {
		val (prefix, element) = when (integer.parent) {
			is JuliaUnaryMinusOp -> true to integer.parent
			is JuliaUnaryPlusOp -> false to integer.parent
			else -> false to integer
		}
		val code = integer.text

		holder.createInfoAnnotation(element, JuliaBundle.message("julia.lint.int")).run {
			when {
				code.startsWith("0x") -> {
					val value = code.substring(2).toInt(16)
					registerFix(JuliaReplaceWithTextIntention(element, "${if (prefix) "-" else ""}$value",
						JuliaBundle.message("julia.lint.int-replace-dec")))
					registerFix(JuliaReplaceWithTextIntention(element, "${if (prefix) "-" else ""}0b${value.toString(2)}",
						JuliaBundle.message("julia.lint.int-replace-bin")))
					registerFix(JuliaReplaceWithTextIntention(element, "${if (prefix) "-" else ""}0o${value.toString(8)}",
						JuliaBundle.message("julia.lint.int-replace-oct")))
				}
				code.startsWith("0b") -> {
					val value = code.substring(2).toInt(2)
					registerFix(JuliaReplaceWithTextIntention(element, "${if (prefix) "-" else ""}$value",
						JuliaBundle.message("julia.lint.int-replace-dec")))
					registerFix(JuliaReplaceWithTextIntention(element, "${if (prefix) "-" else ""}0x${value.toString(16)}",
						JuliaBundle.message("julia.lint.int-replace-hex")))
					registerFix(JuliaReplaceWithTextIntention(element, "${if (prefix) "-" else ""}0o${value.toString(8)}",
						JuliaBundle.message("julia.lint.int-replace-oct")))
				}
				code.startsWith("0o") -> {
					val value = code.substring(2).toInt(8)
					registerFix(JuliaReplaceWithTextIntention(element, "${if (prefix) "-" else ""}$value",
						JuliaBundle.message("julia.lint.int-replace-dec")))
					registerFix(JuliaReplaceWithTextIntention(element, "${if (prefix) "-" else ""}0b${value.toString(2)}",
						JuliaBundle.message("julia.lint.int-replace-bin")))
					registerFix(JuliaReplaceWithTextIntention(element, "${if (prefix) "-" else ""}0x${value.toString(16)}",
						JuliaBundle.message("julia.lint.int-replace-hex")))
				}
				else -> {
					val value = code.toInt()
					registerFix(JuliaReplaceWithTextIntention(element, "${if (prefix) "-" else ""}0b${value.toString(2)}",
						JuliaBundle.message("julia.lint.int-replace-bin")))
					registerFix(JuliaReplaceWithTextIntention(element, "${if (prefix) "-" else ""}0o${value.toString(8)}",
						JuliaBundle.message("julia.lint.int-replace-oct")))
					registerFix(JuliaReplaceWithTextIntention(element, "${if (prefix) "-" else ""}0x${value.toString(16)}",
						JuliaBundle.message("julia.lint.int-replace-hex")))
				}
			}
		}
	}

}
