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
				"immutable type ",
				"typealias ",
				"module ",
				"typealias ",
				"while ",
				"for ",
				"if ",
				"struct ",
				"begin ",
				"quote ",
				"macro ",
				"function ",
				"end"
		).map(LookupElementBuilder::create)
	}

	init {
		extend(CompletionType.BASIC,
				psiElement().inside(psiElement(JuliaTypes.STATEMENTS))
						.andOr(psiElement().afterLeaf(psiElement(JuliaTypes.EOL)),
								psiElement().afterLeaf(psiElement(JuliaTypes.SEMICOLON_SYM))),
				JuliaCompletionProvider(statementBegin))
	}
}

