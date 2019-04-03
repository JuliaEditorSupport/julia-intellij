package org.ice1000.julia.lang.editing

import com.intellij.openapi.util.SystemInfo
import com.intellij.psi.PsiElement
import com.intellij.util.PsiIconUtil
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.JuliaFile
import org.ice1000.julia.lang.psi.*
import org.ice1000.julia.lang.psi.impl.IJuliaFunctionDeclaration
import org.ice1000.julia.lang.psi.impl.refTypeName
import java.lang.IllegalArgumentException
import java.math.BigInteger
import javax.swing.Icon

/**
 * Used in treeViewTokens
 */
val PsiElement.treeViewTokens
	get() = this is JuliaFile ||
		this is JuliaModuleDeclaration ||
		this is IJuliaFunctionDeclaration ||
		this is JuliaTypeDeclaration ||
		this is JuliaAbstractTypeDeclaration ||
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

val IJuliaFunctionDeclaration.toText get() = "${nameIdentifier?.text.orEmpty()}$typeParamsText$paramsText"

val PsiElement.isFieldInTypeDeclaration: Boolean
	get() = parent is JuliaStatements && parent.parent is JuliaTypeDeclaration

fun PsiElement.presentText(): String = when (this) {
	is JuliaFile -> originalFile.name
	is JuliaForExpr -> "for ${multiIndexerList.joinToString { it.text }}"
	is JuliaIfExpr -> "if ${expr?.text.orEmpty()}"
	is JuliaElseClause -> "else"
	is JuliaElseIfClause -> "elseif ${expr?.text.orEmpty()}"
	is JuliaAssignOp -> exprList.first().let { if (it is JuliaSymbolLhs) it.symbolList.last().text else it.text }
	is JuliaWhileExpr -> "while ${expr?.text.orEmpty()}"
	is JuliaTypeDeclaration -> "type ${exprList.firstOrNull()?.text.orEmpty()}"
	is JuliaModuleDeclaration -> "module ${symbol?.text.orEmpty()}"
	is IJuliaFunctionDeclaration -> toText
	is JuliaTypeOp -> exprList.firstOrNull()?.text.orEmpty()
	else -> cutText(text, LONG_TEXT_MAX)
}

fun PsiElement.getIcon(): Icon = when (this) {
	is JuliaFile -> PsiIconUtil.getProvidersIcon(this, 0) ?: JuliaIcons.JULIA_ICON
	is IJuliaFunctionDeclaration -> JuliaIcons.JULIA_FUNCTION_ICON
	is JuliaModuleDeclaration -> JuliaIcons.JULIA_MODULE_ICON
	is JuliaTypeDeclaration -> JuliaIcons.JULIA_TYPE_ICON
	is JuliaWhileExpr -> JuliaIcons.JULIA_WHILE_ICON
	is JuliaTypeOp -> JuliaIcons.JULIA_VARIABLE_ICON
	is JuliaIfExpr -> JuliaIcons.JULIA_IF_ICON
	is JuliaAssignOp ->
		if (this.exprList.firstOrNull()?.let { it.firstChild.node.elementType == JuliaTypes.CONST_KEYWORD }.orFalse())
			JuliaIcons.JULIA_CONST_ICON
		else JuliaIcons.JULIA_VARIABLE_ICON
	is JuliaSymbol -> if (this.isFieldInTypeDeclaration) JuliaIcons.JULIA_VARIABLE_ICON else JuliaIcons.JULIA_BIG_ICON
	else -> JuliaIcons.JULIA_BIG_ICON
}

fun checkIntType(value: BigInteger): String = when (value) {
	in NumeralType.Int32.range ->
		if (SystemInfo.is32Bit) NumeralType.Int32 else NumeralType.Int64
	in NumeralType.Int64.range -> NumeralType.Int64
	in NumeralType.Int128.range -> NumeralType.Int128
	else -> NumeralType.BigInt
}.name

object JuliaRValueLiteral {
	fun parseAssignedSymbolType(elem: JuliaSymbol): String? {
		val parent = (elem.parent as? JuliaAssignOp) ?: return null
		val rValue = parent.exprList.lastOrNull() ?: return null
		return parseType(rValue)
	}

	/**
	 *
	 * @param elem PsiElement
	 * @return String? null will not show type hint
	 */
	fun parseType(elem: PsiElement): String? {
		return when (elem) {
			is JuliaArray -> JuliaRValueLiteral.array(elem)
			is JuliaInteger -> JuliaRValueLiteral.integer(elem)
			is JuliaFloatLit -> "Float64"
			is JuliaCharLit -> "Char"
			is JuliaCommand -> "Cmd"
			is JuliaString -> "String"
			is JuliaVersionNumber -> "VersionNumber"
			is JuliaExpr -> elem.type.takeIf { it != null }
			else -> null
		}?.applyTypeToElement(elem)
	}

	private fun String?.applyTypeToElement(elem: PsiElement) = apply {
		(elem as? JuliaExpr)?.type = this
	}

