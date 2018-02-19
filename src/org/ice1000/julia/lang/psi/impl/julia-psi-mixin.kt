package org.ice1000.julia.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.julia.lang.JuliaTokenType
import org.ice1000.julia.lang.psi.*

interface DocStringOwner {
	var docString: JuliaString?
}

interface IJuliaFunctionDeclaration : PsiNameIdentifierOwner, DocStringOwner {
	// val exprList: List<JuliaExpr>
	val typeAndParams: String
}

abstract class JuliaDeclaration(node: ASTNode) : JuliaExprMixin(node), PsiNameIdentifierOwner {
	private var refCache: Array<PsiReference>? = null
	override fun setName(newName: String) = nameIdentifier?.let { JuliaTokenType.fromText(newName, project).let(it::replace) }
		.also {
			if (it is JuliaDeclaration)
				it.refCache = references.mapNotNull { it.handleElementRename(newName).reference }.toTypedArray()
		}

	open val startPoint: PsiElement
		get() = PsiTreeUtil.getParentOfType(this, JuliaStatements::class.java) ?: parent

	override fun getName() = nameIdentifier?.text.orEmpty()
	override fun getReferences() = refCache
		?: nameIdentifier
			?.let { collectFrom(startPoint, it.text) }
			?.also { refCache = it }
		?: emptyArray()

	override fun subtreeChanged() {
		refCache = null
		super.subtreeChanged()
	}

	override fun processDeclarations(
		processor: PsiScopeProcessor, substitutor: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		processDeclTrivial(processor, substitutor, lastParent, place) && true == nameIdentifier?.let { processor.execute(it, substitutor) }
}

abstract class JuliaAssignOpMixin(node: ASTNode) : JuliaDeclaration(node), JuliaAssignOp {
	override fun getNameIdentifier() = children.firstOrNull { it is JuliaSymbol }
}

abstract class JuliaFunctionMixin(node: ASTNode) : JuliaDeclaration(node), JuliaFunction {
	override var docString: JuliaString? = null
	override fun getNameIdentifier() = children.firstOrNull { it is JuliaSymbol } as JuliaSymbol?
	override val typeAndParams get() = typeParameters?.text ?: functionSignature?.text.orEmpty()
}

abstract class JuliaCompactFunctionMixin(node: ASTNode) : JuliaDeclaration(node), JuliaCompactFunction {
	override var docString: JuliaString? = null
	override fun getNameIdentifier() = exprList.firstOrNull()
	override val typeAndParams: String get() = typeParameters?.text ?: functionSignature.text
}

abstract class JuliaMacroMixin(node: ASTNode) : JuliaDeclaration(node), JuliaMacro {
	override var docString: JuliaString? = null
	override var type: String? = null
	override fun getNameIdentifier() = symbol
}

interface IJuliaString : PsiLanguageInjectionHost {
	override fun createLiteralTextEscaper(): LiteralTextEscaper<out JuliaString>
	override fun updateText(s: String): JuliaString
	var isDocString: Boolean
}

abstract class JuliaStringMixin(node: ASTNode) : ASTWrapperPsiElement(node), JuliaString {
	override var isDocString = false
	override fun isValidHost() = true
	@Suppress("HasPlatformType")
	override fun createLiteralTextEscaper() = LiteralTextEscaper.createSimple(this)
	@Suppress("HasPlatformType")
	override fun updateText(s: String) = ElementManipulators.handleContentChange(this, s)
}

interface IJuliaRegex : PsiLanguageInjectionHost {
	override fun createLiteralTextEscaper(): LiteralTextEscaper<out JuliaRegex>
	override fun updateText(s: String): JuliaRegex
}

abstract class JuliaRegexMixin(node: ASTNode) : ASTWrapperPsiElement(node), JuliaRegex {
	override fun isValidHost() = true
	@Suppress("HasPlatformType")
	override fun createLiteralTextEscaper() = LiteralTextEscaper.createSimple(this)
	@Suppress("HasPlatformType")
	override fun updateText(s: String) = ElementManipulators.handleContentChange(this, s)
}

abstract class JuliaStatementsMixin(node: ASTNode) : ASTWrapperPsiElement(node), JuliaStatements {
	override fun processDeclarations(
		processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		processDeclTrivial(processor, state, lastParent, place)
}

interface IJuliaSymbol : JuliaExpr, PsiNameIdentifierOwner {
	// check if they are declarations
	val isField: Boolean
	val isFunctionName: Boolean
	val isMacroName: Boolean
	val isModuleName: Boolean
	val isTypeName: Boolean
	val isAbstractTypeName: Boolean
	val isPrimitiveTypeName: Boolean
}

interface IJuliaTypeDeclaration : JuliaExpr, PsiNameIdentifierOwner, DocStringOwner

abstract class JuliaTypeDeclarationMixin(node: ASTNode) : JuliaExprMixin(node), JuliaTypeDeclaration {
	override var docString: JuliaString? = null
	override fun getNameIdentifier() = exprList.firstOrNull()
	override fun setName(name: String) = nameIdentifier?.replace(JuliaTokenType.fromText(name, project))
	override fun getName() = nameIdentifier?.text
}

abstract class JuliaAbstractSymbol(node: ASTNode) : ASTWrapperPsiElement(node), PsiNameIdentifierOwner, JuliaExpr {
	private var reference: JuliaSymbolRef? = null
	override var type: String? = null
	override fun getReference() = reference ?: JuliaSymbolRef(this).also { reference = it }
	override fun getNameIdentifier() = this
	override fun setName(name: String) = replace(JuliaTokenType.fromText(name, project))
	override fun getName() = text
	override fun subtreeChanged() {
		type = null
		reference = null
		super.subtreeChanged()
	}
}

abstract class JuliaSymbolMixin(node: ASTNode) : JuliaAbstractSymbol(node), JuliaSymbol {
	override val isField: Boolean
		get() = parent is JuliaTypeDeclaration && this !== parent.children.firstOrNull { it is JuliaSymbol }
	override val isFunctionName get() = parent is JuliaFunction || (parent is JuliaCompactFunction && this === parent.children.firstOrNull())
	override val isMacroName get() = parent is JuliaMacro
	override val isModuleName get() = parent is JuliaModuleDeclaration
	override val isTypeName get() = (parent is JuliaTypeDeclaration && this === parent.children.firstOrNull { it is JuliaSymbol }) || parent is JuliaTypeAlias
	override val isAbstractTypeName get() = parent is JuliaAbstractTypeDeclaration
	override val isPrimitiveTypeName get() = parent is JuliaPrimitiveTypeDeclaration
}

abstract class JuliaMacroSymbolMixin(node: ASTNode) : JuliaAbstractSymbol(node), JuliaMacroSymbol

interface IJuliaExpr : PsiElement {
	var type: String?
}

abstract class JuliaExprMixin(node: ASTNode) : ASTWrapperPsiElement(node), JuliaExpr {
	override var type: String? = null
	override fun subtreeChanged() {
		type = null
		super.subtreeChanged()
	}
}

interface IJuliaModuleDeclaration : PsiNameIdentifierOwner, DocStringOwner

abstract class JuliaModuleDeclarationMixin(node: ASTNode) : ASTWrapperPsiElement(node), JuliaModuleDeclaration {
	override var docString: JuliaString? = null
	override fun getNameIdentifier() = symbol
	override fun setName(name: String) = symbol.replace(JuliaTokenType.fromText(name, project))
	override fun getName() = nameIdentifier.text
}
