package org.ice1000.julia.lang.psi

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.scope.util.PsiScopesUtil.treeWalkUp
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.julia.lang.JuliaTokenType

class JuliaSymbolRef(private val symbol: JuliaSymbol, private var refTo: PsiElement? = null) : PsiPolyVariantReference {
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
		treeWalkUp(variantsProcessor, symbol, symbol.containingFile, ResolveState.initial())
		return variantsProcessor.candidateSet.toTypedArray()
	}

	override fun getCanonicalText(): String = symbol.text
	override fun handleElementRename(newName: String) = JuliaTokenType.fromText(newName, symbol.project).let(symbol::replace)
	override fun bindToElement(element: PsiElement) = symbol.also { refTo = element }
	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		if (symbol.project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(symbol.project).resolveWithCaching(this, resolver, true, false)
	}

	private companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<JuliaSymbolRef> { ref, incompleteCode ->
			val processor = SymbolResolveProcessor(ref, incompleteCode)
			treeWalkUp(processor, ref.symbol, ref.symbol.containingFile, ResolveState.initial())
			PsiTreeUtil
				.getParentOfType(ref.symbol, JuliaStatements::class.java)
				?.processDeclarations(processor, ResolveState.initial(), ref.symbol, processor.place)
			return@PolyVariantResolver processor.candidateSet.toTypedArray()
		}
	}
}

abstract class ResolveProcessor<ResolveResult>(val place: PsiElement) : PsiScopeProcessor {
	val candidateSet = ArrayList<ResolveResult>(1)
	override fun handleEvent(event: PsiScopeProcessor.Event, o: Any?) = Unit
	protected val PsiElement.canResolve get() = this is JuliaSymbol
	protected val PsiElement.hasNoError get() = (this as? StubBasedPsiElement<*>)?.stub != null || !PsiTreeUtil.hasErrorElements(this)
	fun addCandidate(candidate: ResolveResult) = candidateSet.add(candidate)

	protected fun isInScope(element: PsiElement) = true // TODO
//		PsiTreeUtil.isAncestor(element.parent?.parent, place, false)
}

class SymbolResolveProcessor(private val name: String, place: PsiElement, val incompleteCode: Boolean) :
	ResolveProcessor<PsiElementResolveResult>(place) {
	constructor(ref: JuliaSymbolRef, incompleteCode: Boolean) : this(ref.canonicalText, ref.element, incompleteCode)

	private fun addCandidate(symbol: PsiElement) = addCandidate(PsiElementResolveResult(symbol, true))
	override fun <T : Any?> getHint(hintKey: Key<T>): T? = null
	override fun execute(element: PsiElement, resolveState: ResolveState) = when {
		candidateSet.isNotEmpty() -> false
		element.canResolve -> {
			val accessible = name == element.text && isInScope(element)
			if (accessible and element.hasNoError) addCandidate(element)
			!accessible
		}
		else -> true
	}
}

class CompletionProcessor(place: PsiElement, val incompleteCode: Boolean) :
	ResolveProcessor<LookupElementBuilder>(place) {
	constructor(ref: JuliaSymbolRef, incompleteCode: Boolean) : this(ref.element, incompleteCode)

	override fun <T : Any?> getHint(hintKey: Key<T>): T? = null
	override fun execute(element: PsiElement, resolveState: ResolveState): Boolean {
		if (element.canResolve and element.hasNoError and isInScope(element)) {
			val symbol = element.text
			// TODO add to result
		}
		return true
	}
}
