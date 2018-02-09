package org.ice1000.julia.lang.editing

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.psi.*

class JuliaAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is JuliaMacroSymbol -> holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.MACRO_REFERENCE
			is JuliaApplyFunctionOp -> applyFunction(element, holder)
			is JuliaSymbol -> symbol(element, holder)
			is JuliaTypeAlias -> typeAlias(element, holder)
			is JuliaPlusLevelOp -> plusLevelOp(element, holder)
			is JuliaAssignLevelOp -> {
				// TODO replace $= with ⊻=
			}
			is JuliaCharLit -> char(element, holder)
			is JuliaInteger -> integer(element, holder)
			is JuliaString -> string(element, holder)
			is JuliaFloatLit -> holder.createInfoAnnotation(element, null).run {
				// TODO provide conversions
			}
		}
	}

	private fun plusLevelOp(element: JuliaPlusLevelOp, holder: AnnotationHolder) {
		holder.createWarningAnnotation(element, JuliaBundle.message("julia.lint.xor-hint")).run {
			highlightType = ProblemHighlightType.LIKE_DEPRECATED
			registerFix(
				JuliaReplaceWithTextIntention(
					element,
					"xor(${element.firstChild.text}, ${element.lastChild.text})",
					JuliaBundle.message("julia.lint.xor-replace-xor")
				)
			)

			registerFix(
				JuliaReplaceWithTextIntention(
					element,
					"${element.firstChild.text} \u22bb ${element.lastChild.text}",
					JuliaBundle.message("julia.lint.xor-replace-22bb")
				)
			)
		}
	}

	private fun symbol(element: JuliaSymbol, holder: AnnotationHolder) {
		when {
			element.text == "end" -> holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.KEYWORD
			element.isModuleName -> holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.MODULE_NAME
			element.isMacroName -> definition(element, holder, JuliaHighlighter.MACRO_NAME)
			element.isFunctionName -> definition(element, holder, JuliaHighlighter.FUNCTION_NAME)
			element.isAbstractTypeName -> holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.ABSTRACT_TYPE_NAME
			element.isPrimitiveTypeName -> holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.PRIMITIVE_TYPE_NAME
			element.isTypeName -> holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.TYPE_NAME
		}
	}

	private fun applyFunction(
		element: JuliaApplyFunctionOp,
		holder: AnnotationHolder) {
		val name = element.exprList.first()
		if (name is JuliaSymbol) {
			if (name.text == "new") holder.createInfoAnnotation(name, null).textAttributes = JuliaHighlighter.KEYWORD
		}
	}

	private fun string(string: JuliaString, holder: AnnotationHolder) = string.children
		.filter { it.firstChild.node.elementType == JuliaTypes.STRING_ESCAPE && (it.textContains('x') || it.textContains('u')) }
		.forEach { holder.createErrorAnnotation(it, JuliaBundle.message("julia.lint.invalid-string-escape")) }

	private fun definition(element: PsiElement, holder: AnnotationHolder, attributesKey: TextAttributesKey) {
		holder.createInfoAnnotation(element, null).textAttributes = attributesKey
		val space = element.nextSibling as? PsiWhiteSpace ?: return
		if (space.nextSibling?.text == "(") holder.createErrorAnnotation(space, JuliaBundle.message("julia.lint.space-function-name"))
			.registerFix(JuliaRemoveElementIntention(space, JuliaBundle.message("julia.lint.space-function-name-fix")))
	}

	private fun typeAlias(
		element: JuliaTypeAlias,
		holder: AnnotationHolder) {
		holder.createWarningAnnotation(element, JuliaBundle.message("julia.lint.typealias-hint")).run {
			highlightType = ProblemHighlightType.LIKE_DEPRECATED
			registerFix(JuliaReplaceWithTextIntention(
				element,
				"const ${element.children[0].text} = ${element.userType.text}",
				JuliaBundle.message("julia.lint.typealias-fix")))
		}
	}

	private fun char(
		element: JuliaCharLit,
		holder: AnnotationHolder) {
		when (element.textLength) {
		// 0, 1, 2 are impossible, 3: 'a', no need!
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

	private fun integer(
		element: JuliaInteger,
		holder: AnnotationHolder) {
		holder.createInfoAnnotation(element, JuliaBundle.message("julia.lint.int")).run {
			val code = element.text
			when {
				code.startsWith("0x") -> {
					val value = code.substring(2).toInt(16)
					registerFix(JuliaReplaceWithTextIntention(element, value.toString(),
						JuliaBundle.message("julia.lint.int-replace-dec")))
					registerFix(JuliaReplaceWithTextIntention(element, "0b${value.toString(2)}",
						JuliaBundle.message("julia.lint.int-replace-bin")))
					registerFix(JuliaReplaceWithTextIntention(element, "0o${value.toString(8)}",
						JuliaBundle.message("julia.lint.int-replace-oct")))
				}
				code.startsWith("0b") -> {
					val value = code.substring(2).toInt(2)
					registerFix(JuliaReplaceWithTextIntention(element, value.toString(),
						JuliaBundle.message("julia.lint.int-replace-dec")))
					registerFix(JuliaReplaceWithTextIntention(element, "0x${value.toString(16)}",
						JuliaBundle.message("julia.lint.int-replace-hex")))
					registerFix(JuliaReplaceWithTextIntention(element, "0o${value.toString(8)}",
						JuliaBundle.message("julia.lint.int-replace-oct")))
				}
				code.startsWith("0o") -> {
					val value = code.substring(2).toInt(8)
					registerFix(JuliaReplaceWithTextIntention(element, value.toString(),
						JuliaBundle.message("julia.lint.int-replace-dec")))
					registerFix(JuliaReplaceWithTextIntention(element, "0b${value.toString(2)}",
						JuliaBundle.message("julia.lint.int-replace-bin")))
					registerFix(JuliaReplaceWithTextIntention(element, "0x${value.toString(16)}",
						JuliaBundle.message("julia.lint.int-replace-hex")))
				}
				else -> {
					val value = code.toInt()
					registerFix(JuliaReplaceWithTextIntention(element, "0b${value.toString(2)}",
						JuliaBundle.message("julia.lint.int-replace-bin")))
					registerFix(JuliaReplaceWithTextIntention(element, "0o${value.toString(8)}",
						JuliaBundle.message("julia.lint.int-replace-oct")))
					registerFix(JuliaReplaceWithTextIntention(element, "0x${value.toString(16)}",
						JuliaBundle.message("julia.lint.int-replace-hex")))
				}
			}
		}
	}

}
