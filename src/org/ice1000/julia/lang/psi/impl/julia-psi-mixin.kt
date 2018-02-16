package org.ice1000.julia.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.impl.source.tree.injected.StringLiteralEscaper
import org.ice1000.julia.lang.JuliaTokenType
import org.ice1000.julia.lang.psi.*

interface IJuliaFunctionDeclaration : PsiElement {
	// val exprList: List<JuliaExpr>
	var docString: JuliaStringContent?

	val functionName: String
	val typeAndParams: String
}

abstract class JuliaFunctionMixin(astNode: ASTNode) : JuliaExprMixin(astNode),
	JuliaFunction {
	override var docString: JuliaStringContent? = null
	override val functionName get() = symbol?.text.toString()
	override val typeAndParams get() = typeParameters?.text ?: ""+functionSignature?.text
}

abstract class JuliaCompactFunctionMixin(astNode: ASTNode) : JuliaExprMixin(astNode),
	JuliaCompactFunction {
	override var docString: JuliaStringContent? = null
	override val functionName get() = exprList.first().text.toString()
	override val typeAndParams get() = typeParameters?.text ?: ""+functionSignature.text
}

interface IJuliaStringContent : PsiLanguageInjectionHost {
	override fun createLiteralTextEscaper(): StringLiteralEscaper<out JuliaStringContent>
	override fun updateText(s: String): JuliaStringContent
	var isDocString: Boolean
	var isRegex: Boolean
}

abstract class JuliaStringContentMixin(astNode: ASTNode) : ASTWrapperPsiElement(astNode), JuliaStringContent {
	override fun isValidHost() = true
	override fun createLiteralTextEscaper() = StringLiteralEscaper(this)
	override fun updateText(s: String) = replace(JuliaTokenType.fromText(s, project)) as JuliaStringContent
	override var isDocString = false
	override var isRegex = false
}

interface IJuliaSymbol : JuliaExpr {
	// check if they are declarations
	val isFunctionName: Boolean
	val isMacroName: Boolean
	val isModuleName: Boolean
	val isTypeName: Boolean
	val isAbstractTypeName: Boolean
	val isPrimitiveTypeName: Boolean
	var isResolved: Boolean
}

abstract class JuliaSymbolMixin(astNode: ASTNode) : ASTWrapperPsiElement(astNode), JuliaSymbol {
	private var reference: JuliaSymbolRef? = null
	override var type: String? = null
	override val isFunctionName get() = parent is JuliaFunction || parent is JuliaCompactFunction
	override val isMacroName get() = parent is JuliaMacro
	override val isModuleName get() = parent is JuliaModuleDeclaration
	override val isTypeName get() = parent is JuliaTypeDeclaration || parent is JuliaTypeAlias
	override val isAbstractTypeName get() = parent is JuliaAbstractTypeDeclaration
	override val isPrimitiveTypeName get() = parent is JuliaPrimitiveTypeDeclaration
	override var isResolved: Boolean = false
	override fun getReference() = reference ?: JuliaSymbolRef(this).also { reference = it }
	override fun subtreeChanged() {
		type = null
		isResolved = false
		reference = null
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