	fun integer(elem: JuliaInteger): String {
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

	fun array(array: JuliaArray): String? {
		if (array.typeParameters != null) return null
		val exprList = array.exprList
		exprList.forEach { println(it.text) }
		if (exprList.isEmpty()) return null
		val firstType = exprList.firstOrNull()?.let { it.type ?: it.refTypeName } ?: return null
		if (exprList.size == 1) return "Array{$firstType}"
		// strange code on account of the AST of last element you input is uncertain.
		val arrayType = if (
			exprList.subList(0, exprList.lastIndex - 1).all { it.type == firstType || it.refTypeName == firstType }
			&& parseType(exprList.last()) == firstType
		) firstType
		else { // size == 1
			"Any"
		}
		return "Array{$arrayType}"
	}
}

/**
 * VInt is UInt32, but Kotlin doesn't have it until version 1.3 .
 */
typealias VInt = Long

/**
 * If you are puzzled with the code style for some function in this object, do not ask why it is~
 * What a nice julia-style!
 */
object JuliaInKotlin {
	data class VersionNumber(
		val major: VInt,
		val minor: VInt,
		val patch: VInt,
		val prerelease: List<String> = emptyList(),
		val build: List<String> = emptyList()) {

		init {
			val preRegex = Regex(JULIA_VERSION_NUMBER_REGEX_PRE_I, setOf(RegexOption.IGNORE_CASE))

			major >= 0 || throw(IllegalArgumentException("invalid negative major version: $major"))
			minor >= 0 || throw(IllegalArgumentException("invalid negative major version: $major"))
			patch >= 0 || throw(IllegalArgumentException("invalid negative major version: $major"))
			for (ident in prerelease) {
				try {
					val int = ident.toInt()
					int >= 0 || throw(IllegalArgumentException("invalid negative pre-release identifier: $ident"))
				} catch (e: Exception) {
					if (!ident.matches(preRegex) || ident.isEmpty()
						&& !((prerelease.size == 1) && build.isEmpty()))

						throw(IllegalArgumentException("invalid pre-release identifier: $ident"))
				}
			}
			for (ident in build) {
				try {
					val int = ident.toInt()
					int >= 0 || throw(IllegalArgumentException("invalid negative pre-release identifier: $ident"))
				} catch (e: Exception) {
					if (!ident.matches(preRegex) || ident.isEmpty() && build.size != 1)
						throw(IllegalArgumentException("invalid pre-release identifier: $ident"))
				}
			}
		}
	}

	/**
	 * @see `base/version.jl`
	 * ```julia
	 * function VersionNumber(v::AbstractString)
	 * 		v == "∞" && return typemax(VersionNumber)
	 * 		m = match(VERSION_REGEX, v)
	 * 		m === nothing && throw(ArgumentError("invalid version string: $v"))
	 * 		major, minor, patch, minus, prerl, plus, build = m.captures
	 * ......
	 *
	 * 		return VersionNumber(major, minor, patch, prerl, build)
	 * end
	 * ```
	 * @param v [String]
	 */
	fun VersionNumber(v: String): VersionNumber {
		v == "∞" && return typemax()
		val regex = Regex(JULIA_VERSION_NUMBER_REGEX_IX, setOf(RegexOption.IGNORE_CASE, RegexOption.COMMENTS))
		val captures = regex.matchEntire(v)?.groupValues ?: throw(IllegalArgumentException("invalid version string: $v"))

		val major = captures.getOrNull(1)?.parseUInt() ?: 0L
		val minor = captures.getOrNull(2)?.parseUInt() ?: 0L
		val patch = captures.getOrNull(3)?.parseUInt() ?: 0L
		val minus = captures.getOrNull(4)
		var prerl = captures.getOrNull(5)
		val plus = captures.getOrNull(6)
		val build = captures.getOrNull(7)

		if (prerl != null && prerl.isNotEmpty() && prerl[0] == '-') {
			prerl = prerl.substring(1)
		}
		val prerelease = when {
			prerl != null && prerl.isNotEmpty() -> prerl.split(".")
			minus != null && minus.isNotEmpty() -> listOf("")
			else -> emptyList()
		}
		val bld = when {
			build != null && build.isNotEmpty() -> build.split(".")
			plus != null && plus.isNotEmpty() -> listOf("")
			else -> emptyList()
		}
		return VersionNumber(major, minor, patch, prerelease, bld)
	}

	private fun <T> T.parseUInt(): VInt? =
		try {
			this.toString().toLong()
		} catch (e: Exception) {
			null
		}

	/**
	 * @see `base/version.jl`
	 * ```julia
	 * function typemax(::Type{VersionNumber})
	 * 		∞ = typemax(VInt)
	 * 		VersionNumber(∞, ∞, ∞, (), ("",))
	 * end
	 */
	private fun typemax(): VersionNumber {
		val `∞` = 4294967295L
		return VersionNumber(`∞`, `∞`, `∞`, emptyList(), listOf(""))
	}
}