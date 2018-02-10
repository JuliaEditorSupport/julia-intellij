package org.ice1000.julia.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.impl.source.tree.injected.StringLiteralEscaper
import org.ice1000.julia.lang.JuliaTokenType
import org.ice1000.julia.lang.psi.*

interface IJuliaFunctionDeclaration : PsiElement {
	val exprList: List<JuliaExpr>
}

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
	// check if they are declarations
	val isFunctionName: Boolean
	val isMacroName: Boolean
	val isModuleName: Boolean
	val isTypeName: Boolean
	val isAbstractTypeName: Boolean
	val isPrimitiveTypeName: Boolean
}

abstract class JuliaSymbolMixin(astNode: ASTNode) : ASTWrapperPsiElement(astNode), JuliaSymbol {
	override var type: String? = null
	override val isFunctionName get() = parent is JuliaFunction || parent is JuliaCompactFunction
	override val isMacroName get() = parent is JuliaMacro
	override val isModuleName get() = parent is JuliaModuleDeclaration
	override val isTypeName get() = parent is JuliaTypeDeclaration || parent is JuliaTypeAlias
	override val isAbstractTypeName get() = parent is JuliaAbstractTypeDeclaration
	override val isPrimitiveTypeName get() = parent is JuliaPrimitiveTypeDeclaration

	override fun subtreeChanged() {
		type = null
		super.subtreeChanged()
	}
}

interface IJuliaExpr : PsiElement {
	var type: String?
}

abstract class JuliaExprMixin(astNode: ASTNode) : ASTWrapperPsiElement(astNode), JuliaExpr {
	override var type: String? = null
	override fun subtreeChanged() {
		type = null
		super.subtreeChanged()
	}
}
