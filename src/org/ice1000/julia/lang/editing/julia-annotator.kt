package org.ice1000.julia.lang.editing

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.util.SystemProperties
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.editing.JuliaBasicCompletionContributor.CompletionHolder.builtins
import org.ice1000.julia.lang.module.JuliaSettings
import org.ice1000.julia.lang.module.juliaSettings
import org.ice1000.julia.lang.psi.*
import org.ice1000.julia.lang.psi.impl.*
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
		val settings = element.project.juliaSettings.settings
		when (element) {
			is JuliaFunction -> function(element, holder, settings)
			is JuliaCompactFunction -> compactFunction(element, holder, settings)
			is JuliaApplyFunctionOp -> applyFunction(element, holder)
			is JuliaSymbol -> symbol(element, holder)
			is JuliaTypeAlias -> typeAlias(element, holder)
			is JuliaPlusLevelOp -> plusLevelOp(element, holder)
			is JuliaAssignLevelOp -> assignLevelOp(element, holder)
			is JuliaAssignOp -> assignOp(element, holder)
			is JuliaCharLit -> char(element, holder)
			is JuliaInteger -> integer(element, holder)
			is JuliaString -> string(element, holder)
			is JuliaVersionNumber -> versionNumber(element, holder)
			is JuliaFloatLit -> float(element, holder)
		}
	}

	private fun compactFunction(
		element: JuliaCompactFunction, holder: AnnotationHolder, settings: JuliaSettings) {
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
			.let { "($it)" }, settings = settings)
	}

	private fun function(
		element: JuliaFunction, holder: AnnotationHolder, settings: JuliaSettings) {
		val statements = element.statements?.run { exprList + moduleDeclarationList }
			?: return
		val signature = element.functionSignature
		val signatureText = signature?.text ?: "()"
		val typeParamsText = element.typeParameters?.text.orEmpty()
		val where = element.whereClauseList
		val name = element.name
		if (where.isEmpty()) when {
			statements.isEmpty() -> holder.createWeakWarningAnnotation(
				element.firstChild,
				JuliaBundle.message("julia.lint.empty-function"))
				.registerFix(JuliaReplaceWithTextIntention(element,
					"$name$typeParamsText$signatureText = ()",
					JuliaBundle.message("julia.lint.replace-compact-function")))
			statements.size == 1 -> {
				val expression = statements.first().let {
					(it as? JuliaReturnExpr)?.exprList?.joinToString(", ") { it.text }
						?: it.text
				}
				if (expression.length <= settings.maxCharacterToConvertToCompact)
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
			.let { "($it)" }, settings)
	}

	private fun docStringFunction(
		element: IJuliaFunctionDeclaration,
		signature: JuliaFunctionSignature?,
		holder: AnnotationHolder,
		name: String?,
		typeParamsText: String,
		signatureText: String,
		settings: JuliaSettings) {
		if (element.docString != null || element.parent !is JuliaStatements) return
		val identifier = element.nameIdentifier ?: return
		val signatureTextPart = signature?.run { typedNamedVariableList.takeIf { it.isNotEmpty() } }?.run {
			"# Arguments\n\n${joinToString("\n") {
				//                    这是一根被卡在石头里的宝剑 XD ⇊⇊⇊
				"- `${it.exprList.firstOrNull()?.text.orEmpty()}${it.typeAnnotation?.text
					?: "::Any"}`:"
			}}"
		}.orEmpty()
		holder.createInfoAnnotation(identifier, JuliaBundle.message("julia.lint.no-doc-string-function"))
			.registerFix(JuliaInsertTextBeforeIntention(element, """$JULIA_DOC_SURROUNDING
    $name$typeParamsText$signatureText

- Julia version: ${settings.version}
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

	private fun assignOp(element: JuliaAssignOp, holder: AnnotationHolder) {
		// for top-level variable declaration
		if (element.parent is JuliaStatements && element.parent.parent is JuliaFile && element.exprList.first() is JuliaTypeOp) {
			holder.createErrorAnnotation(element, JuliaBundle.message("julia.lint.variable.type-declarations.global-error"))
				.registerFix(JuliaReplaceWithTextIntention(element,
					element.text.let { it.removeRange(it.indexOf("::"), it.indexOf("=")) },
					JuliaBundle.message("julia.lint.variable.type-declarations.global-error-replace")
				))
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
		when (element.symbolKind) {
			JuliaSymbolKind.ModuleName -> holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.MODULE_NAME
			JuliaSymbolKind.MacroName -> definition(element, holder, JuliaHighlighter.MACRO_NAME)
			JuliaSymbolKind.FunctionName -> definition(element, holder, JuliaHighlighter.FUNCTION_NAME)
			JuliaSymbolKind.AbstractTypeName -> holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.ABSTRACT_TYPE_NAME
			JuliaSymbolKind.PrimitiveTypeName -> holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.PRIMITIVE_TYPE_NAME
			JuliaSymbolKind.TypeParameterName -> holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.TYPE_PARAMETER_NAME
			JuliaSymbolKind.FunctionParameter -> holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.FUNCTION_PARAMETER
			JuliaSymbolKind.TypeName -> holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.TYPE_NAME
			JuliaSymbolKind.KeywordParameterName -> holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.KEYWORD_ARGUMENT
			else -> {
			}
		}
		when {
			element.isConstName -> holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.CONST_NAME
			element.isQuoteCall -> holder.createInfoAnnotation(element.parent
				.let { if (it is JuliaQuoteOp) it else it.parent }, null)
				.textAttributes = JuliaHighlighter.QUOTE_NAME
		}
		if (element.text in arrayOf("in", "where", "isa", "end"))
			holder.createInfoAnnotation(element, null)
				.textAttributes = JuliaHighlighter.PRIMITIVE_TYPE_NAME
	}

	private fun applyFunction(
		element: JuliaApplyFunctionOp, holder: AnnotationHolder) {
		val name = element.exprList.firstOrNull()
		if (name is JuliaSymbol) {
			when (name.text) {
				"new" -> holder.createInfoAnnotation(name, null).textAttributes = JuliaHighlighter.KEYWORD
				in builtins -> holder.createInfoAnnotation(name, null).textAttributes = JuliaHighlighter.BUILTIN_NAME
				else -> holder.createInfoAnnotation(name, null).textAttributes = JuliaHighlighter.FUNCTION_CALL
			}
		}
	}

	private fun string(string: JuliaString, holder: AnnotationHolder) = string.children
		.filter { it.firstChild.node.elementType == JuliaTypes.STRING_ESCAPE && (it.textContains('x') || it.textContains('u')) }
		.forEach { holder.createErrorAnnotation(it, JuliaBundle.message("julia.lint.invalid-string-escape")) }

	private fun versionNumber(element: JuliaVersionNumber, holder: AnnotationHolder) {
		// Alibaba Java Coding Guidelines said
		// "Never use exceptions for ordinary control flow. It is ineffective and unreadable."
		// But we just do migration from Julia code to Kotlin code, so ignore it.
		try {
			val v = element.stringContentList.joinToString("") { it.text }
			JuliaInKotlin.VersionNumber(v)
		} catch (e: Exception) {
			holder.createErrorAnnotation(element, e.message)
		}
	}

	private fun definition(element: PsiElement, holder: AnnotationHolder, attributesKey: TextAttributesKey) {
		holder.createInfoAnnotation(element, null).textAttributes = attributesKey
		val space = element.nextSibling as? PsiWhiteSpace ?: return
		if (space.nextSibling?.text == "(") holder.createErrorAnnotation(space, JuliaBundle.message("julia.lint.space-function-name"))
			.registerFix(JuliaRemoveElementIntention(space, JuliaBundle.message("julia.lint.space-function-name-fix")))
	}

	private fun typeAlias(
		element: JuliaTypeAlias, holder: AnnotationHolder) {
		holder.createWarningAnnotation(element, JuliaBundle.message("julia.lint.typealias-hint")).run {
			highlightType = ProblemHighlightType.LIKE_DEPRECATED
			registerFix(JuliaReplaceWithTextIntention(
				element,
				"const ${element.children.firstOrNull()?.text} = ${element.userType?.text}",
				JuliaBundle.message("julia.lint.typealias-fix")))
		}
	}

	private fun char(
		element: JuliaCharLit, holder: AnnotationHolder) {
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

	private fun integer(element: JuliaInteger, holder: AnnotationHolder) {
		val code = element.text
		if (code.any { !it.isLetterOrDigit() }) return
		val (base, intText) = when {
			code.startsWith("0x") -> 16 to code.drop(2)
			code.startsWith("0b") -> 2 to code.drop(2)
			code.startsWith("0o") -> 8 to code.drop(2)
			else -> 10 to code
		}
		val value = BigInteger(intText, base)
		val type = checkIntType(value)
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

	private fun float(element: JuliaFloatLit, holder: AnnotationHolder) {
		val code = element.text
		var state = ""
		if ('e' in code) state = "ef"
		if ('f' in code) state = "fe"
		if (state.isNotEmpty()) {
			holder.createInfoAnnotation(element, JuliaBundle.message("julia.lint.float-literal"))
				.registerFix(JuliaReplaceWithTextIntention(element,
					code.replace(state[0], state[1]),
					JuliaBundle.message("julia.lint.float-literal-replace", state[1])))
		}
	}
}
