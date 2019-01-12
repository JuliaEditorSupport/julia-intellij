package org.ice1000.julia.lang.psi

import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.tree.IElementType

interface JuliaLazyParseableBlock : IJuliaBlock

interface IJuliaBlock : JuliaPsiCompositeElement {
	val statements: JuliaStatements?
}

interface JuliaPsiCompositeElement : NavigatablePsiElement {
	val tokenType: IElementType
}

enum class JuliaSymbolKind(val isDeclaration: Boolean) {
	Field(false), // TODO
	FunctionName(true),
	ApplyFunctionName(false),
	MacroName(true),
	ModuleName(true),
	TypeName(true),
	TypeParameterName(true),
	AbstractTypeName(true),
	PrimitiveTypeName(true),
	FunctionParameter(true),
	VariableName(true),
	GlobalName(true),
	CatchSymbol(true),
	IndexParameter(true),
	LambdaParameter(true),
	KeywordParameterName(false),
	Unknown(false)
}
