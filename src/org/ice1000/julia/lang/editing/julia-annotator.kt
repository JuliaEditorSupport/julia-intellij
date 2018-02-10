package org.ice1000.julia.lang.editing

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.SystemInfo
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.psi.*
import java.math.BigInteger

class JuliaAnnotator : Annotator {
	companion object {
		class Type( val name : String ) {
			val bit : Int = name.replace(Regex("[a-zA-Z]+"), "").toInt()
			val max : BigInteger = BigInteger("2").pow(bit - 1).minus(BigInteger("1"))
			val min : BigInteger = BigInteger("-2").pow(bit - 1)
		}

		val Int8 = Type("Int8")
		val Int16 = Type("Int16")
		val Int32 = Type("Int32")
		val Int64 = Type("Int64")
		val Int128 = Type("Int128")
	}

	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is JuliaMacroSymbol -> holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.MACRO_REFERENCE
			is JuliaApplyFunctionOp -> applyFunction(element, holder)
			is JuliaSymbol -> symbol(element, holder)
			is JuliaTypeAlias -> typeAlias(element, holder)
			is JuliaPlusLevelOp -> plusLevelOp(element, holder)
			is JuliaAssignLevelOp -> assignLevelOp(element, holder)
			is JuliaCharLit -> char(element, holder)
			is JuliaInteger -> integer(element, holder)
			is JuliaString -> string(element, holder)
			is JuliaFloatLit -> holder.createInfoAnnotation(element, null).run {
				// TODO provide conversions
			}
		}
	}

	private fun plusLevelOp(element: JuliaPlusLevelOp, holder: AnnotationHolder) {
		when (element.plusLevelOperator.text[0]) {
			'$' -> {
				holder.createWarningAnnotation(element, JuliaBundle.message("julia.lint.xor-hint", element.text)).run {
					highlightType = ProblemHighlightType.LIKE_DEPRECATED
					registerFix(JuliaReplaceWithTextIntention(element, "xor(${element.firstChild.text}, ${element.lastChild.text})",
						JuliaBundle.message("julia.lint.xor-replace-xor", element.firstChild.text, element.lastChild.text)))
					registerFix(JuliaReplaceWithTextIntention(element, "${element.firstChild.text} \u22bb ${element.lastChild.text}",
						JuliaBundle.message("julia.lint.xor-replace-22bb", element.firstChild.text, element.lastChild.text)))
				}
			}
		}
	}

	private fun assignLevelOp(element: JuliaAssignLevelOp, holder: AnnotationHolder) {
		when (element.assignLevelOperator.text[0]) {
			'$' ->
				holder.createWarningAnnotation(element,
					JuliaBundle.message("julia.lint.xor-hint", element.text)).run {
					highlightType = ProblemHighlightType.LIKE_DEPRECATED
					val left = element.firstChild.text
					val right = element.lastChild.text
					registerFix(JuliaReplaceWithTextIntention(element, "$left = xor($left, $right)",
						JuliaBundle.message("julia.lint.xor-is-replace-xor", left, right)))
					registerFix(JuliaReplaceWithTextIntention(element, "$left \u22bb= $right",
						JuliaBundle.message("julia.lint.xor-is-replace-22bb", left, right)))
				}
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

		fun checkType( value : BigInteger ) : String =
			when(value) {
				in Int32.min..Int32.max ->
					if( SystemInfo.is32Bit ) Int32.name else Int64.name

				in Int64.min..Int64.max -> Int64.name
				in Int128.min..Int128.max -> Int128.name

				else -> "BigInt"
			}

		val code = element.text
		var value = BigInteger("0")
		holder.createInfoAnnotation(element, JuliaBundle.message("julia.lint.int")).run {
			when {
				code.startsWith("0x") -> {
					value = BigInteger(code.substring(2), 16)
					registerFix(JuliaReplaceWithTextIntention(element, value.toString(),
						JuliaBundle.message("julia.lint.int-replace-dec")))
					registerFix(JuliaReplaceWithTextIntention(element, "0b${value.toString(2)}",
						JuliaBundle.message("julia.lint.int-replace-bin")))
					registerFix(JuliaReplaceWithTextIntention(element, "0o${value.toString(8)}",
						JuliaBundle.message("julia.lint.int-replace-oct")))
				}
				code.startsWith("0b") -> {
					value = BigInteger(code.substring(2), 2)
					registerFix(JuliaReplaceWithTextIntention(element, value.toString(),
						JuliaBundle.message("julia.lint.int-replace-dec")))
					registerFix(JuliaReplaceWithTextIntention(element, "0x${value.toString(16)}",
						JuliaBundle.message("julia.lint.int-replace-hex")))
					registerFix(JuliaReplaceWithTextIntention(element, "0o${value.toString(8)}",
						JuliaBundle.message("julia.lint.int-replace-oct")))
				}
				code.startsWith("0o") -> {
					value = BigInteger(code.substring(2), 8)
					registerFix(JuliaReplaceWithTextIntention(element, value.toString(),
						JuliaBundle.message("julia.lint.int-replace-dec")))
					registerFix(JuliaReplaceWithTextIntention(element, "0b${value.toString(2)}",
						JuliaBundle.message("julia.lint.int-replace-bin")))
					registerFix(JuliaReplaceWithTextIntention(element, "0x${value.toString(16)}",
						JuliaBundle.message("julia.lint.int-replace-hex")))
				}
				else -> {
					value = BigInteger(code)
					registerFix(JuliaReplaceWithTextIntention(element, "0b${value.toString(2)}",
						JuliaBundle.message("julia.lint.int-replace-bin")))
					registerFix(JuliaReplaceWithTextIntention(element, "0o${value.toString(8)}",
						JuliaBundle.message("julia.lint.int-replace-oct")))
					registerFix(JuliaReplaceWithTextIntention(element, "0x${value.toString(16)}",
						JuliaBundle.message("julia.lint.int-replace-hex")))
				}
			}
		}

		element.type = checkType(value)

		holder.createInfoAnnotation(element, JuliaBundle.message("julia.lint.int-type", element.type!!))
	}

}
