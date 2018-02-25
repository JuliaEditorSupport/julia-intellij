package org.ice1000.julia.lang.editing

import com.intellij.psi.PsiElement
import com.intellij.util.PsiIconUtil
import icons.JuliaIcons
import org.ice1000.julia.lang.JuliaFile
import org.ice1000.julia.lang.orFalse
import org.ice1000.julia.lang.psi.*
import org.ice1000.julia.lang.psi.impl.IJuliaFunctionDeclaration

/**
 * Used in treeViewTokens
 */
val PsiElement.treeViewTokens
	get() = this is JuliaFile ||
		this is JuliaModuleDeclaration ||
		this is IJuliaFunctionDeclaration ||
		this is JuliaTypeDeclaration ||
		this is JuliaIfExpr ||
		this is JuliaElseIfClause ||
		this is JuliaElseClause ||
		this is JuliaWhileExpr ||
		this is JuliaAssignOp ||
		this is JuliaTypeOp ||
		this is JuliaSymbol

val PsiElement.canBeNamed
	get() = this is JuliaFile ||
		this is IJuliaFunctionDeclaration ||
		this is JuliaTypeDeclaration ||
		this is JuliaAssignOp ||
		this is JuliaSymbol

val IJuliaFunctionDeclaration.toText get() = "$name$typeParamsText$paramsText"

val PsiElement.isFieldInTypeDeclaration: Boolean
	get() = parent is JuliaStatements && parent.parent is JuliaTypeDeclaration

fun PsiElement.presentText(): String = when (this) {
	is JuliaFile -> originalFile.name
	is JuliaIfExpr -> "if ${statements.exprList.firstOrNull()?.text.orEmpty()}"
	is JuliaElseClause -> "else"
	is JuliaElseIfClause -> "elseif ${statements.exprList.firstOrNull()?.text.orEmpty()}"
	is JuliaAssignOp -> exprList.first().let { if (it is JuliaSymbolLhs) it.symbolList.last().text else it.text }
	is JuliaWhileExpr -> "while ${expr?.text.orEmpty()}"
	is JuliaTypeDeclaration -> "type ${exprList.first().text}"
	is JuliaModuleDeclaration -> "module ${symbol.text}"
	is IJuliaFunctionDeclaration -> toText
	is JuliaTypeOp -> exprList.first().text
	else -> text
}
