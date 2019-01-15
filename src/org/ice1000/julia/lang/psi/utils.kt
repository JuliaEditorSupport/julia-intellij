package org.ice1000.julia.lang.psi

import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import org.ice1000.julia.lang.JuliaFile
import org.ice1000.julia.lang.JuliaFileType

typealias Type = String // TODO temp

fun treeWalkUp(
	processor: PsiScopeProcessor,
	entrance: PsiElement,
	maxScope: PsiElement?,
	state: ResolveState = ResolveState.initial()): Boolean {
	if (!entrance.isValid) return false
	var prevParent = entrance
	var scope: PsiElement? = entrance

	while (scope != null) {
		ProgressIndicatorProvider.checkCanceled()
		if (!scope.processDeclarations(processor, state, prevParent, entrance)) return false
		if (scope == maxScope) break
		prevParent = scope
		scope = prevParent.context
	}
	return true
}

fun <A, B, C, D, E> tuple5(a: A, b: B, c: C, d: D, e: E? = null) = Tuple5(a, b, c, d, e)

data class Tuple5<out A, out B, out C, out D, out E>(val a: A, val b: B, val c: C, val d: D, val e: E)

val PsiElement.elementType get() = node.elementType
