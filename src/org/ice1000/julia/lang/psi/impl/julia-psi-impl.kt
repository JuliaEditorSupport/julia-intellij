@file:JvmName("JuliaPsiImplUtils")

package org.ice1000.julia.lang.psi.impl

import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import org.ice1000.julia.lang.psi.JuliaSymbol

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

fun collectFrom(startPoint: PsiElement, name: String) = SyntaxTraverser
	.psiTraverser(startPoint)
	.filter { it is JuliaSymbol && it.text == name }
	.mapNotNull(PsiElement::getReference)
	.toList()
	.toTypedArray()
