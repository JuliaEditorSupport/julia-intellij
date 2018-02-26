package org.ice1000.julia.lang.editing

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import icons.JuliaIcons
import org.ice1000.julia.lang.psi.*
import org.ice1000.julia.lang.psi.impl.IJuliaFunctionDeclaration

open class JuliaCompletionProvider(private val list: List<LookupElement>) : CompletionProvider<CompletionParameters>() {
	override fun addCompletions(
		parameters: CompletionParameters,
		context: ProcessingContext,
		result: CompletionResultSet) = list.forEach(result::addElement)
}

class JuliaBasicCompletionContributor : CompletionContributor() {
	private companion object CompletionHolder {
		private val statementBegin = listOf(	//关键字s
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
		).map { LookupElementBuilder.create(it).withIcon(JuliaIcons.JULIA_BIG_ICON) }		//搞成LookupElementBuilder
		private val tryInside = listOf(			//try块里的东西
			"catch ",
			"finally"
		).map { LookupElementBuilder.create(it).withIcon(JuliaIcons.JULIA_BIG_ICON) }
		private val loopInside = listOf(		//loop块里的东西
			"break",
			"continue"
		).map { LookupElementBuilder.create(it).withIcon(JuliaIcons.JULIA_BIG_ICON) }
		private val ifInside = listOf(			//if块里的东西
			"elseif ",
			"else"
		).map { LookupElementBuilder.create(it).withIcon(JuliaIcons.JULIA_BIG_ICON) }
		private val functionInside = listOf(LookupElementBuilder.create("return"))

		// FIXME temp workaround. Should be replaced by reference resolving.
		private val builtinFunction = listOf(		//函数s
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
		).map { LookupElementBuilder.create(it).withIcon(JuliaIcons.JULIA_FUNCTION_ICON) }

		private val where = listOf(LookupElementBuilder.create("where").withIcon(JuliaIcons.JULIA_BIG_ICON))

	}

	/**
	 * 什么时候需要提示呢。。。
	 */
	override fun invokeAutoPopup(position: PsiElement, typeChar: Char) =
		position.parent !is JuliaString && typeChar in ".([\\"		//不是在字符串里 && char是这些字符里的其中之一...

	init {
		extend(CompletionType.BASIC, psiElement()		//一堆初始化(>_<)
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
