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
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		if (element is DocfmtConfig) {
			val key = element.firstChild
			val value = element.value
			when (key.text) {
				"TabWidth" -> number(element, holder)
				"IndentWidth" -> number(element, holder)
				"UseTab" -> when (value.text) {
					"false" -> unused(element, holder)
					"true" -> Unit
					else -> invalidValue(value, holder)
				}
				"AlignAfterOpenBracket" -> when (value.text) {
					"Align" -> unused(element, holder)
					"AlwaysBreak", "DontAlign" -> Unit // OK
					else -> invalidValue(value, holder)
				}
				"IndentEXPR" -> Unit // TODO
				else -> holder.createErrorAnnotation(key, JuliaBundle.message("docfmt.lint.invalid", key.text)).run {
					highlightType = ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
				}
			}
		} else return
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
