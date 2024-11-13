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

package org.ice1000.julia.lang.psi

import com.intellij.psi.NavigatablePsiElement

interface JuliaLazyParseableBlock : IJuliaBlock

interface IJuliaBlock : JuliaPsiCompositeElement {
	val statements: JuliaStatements?
}

interface JuliaPsiCompositeElement : NavigatablePsiElement

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
