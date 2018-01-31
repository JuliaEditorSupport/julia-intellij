@file:JvmName("JuliaPsiImplUtils")

package org.ice1000.julia.lang.psi.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.tree.injected.StringLiteralEscaper
import com.intellij.psi.scope.PsiScopeProcessor
import org.ice1000.julia.lang.JuliaTokenType
import org.ice1000.julia.lang.psi.JuliaStringContent

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

fun JuliaStringContent.isValidHost() = true
fun JuliaStringContent.createLiteralTextEscaper() = StringLiteralEscaper(this)
fun JuliaStringContent.updateText(s: String) = replace(JuliaTokenType.fromText(s, project)) as JuliaStringContent
