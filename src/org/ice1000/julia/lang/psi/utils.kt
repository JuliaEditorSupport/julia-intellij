package org.ice1000.julia.lang.psi

import com.intellij.psi.PsiElement
import com.intellij.util.PsiIconUtil
import icons.JuliaIcons
import org.ice1000.julia.lang.JuliaFile
import org.ice1000.julia.lang.psi.impl.IJuliaFunctionDeclaration


val PsiElement.isBlock
	get() = this is JuliaFile ||
		this is JuliaStatements ||
		this is JuliaModuleDeclaration ||
		this is IJuliaFunctionDeclaration ||
		this is JuliaTypeDeclaration ||
		this is JuliaIfExpr ||
		this is JuliaElseIfClause ||
		this is JuliaElseClause ||
		this is JuliaWhileExpr ||
		this is JuliaAssignOp

val PsiElement.canBeNamed
	get() = this is JuliaFile ||
		this is IJuliaFunctionDeclaration ||
		this is JuliaTypeDeclaration ||
		this is JuliaAssignOp ||
		this is JuliaSymbol

val JuliaAssignOp.varOrConstIcon
	get() = if (exprList.firstOrNull()?.let { it.firstChild.node.elementType == JuliaTypes.CONST_KEYWORD } == true)
		JuliaIcons.JULIA_CONST_ICON
	else
		JuliaIcons.JULIA_VARIABLE_ICON

val JuliaIfExpr.compareText
	get() = "if " + statements.exprList.firstOrNull()?.text

val JuliaElseIfClause.compareText
	get() = "elseif " + statements.exprList.firstOrNull()?.text

val JuliaWhileExpr.compareText
	get() = expr?.text ?: "while"

val JuliaAssignOp.varOrConstName: String
	get() = exprList.first().let { if (it is JuliaSymbolLhs) it.symbolList.last().text else it.text }

val IJuliaFunctionDeclaration.functionName: String
	get() = when {
		this is JuliaFunction -> symbol?.text.toString()
		this is JuliaCompactFunction -> exprList.first().text.toString()
		else -> ""
	}

val IJuliaFunctionDeclaration.typeAndParams: String
	get() = when {
		this is JuliaFunction -> typeParameters?.text ?: ""+functionSignature?.text
		this is JuliaCompactFunction -> typeParameters?.text ?: ""+functionSignature.text
		else -> ""
	}

val IJuliaFunctionDeclaration.toText: String
	get() = functionName + typeAndParams


fun PsiElement.presentText(): String = when (this) {
	is JuliaFile -> originalFile.name
	is JuliaIfExpr -> compareText
	is JuliaElseClause -> "else"
	is JuliaElseIfClause -> compareText
	is JuliaAssignOp -> varOrConstName
	is JuliaWhileExpr -> compareText
	is JuliaTypeDeclaration -> exprList.first().text
	is JuliaModuleDeclaration -> symbol.text
	is IJuliaFunctionDeclaration -> toText
	else -> text
}

fun PsiElement.presentIcon() = when (this) {
	is JuliaFile -> PsiIconUtil.getProvidersIcon(this, 0)
	is IJuliaFunctionDeclaration -> JuliaIcons.JULIA_FUNCTION_ICON
	is JuliaModuleDeclaration -> JuliaIcons.JULIA_MODULE_ICON
	is JuliaTypeDeclaration -> JuliaIcons.JULIA_TYPE_ICON
	is JuliaWhileExpr -> JuliaIcons.JULIA_WHILE_ICON
	is JuliaAssignOp -> varOrConstIcon
	is JuliaIfExpr -> JuliaIcons.JULIA_IF_ICON
	else -> JuliaIcons.JULIA_BIG_ICON
}