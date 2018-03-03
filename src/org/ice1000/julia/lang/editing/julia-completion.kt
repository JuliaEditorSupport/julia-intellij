package org.ice1000.julia.lang.editing

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import icons.JuliaIcons
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.psi.*

open class JuliaCompletionProvider(private val list: List<LookupElement>) : CompletionProvider<CompletionParameters>() {
	override fun addCompletions(
		parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) =
		list.forEach(result::addElement)
}

class JuliaBasicCompletionContributor : CompletionContributor() {
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
		).map {
			LookupElementBuilder
				.create(it)
				.withIcon(JuliaIcons.JULIA_BIG_ICON)
				.withTypeText(JuliaBundle.message("julia.completion.keyword.tail"), true)
		}
		private val tryInside = listOf(
			"catch ",
			"finally"
		).map {
			LookupElementBuilder
				.create(it)
				.withIcon(JuliaIcons.JULIA_BIG_ICON)
		}
		private val loopInside = listOf(
			"break",
			"continue"
		).map {
			LookupElementBuilder
				.create(it)
				.withIcon(JuliaIcons.JULIA_BIG_ICON)
				.withTypeText(JuliaBundle.message("julia.completion.jump.tail"), true)
		}
		private val ifInside = listOf(
			"elseif ",
			"else"
		).map { LookupElementBuilder.create(it).withIcon(JuliaIcons.JULIA_BIG_ICON) }
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
		).map {
			LookupElementBuilder
				.create(it)
				.withIcon(JuliaIcons.JULIA_FUNCTION_ICON)
				.withTypeText("Predefined symbol", true)
				.let { PrioritizedLookupElement.withPriority(it, 0.1) }
		}

		private val where = listOf(
			LookupElementBuilder
				.create("where")
				.withIcon(JuliaIcons.JULIA_BIG_ICON)
				.withTypeText("Keyword", true))
	}

	override fun invokeAutoPopup(position: PsiElement, typeChar: Char) =
		position.parent !is JuliaString &&
			position.parent !is JuliaCommand &&
			typeChar in ".([ "

	init {
		extend(CompletionType.BASIC, psiElement()
			.inside(JuliaFunction::class.java)
			.afterLeaf(")")
			.andNot(psiElement()
				.withParent(JuliaStatements::class.java)),
			JuliaCompletionProvider(where))
		extend(CompletionType.BASIC,
			psiElement()
				.inside(JuliaStatements::class.java).andNot(
					psiElement().withParent(JuliaString::class.java)),
			JuliaCompletionProvider(statementBegin))
		extend(CompletionType.BASIC,
			psiElement()
				.inside(JuliaStatements::class.java)
				.andNot(psiElement().withParent(JuliaString::class.java)),
			JuliaCompletionProvider(builtinFunction))
		extend(CompletionType.BASIC,
			psiElement()
				.inside(JuliaStatements::class.java)
				.andNot(psiElement().withParent(JuliaString::class.java)),
			JuliaCompletionProvider(tryInside))
		extend(CompletionType.BASIC,
			psiElement()
				.andOr(
					psiElement().inside(JuliaWhileExpr::class.java),
					psiElement().inside(JuliaForExpr::class.java))
				.andNot(psiElement().withParent(JuliaString::class.java)),
			JuliaCompletionProvider(loopInside))
		extend(CompletionType.BASIC,
			psiElement()
				.inside(JuliaIfExpr::class.java)
				.andNot(psiElement().withParent(JuliaString::class.java)),
			JuliaCompletionProvider(ifInside))
		extend(CompletionType.BASIC,
			psiElement()
				.andOr(
					psiElement().inside(JuliaFunction::class.java),
					psiElement().inside(JuliaMacro::class.java))
				.andNot(psiElement().withParent(JuliaString::class.java)),
			JuliaCompletionProvider(functionInside))
	}
}
