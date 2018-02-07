package org.ice1000.julia.lang.docfmt

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiElement
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.docfmt.psi.DocfmtConfig
import org.ice1000.julia.lang.docfmt.psi.DocfmtTypes
import org.ice1000.julia.lang.editing.JuliaCompletionProvider
import org.ice1000.julia.lang.editing.JuliaRemoveElementIntention

class DocfmtAnnotator : Annotator {
	companion object {

		val indentArgs = arrayOf(
			"CSTParser.Begin", "CSTParser.Quote",
			"CSTParser.For", "CSTParser.While", "CSTParser.FunctionDef", "CSTParser.Macro",
			"CSTParser.Struct", "CSTParser.Let", "CSTParser.Try", "CSTParser.If", "CSTParser.Mutable"
		)

		val noWsOpGroup = arrayOf(
			"8", "13", "14", "16"
		)
	}

	/**
	 * //IndentWidth : 4
	 * //KW_WS : true
	 * //NewLineEOF : false
	 * //No_WS_OP_group : [8, 13, 14, 16]
	 * No_WS_OP_indv : Any[]
	 * //StripLineEnds : false
	 * //TabWidth : 4
	 * //UseTab : false
	 */

	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		if (element is DocfmtConfig) {
			fun unused() {
				holder.createWeakWarningAnnotation(element, JuliaBundle.message("docfmt.lint.default")).run {
					highlightType = ProblemHighlightType.LIKE_UNUSED_SYMBOL
					registerFix(JuliaRemoveElementIntention(element, JuliaBundle.message("docfmt.lint.remove")))
				}
			}

			fun invalidValue() = holder.createErrorAnnotation(element, JuliaBundle.message("docfmt.lint.invalid-val", element.text)).run {
					highlightType = ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
				}

			fun number() {
				val value = element.value
				if (value.firstChild.node.elementType != DocfmtTypes.INT) invalidValue()
				else if (value.text == "4") unused()
			}

			fun checkBoolean(default: Boolean = false) {
			when (element.value.text) {
				default.toString() -> unused()
				(!default).toString() -> Unit

				else -> invalidValue()
			}
		}

			val key = element.firstChild
			val value = element.value
			when (key.text) {
				"TabWidth" -> number()
				"IndentWidth" -> number()
				"UseTab" -> checkBoolean()
				"AlignAfterOpenBracket" -> when (value.text) {
					"Align" -> unused()
					"AlwaysBreak", "DontAlign" -> Unit // OK
					else -> invalidValue()
				}
				"IndentEXPR" ->
					if( Companion.indentArgs.contains(value.text) ) Unit
					else invalidValue()
				"KW_WS" -> checkBoolean(true)
				"NewLineEOF" -> checkBoolean()
				"StripLineEnds" -> checkBoolean()
				"No_WS_OP_group" ->
					if( Companion.noWsOpGroup.contains(value.text) ) Unit
					else invalidValue()
				else -> holder.createErrorAnnotation(key, JuliaBundle.message("docfmt.lint.invalid", key.text)).run {
					highlightType = ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
				}
			}
		} else return
	}

	/* move to annotate function
	private fun checkBoolean(element: DocfmtConfig, holder: AnnotationHolder, default: Boolean = false) =
		when( element.value.text ) {
			default.toString() -> unused(element, holder)
			(! default).toString() -> Unit

			else -> invalidValue(element, holder)
		}

	private fun number(element: DocfmtConfig, holder: AnnotationHolder) {
		val value = element.value
		if (value.firstChild.node.elementType != DocfmtTypes.INT) invalidValue(value, holder)
		else if (value.text == "4") unused(element, holder)
	}

	/**
	 * for settings that matches the default value
	 */
	private fun unused(element: PsiElement, holder: AnnotationHolder) {
		holder.createWeakWarningAnnotation(element, JuliaBundle.message("docfmt.lint.default")).run {
			highlightType = ProblemHighlightType.LIKE_UNUSED_SYMBOL
			registerFix(JuliaRemoveElementIntention(element, JuliaBundle.message("docfmt.lint.remove")))
		}
	}

	/**
	 * for settings that doesn't match any valid value
	 */
	private fun invalidValue(value: PsiElement, holder: AnnotationHolder) =
		holder.createErrorAnnotation(value, JuliaBundle.message("docfmt.lint.invalid-val", value.text)).run {
			highlightType = ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
		}

		*/
}

class DocfmtCompletionContributor : CompletionContributor() {
	internal companion object CompletionHolder {
		private val VALID_KEY = listOf(
			"TabWidth",
			"UseTab",
			"IndentWidth",
			"AlignAfterOpenBracket",
			"StripLineEnds",
			"IndentEXPR",
			"KW_WS",
			"No_WS_OP_group",
			"No_WS_OP_indv",
			"NewLineEOF"
		)
		private val KEYS = VALID_KEY.map(LookupElementBuilder::create)
		private val VALUES = listOf(
			"Align",
			"true",
			"false"
		).map(LookupElementBuilder::create)
	}

	init {
		extend(
			CompletionType.BASIC,
			psiElement().afterLeaf(psiElement(DocfmtTypes.EOL)),
			JuliaCompletionProvider(KEYS))
		extend(
			CompletionType.BASIC,
			psiElement().afterLeaf(psiElement(DocfmtTypes.EQ_SYM)),
			JuliaCompletionProvider(VALUES))
	}
}
