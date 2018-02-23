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
		).map { LookupElementBuilder.create(it).withIcon(JuliaIcons.JULIA_BIG_ICON) }
		private val tryInside = listOf(
			"catch ",
			"finally"
		).map { LookupElementBuilder.create(it).withIcon(JuliaIcons.JULIA_BIG_ICON) }
		private val loopInside = listOf(
			"break",
			"continue"
		).map { LookupElementBuilder.create(it).withIcon(JuliaIcons.JULIA_BIG_ICON) }
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
		).map { LookupElementBuilder.create(it).withIcon(JuliaIcons.JULIA_FUNCTION_ICON) }

		private val where = listOf(LookupElementBuilder.create("where").withIcon(JuliaIcons.JULIA_BIG_ICON))

	}

	override fun invokeAutoPopup(position: PsiElement, typeChar: Char) =
		position.parent !is JuliaString && typeChar in ".([\\"

	init {
		extend(CompletionType.BASIC, psiElement()
			.inside(JuliaStatements::class.java)
			.andNot(psiElement().withParent(JuliaString::class.java)),
			JuliaContextCompletionProvider())
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

class JuliaContextCompletionProvider : CompletionProvider<CompletionParameters>() {

	//FIXME: stupid code
	override fun addCompletions(
		parameters: CompletionParameters,
		context: ProcessingContext?,
		result: CompletionResultSet) {
		val list = ArrayList<PsiElement>()
		val pos = parameters.position
		val file = pos.containingFile
		file.children.forEach { list.add(it) }
		while (list.isNotEmpty()) {
			val elem = list.removeAt(list.lastIndex)
			if (elem.canBeNamed) {
				val text = elem.presentText()
//				the type of return value,show at right of popup
				val typeText = elem.typeText()
//				tail text, it will not be completed by Enter Key press
				val tailText = elem.tailText()
				val icon = elem.presentIcon()
				result.addElement(
					LookupElementBuilder
						.create(text)
						.withIcon(icon)
						.withTailText(tailText, true)
						.withTypeText(typeText, true))
			}
			elem.children.forEach { list.add(it) }
		}
	}
}

/**
 * after text but it won't be completed
 */
private fun PsiElement.tailText() = when (this) {
	is IJuliaFunctionDeclaration -> "$typeParamsText$paramsText"
	else -> ""
}

/**
 * right side of popup
 */
private fun PsiElement.typeText() = when (this) {
//the type of return value
	is IJuliaFunctionDeclaration -> ""
	else -> ""
}

