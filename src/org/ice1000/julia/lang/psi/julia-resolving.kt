package org.ice1000.julia.lang.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.ResolveCache


class JuliaSymbolRefererence(private val symbol: JuliaSymbol) : PsiReference {
	private val range = TextRange(0, symbol.textLength)
	override fun getElement() = symbol
	override fun getRangeInElement() = range
	override fun isSoft() = true
	override fun resolve() = ResolveCache.getInstance(symbol.project).resolveWithCaching(this, resolver, true, false)
	override fun isReferenceTo(element: PsiElement?) = (element as? JuliaSymbol)?.text == symbol.text
	override fun getVariants(): Array<Any> = TODO("not implemented")
	override fun getCanonicalText(): String = TODO("not implemented")
	override fun handleElementRename(newElementName: String?): PsiElement = TODO("not implemented")
	override fun bindToElement(element: PsiElement): PsiElement = TODO("not implemented")

	private companion object ResolverHolder {
		private val resolver = ResolveCache.Resolver { ref, incompleteCode ->
			TODO()
		}
	}
}

