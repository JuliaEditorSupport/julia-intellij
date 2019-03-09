package org.ice1000.julia.lang.psi.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.julia.lang.orFalse
import org.ice1000.julia.lang.psi.*


val IJuliaSymbol.isQuoteCall: Boolean
	get() = (parent is JuliaQuoteOp) || (parent is JuliaExprWrapper && parent.parent is JuliaQuoteIndexing)

val IJuliaSymbol.isConstName: Boolean
	get() = (parent is JuliaSymbolLhs) || isConstNameRef

val IJuliaSymbol.isConstNameRef: Boolean
	get() = (reference?.resolve() as? JuliaSymbol)?.isConstName.orFalse()

val IJuliaSymbol.isTypeNameRef: Boolean
	get() = (reference?.resolve() as? JuliaSymbol)?.symbolKind == JuliaSymbolKind.TypeName

val IJuliaSymbol.isModuleNameRef: Boolean
	get() = (reference?.resolve() as? JuliaSymbol)?.symbolKind == JuliaSymbolKind.ModuleName

val IJuliaSymbol.isInUsingExpr: Boolean
	get() = this.parent is JuliaMemberAccess && this.parent.parent is JuliaUsing

val IJuliaSymbol.typeFoundFromStub: Boolean
	get() = JuliaTypeDeclarationIndex.findElementsByName(this.project, this.text).isNotEmpty()

val IJuliaSymbol.isAbstractTypeRef: Boolean
	get() = JuliaAbstractTypeDeclarationIndex.findElementsByName(this.project, this.text).isNotEmpty()
/**
 * since function body is nullable~
 */
val JuliaFunction.statements: JuliaLazyParseableBlockImpl?
	get() = PsiTreeUtil.findChildOfType(this, JuliaLazyParseableBlockImpl::class.java)

val PsiElement.isSuperTypeExpr: Boolean
	get() = this.prevRealSibling?.elementType === JuliaTypes.SUBTYPE_SYM

val PsiElement.prevRealSibling: PsiElement?
	get() {
		var pre = this.prevSibling
		while (pre != null) {
			if (pre is PsiWhiteSpace || pre.elementType == JuliaTypes.EOL) {
				pre = pre.prevSibling
			} else {
				return pre
			}
		}
		return pre
	}

val PsiElement.nextRealSibling: PsiElement?
	get() {
		var next = this.nextSibling
		while (next != null) {
			if (next is PsiWhiteSpace) {
				next = next.nextSibling
			} else {
				return next
			}
		}
		return next
	}
