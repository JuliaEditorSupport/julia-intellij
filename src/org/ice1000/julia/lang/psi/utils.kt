package org.ice1000.julia.lang.psi

import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StringStubIndexExtension

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

inline fun <reified T : StubBasedPsiElement<*>> findElement(project: Project, name: String): T? {
	val results : MutableCollection<T> = getStubIndex<T>().get(name, project, GlobalSearchScope.allScope(project))
	return if (results.isEmpty()) null else results.first()
}

inline fun <reified T : StubBasedPsiElement<*>> findAllElements(project: Project): List<T> {
	return getStubIndex<T>().getAllKeys(project).flatMap {
		name ->  getStubIndex<T>().get(name, project, GlobalSearchScope.allScope(project))
	}
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : PsiElement> getStubIndex(): StringStubIndexExtension<T> {
	return when (T::class.java) {
		JuliaFunction::class.java -> JuliaFunctionIndex as StringStubIndexExtension<T>
		else -> throw IllegalArgumentException("Unexpetced type: ${T::class.java}")
	}
}