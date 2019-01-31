package org.ice1000.julia.lang.editing

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.psi.*
import kotlin.streams.toList


open class JuliaCompletionProvider(private val list: List<LookupElement>) : CompletionProvider<CompletionParameters>() {
	override fun addCompletions(
		parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) =
		list.forEach(result::addElement)
}

class JuliaModuleStubCompletionProvider : CompletionProvider<CompletionParameters>() {
	override fun addCompletions(
		parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
		val text = parameters.originalPosition?.text ?: return
		val project = parameters.editor.project ?: return
		val keys = JuliaModuleDeclarationIndex.getAllKeys(project)
		keys
			.filter { it.contains(text, true) }
			.forEach { str ->
				val k = JuliaModuleDeclarationIndex.findElementsByName(project, str)
				k.forEach {
					result.addElement(LookupElementBuilder
						.create(str)
						.withIcon(JuliaIcons.JULIA_MODULE_ICON)
						.withTypeText(it.containingFile.presentText(), true)
						.prioritized(0))
				}
			}
	}
}

class JuliaTypeStubCompletionProvider : CompletionProvider<CompletionParameters>() {
	override fun addCompletions(
		parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
		val text = parameters.originalPosition?.text ?: return
		val project = parameters.editor.project ?: return
		val keys = JuliaTypeDeclarationIndex.getAllKeys(project)
		keys
			.filter { it.contains(text, true) }
			.forEach { str ->
				val types = JuliaTypeDeclarationIndex.findElementsByName(project, str)
				types.forEach {
					result.addElement(LookupElementBuilder
						.create(str)
						.withIcon(JuliaIcons.JULIA_TYPE_ICON)
						.withTypeText(it.containingFile.presentText(), true)
						.prioritized(0))
				}
			}
		val abstractKeys = JuliaAbstractTypeDeclarationIndex.getAllKeys(project)
		abstractKeys
			.filter { it.contains(text, true) }
			.forEach { str ->
				val types = JuliaAbstractTypeDeclarationIndex.findElementsByName(project, str)
				types.forEach {
					result.addElement(LookupElementBuilder
						.create(str)
						.withIcon(JuliaIcons.JULIA_ABSTRACT_TYPE_ICON)
						.withTypeText(it.containingFile.presentText(), true)
						.prioritized(0))
				}
			}
	}
}

class JuliaBasicCompletionContributor : CompletionContributor() {
	companion object CompletionHolder {
		/**
		 * the lowest priority of completion, just make it less than [KEYWORDS_PRIORITY].
		 */
		const val BUILTIN_TAB_PRIORITY = -0xcafe
		/**
		 * This completion is lower than [JuliaSymbolRef]
		 * @see [CompletionProcessor]
		 */
		const val KEYWORDS_PRIORITY = -0xbabe

		private val statementBegin = listOf(
			"type ",
			"abstract type ",
			"primitive type ",
			"immutable ",
			"module ",
			"baremodule ",
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
				.prioritized(KEYWORDS_PRIORITY)
		}
		private val tryInside = listOf(
			"catch ",
			"finally"
		).map {
			LookupElementBuilder
				.create(it)
				.withIcon(JuliaIcons.JULIA_BIG_ICON)
				.prioritized(KEYWORDS_PRIORITY)
		}
		private val loopInside = listOf(
			"break",
			"continue"
		).map {
			LookupElementBuilder
				.create(it)
				.withIcon(JuliaIcons.JULIA_BIG_ICON)
				.withTypeText(JuliaBundle.message("julia.completion.jump.tail"), true)
				.prioritized(KEYWORDS_PRIORITY)
		}
		private val ifInside = listOf(
			"elseif ",
			"else"
		).map { LookupElementBuilder.create(it).withIcon(JuliaIcons.JULIA_BIG_ICON).prioritized(KEYWORDS_PRIORITY) }
		private val functionInside = listOf(LookupElementBuilder.create("return").prioritized(KEYWORDS_PRIORITY))

		private val builtinV06 by lazy {
			this::class.java.getResource("builtin-v0.6.txt")
				.openStream()
				.bufferedReader().lines().toList()
		}
		private val builtinV10 by lazy {
			this::class.java.getResource("builtin-v1.0.txt")
				.openStream()
				.bufferedReader().lines().toList()
		}

		val builtins by lazy {
			(builtinV06 + builtinV10).distinct()
		}

		// FIXME temp workaround. Should be replaced by reference resolving.
		private val builtinFunction = (builtinV06 + builtinV10).map {
			LookupElementBuilder
				.create(it)
				.withIcon(JuliaIcons.JULIA_FUNCTION_ICON)
				.withTypeText(
					when (it) {
						!in builtinV10 -> "0.6 Predefined symbol"
						!in builtinV06 -> "1.0 Predefined symbol"
						else -> "Predefined symbol"
					}, true)
				.prioritized(BUILTIN_TAB_PRIORITY)
		}

		private val where = listOf(
			LookupElementBuilder
				.create("where")
				.withIcon(JuliaIcons.JULIA_BIG_ICON)
				.withTypeText("Keyword", true)
				.prioritized(KEYWORDS_PRIORITY)
		)
	}

	override fun invokeAutoPopup(position: PsiElement, typeChar: Char) =
		position.parent !is JuliaString &&
			position.parent !is JuliaStringContent &&
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
				.andNot(psiElement().withParent(JuliaString::class.java))
				.andNot(psiElement().withParent(JuliaComment::class.java)),
			JuliaCompletionProvider(builtinFunction))
		extend(CompletionType.BASIC,
			psiElement()
				.inside(JuliaStatements::class.java)
				.andNot(psiElement().withParent(JuliaString::class.java))
				.andNot(psiElement().withParent(JuliaComment::class.java)),
			JuliaTypeStubCompletionProvider())
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
		extend(CompletionType.BASIC,
			psiElement()
				.inside(JuliaStatements::class.java),
			JuliaModuleStubCompletionProvider())
	}
}

/**
 * convert a LookupElementBuilder into a Prioritized LookupElement
 * @receiver [LookupElementBuilder]
 * @param priority [Int] the priority of current LookupElementBuilder
 * @return [LookupElement] Prioritized LookupElement
 */
fun LookupElementBuilder.prioritized(priority: Int): LookupElement = PrioritizedLookupElement.withPriority(this, priority.toDouble())