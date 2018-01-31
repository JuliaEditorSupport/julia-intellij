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
			"import ",
			"using ",
			"include ",
			"export ",
			"typealias ",
			"while ",
			"for ",
			"try ",
			"if ",
			"mutable struct ",
			"struct ",
			"begin ",
			"let ",
			"quote ",
			"const ",
			"local ",
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

		// FIXME temp workaround. Should be replaced by reference resolving.
		private val builtinFunction = listOf(
			"typeof",
			"isa",
			"sqrt",
			"zero",
			"zeros",
			"zeta",
			"abs",
			"acos",
			"cos",
			"cosh",
			"asin",
			"sin",
			"sinh",
			"atan",
			"tan",
			"tanh",
			"acsc",
			"csc",
			"csch",
			"asec",
			"sec",
			"sech",
			"acot",
			"cot",
			"coth",
			"bessel",
			"broadcast",
			"accumulate",
			"accept",
			"checkindex",
			"cholfact",
			"chomp",
			"clipboard",
			"accept",
			"cumprod",
			"deserialize",
			"serialize",
			"shuffle",
			"throw",
			"println",
			"print"
		).map(LookupElementBuilder::create)
	}

	init {
		extend(CompletionType.BASIC,
			psiElement()
				.inside(psiElement(JuliaTypes.STATEMENTS)),
			JuliaCompletionProvider(statementBegin))
		extend(CompletionType.BASIC,
			psiElement()
				.inside(psiElement(JuliaTypes.STATEMENTS)),
			JuliaCompletionProvider(builtinFunction))
		extend(CompletionType.BASIC,
			psiElement()
				.inside(psiElement(JuliaTypes.TRY_CATCH)),
			JuliaCompletionProvider(tryInside))
		extend(CompletionType.BASIC,
			psiElement()
				.andOr(psiElement()
					.inside(psiElement(JuliaTypes.WHILE_EXPR)),
					psiElement()
						.inside(psiElement(JuliaTypes.FOR_EXPR))),
			JuliaCompletionProvider(loopInside))
		extend(CompletionType.BASIC,
			psiElement()
				.inside(psiElement(JuliaTypes.IF_EXPR)),
			JuliaCompletionProvider(ifInside))
		extend(CompletionType.BASIC,
			psiElement()
				.andOr(psiElement()
					.inside(psiElement(JuliaTypes.FUNCTION)),
					psiElement()
						.inside(psiElement(JuliaTypes.MACRO))),
			JuliaCompletionProvider(functionInside))
	}
}

