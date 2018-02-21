package org.ice1000.julia.lang.psi

import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor

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
		if (scope is PsiClass)
			if (!scope.processDeclarations(processor, state, prevParent, entrance)) return false
		if (scope == maxScope) break
		prevParent = scope
		scope = prevParent.context
	}
	return true
}