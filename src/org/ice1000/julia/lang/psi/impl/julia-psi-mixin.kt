package org.ice1000.julia.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.impl.source.tree.injected.StringLiteralEscaper
import org.ice1000.julia.lang.JuliaTokenType
import org.ice1000.julia.lang.psi.*

interface IJuliaStringContent : PsiLanguageInjectionHost {
	override fun createLiteralTextEscaper(): StringLiteralEscaper<out JuliaStringContent>
	override fun updateText(s: String): JuliaStringContent
}

abstract class JuliaStringContentMixin(astNode: ASTNode) : ASTWrapperPsiElement(astNode), JuliaStringContent {
	override fun isValidHost() = true
	override fun createLiteralTextEscaper() = StringLiteralEscaper(this)
	override fun updateText(s: String) = replace(JuliaTokenType.fromText(s, project)) as JuliaStringContent
}

interface IJuliaSymbol {
	val isFunctionName: Boolean
	val isMacroName: Boolean
	val isModuleName: Boolean
	val isTypeName: Boolean
	val isAbstractTypeName: Boolean
	val isPrimitiveTypeName: Boolean
}

abstract class JuliaSymbolMixin(astNode: ASTNode) : ASTWrapperPsiElement(astNode), JuliaSymbol {
	override val isFunctionName = parent is JuliaFunction || parent is JuliaCompactFunction
	override val isMacroName = parent is JuliaMacro
	override val isModuleName = parent is JuliaModuleDeclaration
	override val isTypeName = parent is JuliaTypeDeclaration || parent is JuliaTypeAlias
	override val isAbstractTypeName = parent is JuliaAbstractTypeDeclaration
	override val isPrimitiveTypeName = parent is JuliaPrimitiveTypeDeclaration
}
