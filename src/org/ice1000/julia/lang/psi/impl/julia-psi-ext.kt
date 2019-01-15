package org.ice1000.julia.lang.psi.impl

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

/**
 * since function body is nullable~
 */
val JuliaFunction.statements: JuliaStatements?
	get() = PsiTreeUtil.findChildOfType(this, JuliaStatements::class.java)
