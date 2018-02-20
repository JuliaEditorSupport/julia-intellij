package org.ice1000.julia.lang.psi

import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.psi.*
import com.intellij.psi.scope.JavaScopeProcessorEvent
import com.intellij.psi.scope.PsiScopeProcessor

/**
 * 喵真的不知道要放在哪里desu。。。
 */
fun treeWalkUp(processor: PsiScopeProcessor,
							 entrance: PsiElement,
							 maxScope: PsiElement?,
							 state: ResolveState = ResolveState.initial()): Boolean {
	if (!entrance.isValid) return false

	var prevParent = entrance
	var scope : PsiElement? = entrance

	while(scope != null) {
		ProgressIndicatorProvider.checkCanceled()

		if(scope is PsiClass) // processor.handleEvent(JavaScopeProcessorEvent.SET_CURRENT_FILE_CONTEXT, scope)
		if (! scope.processDeclarations(processor, state, prevParent, entrance)) return false
		if(scope is PsiModifierListOwner && scope !is PsiParameter) {
			/*
			scope.modifierList?.run {
				if(hasModifierProperty(PsiModifier.STATIC))
					processor.handleEvent(JavaScopeProcessorEvent.START_STATIC, null)
			}
			*/
		}

		if(scope == maxScope) break
		prevParent = scope
		scope = prevParent.context

//		processor.handleEvent(JavaScopeProcessorEvent.CHANGE_LEVEL, null)
	}

	return true
}