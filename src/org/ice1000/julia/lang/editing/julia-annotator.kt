package org.ice1000.julia.lang.editing

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.SystemInfo
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.util.SystemProperties
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.module.juliaSettings
import org.ice1000.julia.lang.psi.*
import org.ice1000.julia.lang.psi.impl.IJuliaFunctionDeclaration
import java.math.BigInteger
import java.math.BigInteger.ONE
import java.math.BigInteger.ZERO

enum class NumeralType(
	bit: Int,
	val range: ClosedRange<BigInteger> = BigInteger.valueOf(2L).pow(bit - 1).let { -it..it - ONE }) {
	Int8(8),
	Int16(16),
	Int32(32),
	Int64(64),
	Int128(128),
	BigInt(0, ZERO..ZERO);
}

class JuliaAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is JuliaFunction -> function(element, holder)
			is JuliaCompactFunction -> compactFunction(element, holder)
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

	private fun compactFunction(
		element: JuliaCompactFunction,
		holder: AnnotationHolder) {
		val typeParams = element.typeParameters
		val name = element.name
		val signature = element.functionSignature
		val functionBody = element.exprList.lastOrNull()?.text.orEmpty()
		holder.createInfoAnnotation(element, JuliaBundle.message("julia.lint.compact-function"))
			.registerFix(JuliaReplaceWithTextIntention(
				element, """function $name${typeParams?.text.orEmpty()}${element.functionSignature.text}
${if ("()" == functionBody || functionBody.isBlank()) "" else "    return $functionBody\n"}end""",
				JuliaBundle.message("julia.lint.replace-ordinary-function")))
		docStringFunction(element, signature, holder, name, "", signatureText = signature
			.typedNamedVariableList
			.joinToString(", ") { it.exprList.firstOrNull()?.text.orEmpty() }
			.let { "($it)" })
	}

	private fun function(
		element: JuliaFunction,
		holder: AnnotationHolder) {
		val statements = element.statements?.run { exprList + moduleDeclarationList + globalStatementList } ?: return
		val signature = element.functionSignature
		val signatureText = signature?.text ?: "()"
		val typeParamsText = element.typeParameters?.text.orEmpty()
		val where = element.whereClause
		val name = element.name
		if (where == null) when {
			statements.isEmpty() -> holder.createWeakWarningAnnotation(
				element.firstChild,
				JuliaBundle.message("julia.lint.empty-function"))
				.registerFix(JuliaReplaceWithTextIntention(element,
					"$name$typeParamsText$signatureText = ()",
					JuliaBundle.message("julia.lint.replace-compact-function")))
			statements.size == 1 -> {
				val expression = statements.first().let {
					(it as? JuliaReturnExpr)?.exprList?.joinToString(", ") { it.text } ?: it.text
				}
				holder.createWeakWarningAnnotation(
					element.firstChild,
					JuliaBundle.message("julia.lint.lonely-function"))
					.registerFix(JuliaReplaceWithTextIntention(element,
						"$name$typeParamsText$signatureText = $expression",
						JuliaBundle.message("julia.lint.replace-compact-function")))
			}
		}
		docStringFunction(element, signature, holder, name, typeParamsText, signature
			?.typedNamedVariableList
			.orEmpty()
			.joinToString(", ") { it.exprList.firstOrNull()?.text.orEmpty() }
			.let { "($it)" })
	}

	private fun docStringFunction(
		element: IJuliaFunctionDeclaration,
		signature: JuliaFunctionSignature?,
		holder: AnnotationHolder,
		name: String?,
		typeParamsText: String,
		signatureText: String) {
		if (element.docString != null || element.parent !is JuliaStatements) return
		val identifier = element.nameIdentifier ?: return
		val signatureTextPart = signature?.run { typedNamedVariableList.takeIf { it.isNotEmpty() } }?.run {
			"# Arguments\n\n${joinToString("\n") {
				//                    这是一根被卡在石头里的宝剑 XD ⇊⇊⇊
				"- `${it.exprList.firstOrNull()?.text.orEmpty()}${it.typeAnnotation?.text ?: "::Any"}`:"
			}}"
		}.orEmpty()
		holder.createInfoAnnotation(identifier, JuliaBundle.message("julia.lint.no-doc-string-function"))
			.registerFix(JuliaInsertTextBeforeIntention(element, """$JULIA_DOC_SURROUNDING
    $name$typeParamsText$signatureText

- Julia version: ${element.project.juliaSettings.settings.version}
- Author: ${SystemProperties.getUserName()}

$signatureTextPart

# Examples

```jldoctest
julia>
```
$JULIA_DOC_SURROUNDING
""", JuliaBundle.message("julia.lint.insert-doc-string"), true))
	}

	private fun plusLevelOp(element: JuliaPlusLevelOp, holder: AnnotationHolder) {
		when (element.plusLevelOperator.text) {
			"$" -> holder.createWarningAnnotation(element, JuliaBundle.message("julia.lint.xor-hint", element.text)).run {
				highlightType = ProblemHighlightType.LIKE_DEPRECATED
				registerFix(JuliaReplaceWithTextIntention(element, "xor(${element.firstChild.text}, ${element.lastChild.text})",
					JuliaBundle.message("julia.lint.xor-replace-xor", element.firstChild.text, element.lastChild.text)))
				registerFix(JuliaReplaceWithTextIntention(element, "${element.firstChild.text} \u22bb ${element.lastChild.text}",
					JuliaBundle.message("julia.lint.xor-replace-22bb", element.firstChild.text, element.lastChild.text)))
			}
		}
	}

	private fun assignLevelOp(element: JuliaAssignLevelOp, holder: AnnotationHolder) {
		when (element.children.getOrNull(1)?.run { text.firstOrNull() }) {
			'$' -> holder.createWarningAnnotation(element,
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
			element.isFunctionName -> {
				definition(element, holder, JuliaHighlighter.FUNCTION_NAME)
			}
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
		val name = element.exprList.firstOrNull()
		if (name is JuliaSymbol) {
			when (name.text) {
				"new" -> holder.createInfoAnnotation(name, null).textAttributes = JuliaHighlighter.KEYWORD
			}
		}
	}

	private fun string(string: JuliaString, holder: AnnotationHolder) = string.children
		.filter { it.firstChild.node.elementType == JuliaTypes.STRING_ESCAPE && (it.textContains('x') or it.textContains('u')) }
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
				"const ${element.children.firstOrNull()?.text} = ${element.userType?.text}",
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

	private fun checkType(value: BigInteger): String = when (value) {
		in NumeralType.Int32.range ->
			if (SystemInfo.is32Bit) NumeralType.Int32 else NumeralType.Int64
		in NumeralType.Int64.range -> NumeralType.Int64
		in NumeralType.Int128.range -> NumeralType.Int128
		else -> NumeralType.BigInt
	}.name

	private fun integer(element: JuliaInteger, holder: AnnotationHolder) {
		val code = element.text
		if ('p' in code || 'e' in code) return
		val (base, intText) = when {
			code.startsWith("0x") -> 16 to code.drop(2)
			code.startsWith("0b") -> 2 to code.drop(2)
			code.startsWith("0o") -> 8 to code.drop(2)
			else -> 10 to code
		}
		val value = BigInteger(intText, base)
		val type = checkType(value)
		element.type = type
		val annotation = holder.createInfoAnnotation(element,
			JuliaBundle.message("julia.lint.int-type", type))
		if (base != 2) annotation.registerFix(JuliaReplaceWithTextIntention(element, "0b${value.toString(2)}",
			JuliaBundle.message("julia.lint.int-replace-bin")))
		if (base != 8) annotation.registerFix(JuliaReplaceWithTextIntention(element, "0o${value.toString(8)}",
			JuliaBundle.message("julia.lint.int-replace-oct")))
		if (base != 10) annotation.registerFix(JuliaReplaceWithTextIntention(element, value.toString(),
			JuliaBundle.message("julia.lint.int-replace-dec")))
		if (base != 16) annotation.registerFix(JuliaReplaceWithTextIntention(element, "0x${value.toString(16)}",
			JuliaBundle.message("julia.lint.int-replace-hex")))
	}
}
