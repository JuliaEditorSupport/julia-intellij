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
