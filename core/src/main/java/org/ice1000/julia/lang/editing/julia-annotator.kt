/*
 *     Julia language support plugin for Intellij-based IDEs.
 *     Copyright (C) 2024 julia-intellij contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.ice1000.julia.lang.editing

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.ExpressionTypeProvider
import com.intellij.lang.annotation.AnnotationBuilder
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.annotation.HighlightSeverity.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.util.SystemProperties
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.editing.JuliaBasicCompletionContributor.CompletionHolder.builtins
import org.ice1000.julia.lang.module.JuliaSettings
import org.ice1000.julia.lang.module.compareVersion
import org.ice1000.julia.lang.module.juliaSettings
import org.ice1000.julia.lang.psi.*
import org.ice1000.julia.lang.psi.impl.*
import org.jetbrains.annotations.Nls
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
			is JuliaImportAllExpr -> importAll(element, holder, settings)
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
			is JuliaTypeDeclaration -> typeDeclaration(element, holder)
		}
	}

	private fun typeDeclaration(element: JuliaTypeDeclaration, holder: AnnotationHolder) {
		val declarator = element.firstChild
		if (declarator.text == "type") {
			annotation(
				holder, ERROR, JuliaBundle.message("julia.lint.type-replace-struct"), declarator,
				highlightType = ProblemHighlightType.LIKE_DEPRECATED
			) {
				withFix(object : JuliaIntentionAction(JuliaBundle.message("julia.lint.type-replace-struct-fix")) {
					override fun invoke(project: Project, editor: Editor, file: PsiFile?) {
						val document = editor.document
						if (!document.isWritable) return
						ApplicationManager.getApplication().runWriteAction {
							val offset = declarator.textOffset
							editor.caretModel.moveToOffset(offset)
							declarator.delete()
							PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document)
							document.insertString(offset, "struct")
						}
					}
				})
			}
		}
	}

	private fun compactFunction(
		element: JuliaCompactFunction, holder: AnnotationHolder, settings: JuliaSettings) {
		val typeParams = element.typeParameters
		val nameIdentifier = element.nameIdentifier ?: return
		val name = nameIdentifier.text
		val signature = element.functionSignature
		val functionBody = element.exprList.lastOrNull()?.text.orEmpty()
		annotation(holder, INFORMATION, JuliaBundle.message("julia.lint.compact-function"), element) {
			withFix(
				JuliaReplaceWithTextIntention(
				element, """function $name${typeParams?.text.orEmpty()}${element.functionSignature.text}
${if ("()" == functionBody || functionBody.isBlank()) "" else "    return $functionBody\n"}end""",
					JuliaBundle.message("julia.lint.replace-ordinary-function")
				)
			)
		}
		docStringFunction(element, signature, holder, name, "", generateSignature(signature), settings)
	}

	private fun function(
		element: JuliaFunction, holder: AnnotationHolder, settings: JuliaSettings) {
		val statements = element.statements?.children?.filter { it is JuliaExpr } ?: return
		val signature = element.functionSignature
		val signatureText = signature?.text?.trim() ?: "()"
		val typeParamsText = element.typeParameters?.text.orEmpty()
		val where = element.whereClauseList
		val nameIdentifier = element.nameIdentifier ?: return
		val name = nameIdentifier.text
		if (where.isEmpty()) when {
			statements.isEmpty() -> annotation(
				holder, WEAK_WARNING, JuliaBundle.message("julia.lint.empty-function"), nameIdentifier
			) {
				withFix(
					JuliaReplaceWithTextIntention(
						element,
						"$name$typeParamsText$signatureText = ()",
						JuliaBundle.message("julia.lint.replace-compact-function")
					)
				)
			}
			statements.size == 1 -> {
				// use last. Because typeOf will become the first when function with typeOp
				val expression = statements.last().let {
					(it as? JuliaReturnExpr)?.exprList?.joinToString(", ") { it.text }
						?: it.text
				}.trim()
				if (expression.length <= settings.maxCharacterToConvertToCompact)
					annotation(holder, WEAK_WARNING, JuliaBundle.message("julia.lint.lonely-function"), nameIdentifier)
					{
						withFix(
							JuliaReplaceWithTextIntention(
								element,
								"$name$typeParamsText$signatureText = $expression",
								JuliaBundle.message("julia.lint.replace-compact-function")
							)
						)
					}
			}
		}
		docStringFunction(element, signature, holder, name, typeParamsText, generateSignature(signature), settings)
	}

	private fun generateSignature(signature: JuliaFunctionSignature?): String {
		return signature
			?.typedNamedVariableList
			.orEmpty()
			.joinToString(", ") {
				it.exprList.firstOrNull()?.text.orEmpty() +
					it.typeAnnotation?.text?.replace(" ", "").takeIf { it != "Any" }.orEmpty()
			}
			.let { "($it)" }
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
		annotation(holder, INFORMATION, JuliaBundle.message("julia.lint.no-doc-string-function"), identifier)
		{
			withFix(
				JuliaInsertTextBeforeIntention(
					element,
					"""$JULIA_DOC_SURROUNDING
    $name$typeParamsText$signatureText

- Julia version: ${settings.version}
- Author: ${SystemProperties.getUserName()}

$signatureTextPart

# Examples

```jldoctest
julia>
```
$JULIA_DOC_SURROUNDING
""", JuliaBundle.message("julia.lint.insert-doc-string"), true
				)
			)
		}
	}

	private fun importAll(element: JuliaImportAllExpr, holder: AnnotationHolder, settings: JuliaSettings) {
		when {
			compareVersion(settings.version, "0.7.0") < 0 -> // warning underline
				annotation(holder, WEAK_WARNING, JuliaBundle.message("julia.lint.importall-hint"), element)
			compareVersion(settings.version, "1.0.0") < 0 -> // warning background
				annotation(holder, WEAK_WARNING, JuliaBundle.message("julia.lint.importall-hint"), element)
			else ->
				annotation(holder, ERROR, JuliaBundle.message("julia.lint.importall-hint"), element)
		}
	}

	private fun plusLevelOp(element: JuliaPlusLevelOp, holder: AnnotationHolder) {
		val plusLevelOperator = element.plusLevelOperator
		val text = plusLevelOperator.text
		when (text) {
			"$" -> annotation(
				holder, WARNING, JuliaBundle.message("julia.lint.xor-hint", text), plusLevelOperator,
				highlightType = ProblemHighlightType.LIKE_DEPRECATED
			)
			{
				withFix(
					JuliaReplaceWithTextIntention(
						element, "xor(${element.firstChild.text}, ${element.lastChild.text})",
						JuliaBundle.message("julia.lint.xor-replace-xor", element.firstChild.text, element.lastChild.text)
					)
				).withFix(
					JuliaReplaceWithTextIntention(
						element, "${element.firstChild.text} \u22bb ${element.lastChild.text}",
						JuliaBundle.message("julia.lint.xor-replace-22bb", element.firstChild.text, element.lastChild.text)
					)
				)
			}

		}
	}

	private fun assignOp(element: JuliaAssignOp, holder: AnnotationHolder) {
		// for top-level variable declaration
		if (element.parent is JuliaStatements && element.parent.parent is JuliaFile && element.exprList.first() is JuliaTypeOp) {
			annotation(
				holder, ERROR, JuliaBundle.message("julia.lint.variable.type-declarations.global-error"), element
			) {
				withFix(
					JuliaReplaceWithTextIntention(
						element,
						element.text.let { it.removeRange(it.indexOf("::"), it.indexOf("=")) },
						JuliaBundle.message("julia.lint.variable.type-declarations.global-error-replace")
					)
				)
			}
		}
		// element after `=` if stupid!
		val leftElement = element.exprList.firstOrNull()
		val rightElement = leftElement?.nextRealSibling?.nextRealSibling ?: return
		when (rightElement) {
			is JuliaUsing,
			is JuliaImportExpr,
			is IJuliaTypeDeclaration ->
				annotation(
					holder, WARNING, JuliaBundle.message("julia.lint.variable.assign-from-nothing.warning"), element
				) {
					withFix(
						JuliaReplaceWithTextIntention(
							element,
							rightElement.text,
							JuliaBundle.message("julia.lint.variable.assign-from-nothing-replace")
						)
					)
				}
			is JuliaModuleDeclaration -> {
				annotation(holder, ERROR, JuliaBundle.message("julia.lint.variable.assign-to-module-error"), element) {
					withFix(
						JuliaReplaceWithTextIntention(
							element,
							rightElement.text,
							JuliaBundle.message("julia.lint.variable.assign-from-nothing-replace")
						)
					)
				}
			}
		}
	}

	private fun assignLevelOp(element: JuliaAssignLevelOp, holder: AnnotationHolder) {
		when (element.children.getOrNull(1)?.run { text.firstOrNull() }) {
			'$' -> annotation(
				holder, WARNING, JuliaBundle.message("julia.lint.xor-hint", element.text), element,
				highlightType = ProblemHighlightType.LIKE_DEPRECATED
			) {
				withFix(
					JuliaReplaceWithTextIntention(
						element,
						"${element.firstChild.text} = xor(${element.firstChild.text}, ${element.lastChild.text})",
						JuliaBundle.message("julia.lint.xor-is-replace-xor", element.firstChild.text, element.lastChild.text)
					)
				).withFix(
					JuliaReplaceWithTextIntention(
						element,
						"${element.firstChild.text} \u22bb= ${element.lastChild.text}",
						JuliaBundle.message("julia.lint.xor-is-replace-22bb", element.firstChild.text, element.lastChild.text)
					)
				)
			}
		}
	}

	private fun symbol(element: JuliaSymbol, holder: AnnotationHolder) {
		when (element.symbolKind) {
			JuliaSymbolKind.ModuleName -> annotation(holder, INFORMATION, "", element, JuliaHighlighter.MODULE_NAME)
			JuliaSymbolKind.MacroName -> definition(element, holder, JuliaHighlighter.MACRO_NAME)
			JuliaSymbolKind.FunctionName -> definition(element, holder, JuliaHighlighter.FUNCTION_NAME)
			JuliaSymbolKind.AbstractTypeName -> annotation(
				holder, INFORMATION, "", element, JuliaHighlighter.ABSTRACT_TYPE_NAME
			)

			JuliaSymbolKind.PrimitiveTypeName -> annotation(
				holder, INFORMATION, "", element, JuliaHighlighter.PRIMITIVE_TYPE_NAME
			)
			JuliaSymbolKind.TypeParameterName -> {
				annotation(holder, INFORMATION, "", element, JuliaHighlighter.TYPE_PARAMETER_NAME)
				return
			}

			JuliaSymbolKind.FunctionParameter -> annotation(
				holder, INFORMATION, "", element, JuliaHighlighter.FUNCTION_PARAMETER
			)
			JuliaSymbolKind.TypeName -> {
				if (element.isAbstractTypeRef) annotation(
					holder, INFORMATION, "", element, JuliaHighlighter.ABSTRACT_TYPE_NAME
				)
				annotation(holder, INFORMATION, "", element, JuliaHighlighter.TYPE_NAME)
			}

			JuliaSymbolKind.KeywordParameterName -> annotation(
				holder, INFORMATION, "", element, JuliaHighlighter.KEYWORD_ARGUMENT
			)
			else -> {
			}
		}
		when {
			element.isConstName || element.isConstNameRef -> annotation(
				holder, INFORMATION, "", element, JuliaHighlighter.CONST_NAME
			)

			element.isTypeNameRef -> annotation(
				holder, INFORMATION, "", element, JuliaHighlighter.TYPE_NAME
			)
			element.isSuperTypeExpr -> {
				val attr = if (element.isAbstractTypeRef) JuliaHighlighter.ABSTRACT_TYPE_NAME
				else JuliaHighlighter.TYPE_NAME
				annotation(holder, INFORMATION, "", element, attr)
			}

			element.isModuleNameRef -> annotation(
				holder, INFORMATION, "", element, JuliaHighlighter.MODULE_NAME
			)

			element.isQuoteCall -> annotation(
				holder, INFORMATION, "",
				element,
				JuliaHighlighter.QUOTE_NAME
			)
		}
		element.text.let {
			when {
				it.isNullOrBlank() -> return@let
				it in arrayOf("in", "where", "end") -> annotation(
					holder, INFORMATION, "", element, JuliaHighlighter.PRIMITIVE_TYPE_NAME
				)
				// TODO: `builtins` need stub later
				it[0].isUpperCase() && it in builtins -> annotation(
					holder, INFORMATION, "", element, JuliaHighlighter.TYPE_NAME
				)

				it in arrayOf("nothing") -> annotation(
					holder, INFORMATION, "", element, JuliaHighlighter.KEYWORD
				)
			}
		}
	}

	private fun applyFunction(
		element: JuliaApplyFunctionOp, holder: AnnotationHolder) {
		val name = element.exprList.firstOrNull()
		if (name is JuliaSymbol) {
			when (name.text) {
				"new" -> annotation(holder, INFORMATION, "", name, JuliaHighlighter.KEYWORD)
				in builtins -> annotation(holder, INFORMATION, "", name, JuliaHighlighter.BUILTIN_NAME)
				else -> annotation(holder, INFORMATION, "", name, JuliaHighlighter.FUNCTION_CALL)
			}
			when {
				name.isTypeNameRef || name.typeFoundFromStub
					-> annotation(holder, INFORMATION, "", name, JuliaHighlighter.TYPE_NAME)
			}
		}

	}

	private fun string(string: JuliaString, holder: AnnotationHolder) = string.children
		.filter { it.firstChild.node.elementType == JuliaTypes.STRING_ESCAPE && (it.textContains('x') || it.textContains('u')) }
		.forEach { annotation(holder, ERROR, JuliaBundle.message("julia.lint.invalid-string-escape"), it) }

	private fun versionNumber(element: JuliaVersionNumber, holder: AnnotationHolder) {
		// Alibaba Java Coding Guidelines said
		// "Never use exceptions for ordinary control flow. It is ineffective and unreadable."
		// But we just do migration from Julia code to Kotlin code, so ignore it.
		try {
			val v = element.stringContentList.joinToString("") { it.text }
			JuliaInKotlin.VersionNumber(v)
		} catch (e: Exception) {
			annotation(holder, ERROR, e.message ?: "Unknown exception", element)
		}
	}

	private fun definition(element: PsiElement, holder: AnnotationHolder, attributesKey: TextAttributesKey) {
		annotation(holder, INFORMATION, "", element, attributesKey)
		val space = element.nextSibling as? PsiWhiteSpace ?: return
		if (space.nextSibling?.text == "(")
			annotation(holder, ERROR, JuliaBundle.message("julia.lint.space-function-name"), space) {
				withFix(JuliaRemoveElementIntention(space, JuliaBundle.message("julia.lint.space-function-name-fix")))
			}
	}

	private fun typeAlias(
		element: JuliaTypeAlias, holder: AnnotationHolder) {
		annotation(
			holder, WARNING,
			JuliaBundle.message("julia.lint.typealias-hint"), element, highlightType = ProblemHighlightType.LIKE_DEPRECATED
		) {
			withFix(
				JuliaReplaceWithTextIntention(
					element,
					"const ${element.children.firstOrNull()?.text} = ${element.userType?.text}",
					JuliaBundle.message("julia.lint.typealias-fix")
				)
			)
		}

	}

	private fun char(
		element: JuliaCharLit, holder: AnnotationHolder) {
		when (element.textLength) {
			// 0, 1, 2 are impossible, 3: 'a', no need!
			0, 1, 2, 3 -> {
			}
			// '\n'
			4 -> if (element.text[2] !in "ux") charEscapeHighlighting(holder, element)
			else invalidCharEscapeError(holder, element)
			// '\x00'
			6 -> {
				if (element.text.trimQuotePair().matches(Regex(JULIA_CHAR_SINGLE_UNICODE_X_REGEX)))
					charEscapeHighlighting(holder, element)
				else invalidCharEscapeError(holder, element)
			}
			// '\u0022'
			8 -> {
				if (element.text.trimQuotePair().matches(Regex(JULIA_CHAR_SINGLE_UNICODE_U_REGEX)))
					charEscapeHighlighting(holder, element)
				else invalidCharEscapeError(holder, element)
			}
			// '\xe5\x86\xb0'
			14 -> {
				if (element.text.trimQuotePair().matches(Regex(JULIA_CHAR_TRIPLE_UNICODE_X_REGEX)))
					charEscapeHighlighting(holder, element)
				else invalidCharEscapeError(holder, element)
			}

			else -> invalidCharEscapeError(holder, element)
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
		annotation(holder, INFORMATION, JuliaBundle.message("julia.lint.int-type", type), element) {
			if (base != 2) withFix(
				JuliaReplaceWithTextIntention(
					element, "0b${value.toString(2)}",
					JuliaBundle.message("julia.lint.int-replace-bin")
				)
			)
			if (base != 8) withFix(
				JuliaReplaceWithTextIntention(
					element, "0o${value.toString(8)}",
					JuliaBundle.message("julia.lint.int-replace-oct")
				)
			)
			if (base != 10) withFix(
				JuliaReplaceWithTextIntention(
					element, value.toString(),
					JuliaBundle.message("julia.lint.int-replace-dec")
				)
			)
			if (base != 16) withFix(
				JuliaReplaceWithTextIntention(
					element, "0x${value.toString(16)}",
					JuliaBundle.message("julia.lint.int-replace-hex")
				)
			)
		}
	}

	private fun float(element: JuliaFloatLit, holder: AnnotationHolder) {
		val code = element.text
		var state = ""
		if ('e' in code) state = "ef"
		if ('f' in code) state = "fe"
		if (state.isNotEmpty()) {
			annotation(holder, INFORMATION, JuliaBundle.message("julia.lint.float-literal"), element)
			{
				withFix(
					JuliaReplaceWithTextIntention(
						element,
						code.replace(state[0], state[1]),
						JuliaBundle.message("julia.lint.float-literal-replace", state[1])
					)
				)
			}
		}
	}

	private inline fun annotation(
		holder: AnnotationHolder,
		severity: HighlightSeverity,
		message: @Nls String,
		psiElement: PsiElement?,
		textAttributesKey: TextAttributesKey? = null,
		highlightType: ProblemHighlightType? = null,
		configurator: (AnnotationBuilder.() -> Unit) = { },
	) {
		val builder = holder.newAnnotation(severity, message)
		psiElement?.let { builder.range(it) }
		highlightType?.let { builder.highlightType(it) }
		textAttributesKey?.let { builder.textAttributes(it) }
		configurator(builder)
		builder.create()
	}

	private fun charEscapeHighlighting(holder: AnnotationHolder, element: JuliaCharLit) =
		annotation(holder, INFORMATION, "", null, JuliaHighlighter.CHAR_ESCAPE) {
			range(element.textRange.narrow(1, 1))
		}


	private fun invalidCharEscapeError(holder: AnnotationHolder, element: JuliaCharLit) = annotation(
		holder, ERROR, JuliaBundle.message("julia.lint.invalid-char-escape"), null,
		JuliaHighlighter.CHAR_ESCAPE_INVALID
	) { range(element.textRange.narrow(1, 1)) }
}

// TODO: needs stub
class JuliaExpressionTypeProvider : ExpressionTypeProvider<JuliaExpr>() {
	override fun getInformationHint(element: JuliaExpr): String {
		val type = element.type ?: element.reference?.run { resolve() as? JuliaExpr }?.type
		val text = type ?: "<unknown>"
		return text
	}

	override fun getExpressionsAt(elementAt: PsiElement): MutableList<JuliaExpr> {
		return SyntaxTraverser.psiApi().parents(elementAt).filter(JuliaExpr::class.java).filter {
			it is JuliaSymbol || it is JuliaFloatLit || it is JuliaInteger || it is JuliaString || it is JuliaMemberAccess
		}.toList()
	}

	override fun getErrorHint(): String = "<unknown type>"

}