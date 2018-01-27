package org.ice1000.julia.lang.editing

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.util.ProcessingContext
import org.ice1000.julia.lang.psi.JuliaTypes

class JuliaBasicCompletionContributor : CompletionContributor() {
	class JuliaCompletionProvider(private val list: List<LookupElement>) : CompletionProvider<CompletionParameters>() {
		override fun addCompletions(
			parameters: CompletionParameters,
			context: ProcessingContext,
			result: CompletionResultSet) = list.forEach(result::addElement)
	}

	private companion object CompletionHolder {
		private val statementBegin = listOf(
			"type ",
			"abstract type ",
			"primitive type ",
			"immutable ",
			"module ",
			"typealias ",
			"while ",
			"for ",
			"try ",
			"if ",
			"mutable struct ",
			"struct ",
			"begin ",
			"quote ",
			"macro ",
			"function ",
			"end"
		).map(LookupElementBuilder::create)
		private val tryInside = listOf(
			"catch ",
			"finally"
		).map(LookupElementBuilder::create)
		private val loopInside = listOf(
			"break",
			"continue"
		).map(LookupElementBuilder::create)
		private val ifInside = listOf(
			"elseif ",
			"else"
		).map(LookupElementBuilder::create)
		private val functionInside = listOf(LookupElementBuilder.create("return"))
	}

	init {
		extend(CompletionType.BASIC,
			psiElement()
				.inside(psiElement(JuliaTypes.STATEMENTS))
				.andOr(psiElement()
					.afterLeaf(psiElement(JuliaTypes.EOL)),
					psiElement()
						.afterLeaf(psiElement(JuliaTypes.SEMICOLON_SYM))),
			JuliaCompletionProvider(statementBegin))
		extend(CompletionType.BASIC,
			psiElement()
				.inside(psiElement(JuliaTypes.TRY_CATCH_STATEMENT))
				.andOr(psiElement()
					.afterLeaf(psiElement(JuliaTypes.EOL)),
					psiElement()
						.afterLeaf(psiElement(JuliaTypes.SEMICOLON_SYM))),
			JuliaCompletionProvider(tryInside))
		extend(CompletionType.BASIC,
			psiElement()
				.andOr(psiElement()
					.inside(psiElement(JuliaTypes.WHILE_EXPR)),
					psiElement()
						.inside(psiElement(JuliaTypes.FOR_EXPR)))
				.andOr(psiElement()
					.afterLeaf(psiElement(JuliaTypes.EOL)),
					psiElement()
						.afterLeaf(psiElement(JuliaTypes.SEMICOLON_SYM))),
			JuliaCompletionProvider(loopInside))
		extend(CompletionType.BASIC,
			psiElement()
				.inside(psiElement(JuliaTypes.IF_EXPR))
				.andOr(psiElement()
					.afterLeaf(psiElement(JuliaTypes.EOL)),
					psiElement()
						.afterLeaf(psiElement(JuliaTypes.SEMICOLON_SYM))),
			JuliaCompletionProvider(ifInside))
		extend(CompletionType.BASIC,
			psiElement().inside(psiElement(JuliaTypes.FUNCTION)),
			JuliaCompletionProvider(functionInside))
	}
}

