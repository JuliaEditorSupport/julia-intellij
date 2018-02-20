package org.ice1000.julia.lang.psi

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.julia.lang.JuliaTokenType

/**
 * @author ice1000
 * @param symbol should be [org.ice1000.julia.lang.psi.JuliaSymbol] or [org.ice1000.julia.lang.psi.JuliaMacroSymbol]
 */
class JuliaSymbolRef(private val symbol: PsiElement, private var refTo: PsiElement? = null) : PsiPolyVariantReference {
	private val range = TextRange(0, symbol.textLength)
	override fun equals(other: Any?) = (other as? JuliaSymbolRef)?.element == element
	override fun hashCode() = symbol.hashCode()
	override fun getElement() = symbol
	override fun getRangeInElement() = range
	override fun isSoft() = true
	override fun resolve() = multiResolve(false).firstOrNull()?.element
	override fun isReferenceTo(element: PsiElement?) = element == refTo || (element as? JuliaSymbol)?.text == symbol.text
	override fun getVariants(): Array<LookupElementBuilder> {
		val variantsProcessor = CompletionProcessor(this, true)
		// FIXME #96
		treeWalkUp(variantsProcessor, symbol, symbol.containingFile)
		return variantsProcessor.candidateSet.toTypedArray()
	}

	override fun getCanonicalText(): String = symbol.text
	override fun handleElementRename(newName: String) = JuliaTokenType.fromText(newName, symbol.project).let(symbol::replace)
	override fun bindToElement(element: PsiElement) = symbol.also { refTo = element }
	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = element.containingFile?.takeUnless { symbol.project.isDisposed } ?: return emptyArray()
		val resolver = when (symbol) {
			is JuliaSymbol -> symbolResolver
			is JuliaMacroSymbol -> macroResolver
			else -> return emptyArray()
		}
		return ResolveCache.getInstance(symbol.project).resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	private companion object ResolverHolder {
		private val symbolResolver = ResolveCache.PolyVariantResolver<JuliaSymbolRef> { ref, incompleteCode ->
			val processor = SymbolResolveProcessor(ref, incompleteCode)
			// FIXME #96
			treeWalkUp(processor, ref.symbol, ref.symbol.containingFile)
			PsiTreeUtil
				.getParentOfType(ref.symbol, JuliaStatements::class.java)
				?.processDeclarations(processor, ResolveState.initial(), ref.symbol, processor.place)
			return@PolyVariantResolver processor.candidateSet.toTypedArray()
		}

		private val macroResolver = ResolveCache.PolyVariantResolver<JuliaSymbolRef> { ref, incompleteCode ->
			val processor = MacroSymbolResolveProcessor(ref, incompleteCode)
			// FIXME #96
			treeWalkUp(processor, ref.symbol, ref.symbol.containingFile)
			PsiTreeUtil
				.getParentOfType(ref.symbol, JuliaStatements::class.java)
				?.processDeclarations(processor, ResolveState.initial(), ref.symbol, processor.place)
			return@PolyVariantResolver processor.candidateSet.toTypedArray()
		}
	}
}

abstract class ResolveProcessor<ResolveResult>(val place: PsiElement) : PsiScopeProcessor {
	abstract val candidateSet: ArrayList<ResolveResult>
	override fun handleEvent(event: PsiScopeProcessor.Event, o: Any?) = Unit
	override fun <T : Any?> getHint(hintKey: Key<T>): T? = null
	protected val PsiElement.canResolve get() = this is JuliaSymbol
	protected val PsiElement.hasNoError get() = (this as? StubBasedPsiElement<*>)?.stub != null || !PsiTreeUtil.hasErrorElements(this)
	// TODO add definitions
	protected fun isInScope(element: PsiElement) = if (element is JuliaSymbol) when {
		element.isFunctionName ||
			element.isModuleName ||
			element.isMacroName ||
			element.isAbstractTypeName ||
			element.isPrimitiveTypeName ||
			element.isTypeName -> default(element)
		else -> false
	} else false

	private fun default(element: PsiElement) =
		PsiTreeUtil.isAncestor(PsiTreeUtil.getParentOfType(element, JuliaStatements::class.java), place, false)
}

open class SymbolResolveProcessor(
	@JvmField protected val name: String,
	place: PsiElement,
	private val incompleteCode: Boolean) :
	ResolveProcessor<PsiElementResolveResult>(place) {
	constructor(ref: JuliaSymbolRef, incompleteCode: Boolean) : this(ref.canonicalText, ref.element, incompleteCode)

	override val candidateSet = ArrayList<PsiElementResolveResult>(3)
	protected open fun accessible(element: PsiElement) = name == element.text && isInScope(element)
	override fun execute(element: PsiElement, resolveState: ResolveState) = when {
		candidateSet.isNotEmpty() -> false
		element.canResolve -> {
			val accessible = accessible(element)
			if (accessible and element.hasNoError) candidateSet += PsiElementResolveResult(element, true)
			!accessible
		}
		else -> true
	}
}

class MacroSymbolResolveProcessor(name: String, place: PsiElement, incompleteCode: Boolean) :
	SymbolResolveProcessor(name, place, incompleteCode) {
	constructor(ref: JuliaSymbolRef, incompleteCode: Boolean) : this(ref.canonicalText, ref.element, incompleteCode)

	override fun accessible(element: PsiElement) = "@${element.text}" == name && isInScope(element)
}

class CompletionProcessor(place: PsiElement, private val incompleteCode: Boolean) :
	ResolveProcessor<LookupElementBuilder>(place) {
	constructor(ref: JuliaSymbolRef, incompleteCode: Boolean) : this(ref.element, incompleteCode)

	override val candidateSet = ArrayList<LookupElementBuilder>(20)
	override fun execute(element: PsiElement, resolveState: ResolveState): Boolean {
		if (element.canResolve and element.hasNoError and isInScope(element)) {
			val symbol = element.text
			// TODO add to result
		}
		return true
	}
}
