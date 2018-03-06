@file:JvmName("JuliaPsiImplUtils")

package org.ice1000.julia.lang.psi.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.SyntaxTraverser
import com.intellij.psi.TokenType
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.tree.IElementType
import org.ice1000.julia.lang.psi.JuliaString
import org.ice1000.julia.lang.psi.JuliaSymbol
import org.ice1000.julia.lang.psi.JuliaTypes

fun PsiElement.processDeclTrivial(
	processor: PsiScopeProcessor,
	substitutor: ResolveState,
	lastParent: PsiElement?,
	place: PsiElement): Boolean {
	var run: PsiElement? = lastParent?.prevSibling ?: lastChild
	while (run != null) {
		if (!run.processDeclarations(processor, substitutor, null, place)) return false
		run = run.prevSibling
	}
	return true
}

/**
 * @param self The declaration itself
 */
fun collectFrom(startPoint: PsiElement, name: String, self: PsiElement? = null) = SyntaxTraverser
	.psiTraverser(startPoint)
	.filter { it is JuliaSymbol && !it.isDeclaration && it.text == name && it != self }
	.mapNotNull(PsiElement::getReference)
	.toTypedArray()


val DocStringOwner.docString: JuliaString? get() = prevSiblingIgnoring(JuliaTypes.EOL, TokenType.WHITE_SPACE)
val IJuliaString.docStringOwner: DocStringOwner? get() = nextSiblingIgnoring(JuliaTypes.EOL, TokenType.WHITE_SPACE)

/**
 * nextSibling的进化版
 * 具有忽略功能
 * 可以秒杀一切不过关的IElementType
 */
inline fun <reified Psi : PsiElement> PsiElement.nextSiblingIgnoring(vararg types: IElementType): Psi? {
	var next: PsiElement? = nextSibling
	while (true) {
		val localNext = next ?: return null
		next = localNext.nextSibling
		return if (types.any { localNext.node.elementType == it }) continue
		else localNext as? Psi
	}
}

/**
 * prevSibling的进化版
 * 功能和上面那个差不多
 * 就这样过了吧
 */
inline fun <reified Psi : PsiElement> PsiElement.prevSiblingIgnoring(vararg types: IElementType): Psi? {
	var next: PsiElement? = prevSibling
	while (true) {
		val localNext = next ?: return null
		next = localNext.prevSibling
		return if (types.any { localNext.node.elementType == it }) continue
		else localNext as? Psi
	}
}
