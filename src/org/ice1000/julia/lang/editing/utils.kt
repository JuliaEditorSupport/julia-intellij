package org.ice1000.julia.lang.editing

import com.intellij.openapi.util.SystemInfo
import com.intellij.psi.PsiElement
import org.ice1000.julia.lang.JuliaFile
import org.ice1000.julia.lang.psi.*
import org.ice1000.julia.lang.psi.impl.IJuliaFunctionDeclaration
import java.math.BigInteger

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
	is JuliaIfExpr -> "if ${children.getOrNull(1)?.text.orEmpty()}"
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

fun checkIntType(value: BigInteger): String = when (value) {
	in NumeralType.Int32.range ->
		if (SystemInfo.is32Bit) NumeralType.Int32 else NumeralType.Int64
	in NumeralType.Int64.range -> NumeralType.Int64
	in NumeralType.Int128.range -> NumeralType.Int128
	else -> NumeralType.BigInt
}.name

object JuliaRValueLiteral {
	fun integer(elem: PsiElement): String {
		val code = elem.text
		if (code.any { !it.isLetterOrDigit() }) return ""
		val (base, intText) = when {
			code.startsWith("0x") -> 16 to code.drop(2)
			code.startsWith("0b") -> 2 to code.drop(2)
			code.startsWith("0o") -> 8 to code.drop(2)
			else -> 10 to code
		}
		val value = BigInteger(intText, base)
		return checkIntType(value)
	}

	fun array(elem: PsiElement): String? {
		(elem as JuliaArray).run {
			if (this.typeParameters != null) return null
			// TODO getTypeByElements
			val elementType = ""
			return "Array{$elementType}"
		}
	}
}