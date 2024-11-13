/*
 *     Julia language support plugin for Intellij-based IDEs.
 *     Copyright (C) 2024 julia-intellij contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

@file:JvmName("JuliaPsiImplUtils")

package org.ice1000.julia.lang.psi.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.SyntaxTraverser
import com.intellij.psi.TokenType
import com.intellij.psi.impl.source.tree.LazyParseablePsiElement
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.tree.IElementType
import org.ice1000.julia.lang.psi.*

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
	.filter { it is JuliaSymbol && !it.symbolKind.isDeclaration && it.text == name && it != self }
	.mapNotNull(PsiElement::getReference)
	.let { if (self != null) it.filter { it.isReferenceTo(self) } else it }
	.toTypedArray()

val DocStringOwner.docString: JuliaString? get() = prevSiblingIgnoring(JuliaTypes.EOL, TokenType.WHITE_SPACE)
val IJuliaString.docStringOwner: DocStringOwner? get() = nextSiblingIgnoring(JuliaTypes.EOL, TokenType.WHITE_SPACE)

inline fun <reified Psi : PsiElement> PsiElement.nextSiblingIgnoring(vararg types: IElementType): Psi? {
	var next: PsiElement? = nextSibling
	while (true) {
		val localNext = next ?: return null
		next = localNext.nextSibling
		return if (types.any { localNext.node.elementType == it }) continue
		else localNext as? Psi
	}
}

inline fun <reified Psi : PsiElement> PsiElement.prevSiblingIgnoring(vararg types: IElementType): Psi? {
	var next: PsiElement? = prevSibling
	while (true) {
		val localNext = next ?: return null
		next = localNext.prevSibling
		return if (types.any { localNext.node.elementType == it }) continue
		else localNext as? Psi
	}
}

fun PsiElement.childrenBefore(type: IElementType): List<PsiElement> {
	val ret = ArrayList<PsiElement>()
	var next: PsiElement? = firstChild
	while (true) {
		next = next?.nextSibling ?: return ret
		if (next.node.elementType == type) return ret
		ret += next
	}
}

class JuliaLazyParseableBlockImpl(type: IElementType, buffer: CharSequence?) : LazyParseablePsiElement(type, buffer), JuliaLazyParseableBlock {
	override val statements: JuliaStatements?
		get() = findChildByClass(JuliaStatements::class.java)

	override fun toString(): String = tokenType.toString()
}
