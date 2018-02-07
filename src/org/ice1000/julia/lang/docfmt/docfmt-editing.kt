package org.ice1000.julia.lang.docfmt

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.docfmt.psi.*
import org.ice1000.julia.lang.editing.JuliaCompletionProvider
import org.ice1000.julia.lang.editing.JuliaRemoveElementIntention

class DocfmtAnnotator : Annotator {
	private companion object {
		internal val noWsOpGroup = listOf("8", "13", "14", "16")
		private val indentArgs = listOf(
			"CSTParser.Begin", "CSTParser.Quote",
			"CSTParser.For", "CSTParser.While", "CSTParser.FunctionDef", "CSTParser.Macro",
			"CSTParser.Struct", "CSTParser.Let", "CSTParser.Try", "CSTParser.If", "CSTParser.Mutable"
		)
	}

	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		if (element is DocfmtConfig) {
			val key = element.firstChild
			val value = element.value
			fun unused() = holder.createWeakWarningAnnotation(element, JuliaBundle.message("docfmt.lint.default")).run {
				highlightType = ProblemHighlightType.LIKE_UNUSED_SYMBOL
				registerFix(JuliaRemoveElementIntention(element, JuliaBundle.message("docfmt.lint.remove")))
			}

			fun invalidValue(element: PsiElement) = holder.createErrorAnnotation(element, JuliaBundle.message("docfmt.lint.invalid-val", element.text)).run {
				highlightType = ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
			}

			fun number() {
				if (value.firstChild.node.elementType != DocfmtTypes.INT) invalidValue(value)
				else if (value.text == "4") unused()
			}

			fun boolean(default: Boolean = false) {
				when (element.value.text) {
					"$default" -> unused()
					"${!default}" -> Unit
					else -> invalidValue(value)
				}
			}

			when (key.text) {
				"TabWidth" -> number()
				"IndentWidth" -> number()
				"UseTab" -> boolean()
				"AlignAfterOpenBracket" -> when (value.text) {
					"Align" -> unused()
					"AlwaysBreak", "DontAlign" -> Unit // OK
					else -> invalidValue(value)
				}
				"IndentEXPR" -> if (value.text !in indentArgs) invalidValue(value)
				"KW_WS" -> boolean(true)
				"NewLineEOF" -> boolean()
				"StripLineEnds" -> boolean()
				"No_WS_OP_group" -> if (value.text !in noWsOpGroup) invalidValue(value)
				"No_WS_OP_indv" -> if (value.firstChild.node.elementType == DocfmtTypes.INT) invalidValue(value)
				else -> holder.createErrorAnnotation(key, JuliaBundle.message("docfmt.lint.invalid", key.text)).run {
					highlightType = ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
				}
			}
		} else return
	}
}

class DocfmtCompletionContributor : CompletionContributor() {
	internal companion object ValueCompleter : CompletionProvider<CompletionParameters>() {
		override fun addCompletions(p: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
			val value = p.originalPosition?.parent as? DocfmtValue ?: return
			val key = (value.parent as? DocfmtConfig)?.firstChild ?: return
			when (key.text) {
				"KW_WS", "NewLineEOF", "StripLineEnds", "UseTab" -> BOOLEAN.forEach(result::addElement)
				"AlignAfterOpenBracket" -> ALIGN.forEach(result::addElement)
				"No_WS_OP_indv" -> OPERATORS.forEach(result::addElement)
			}
		}

		private val BOOLEAN = listOf("true", "false").map(LookupElementBuilder::create)
		private val S_OPERATORS = "+-*/!$%^&().~:?|\\[]{}<>"
			.toCharArray().map(LookupElementBuilder::create)
		private val D_OPERATORS = listOf("<=", ">=", "==", "===", "!==", "|>", "<|")
			.map(LookupElementBuilder::create)
		private val OPERATORS = S_OPERATORS + D_OPERATORS
		private val ALIGN = listOf("Align", "AlwaysBreak", "DontAlign").map(LookupElementBuilder::create)
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
	}

	override fun invokeAutoPopup(position: PsiElement, typeChar: Char): Boolean {
		return typeChar in "= \n\t" || super.invokeAutoPopup(position, typeChar)
	}

	init {
		extend(
			CompletionType.BASIC,
			psiElement().andNot(psiElement().afterLeaf(psiElement(DocfmtTypes.EQ_SYM))),
			JuliaCompletionProvider(KEYS))
		extend(
			CompletionType.BASIC,
			psiElement().afterLeaf(psiElement(DocfmtTypes.EQ_SYM)),
			ValueCompleter)
	}
}
