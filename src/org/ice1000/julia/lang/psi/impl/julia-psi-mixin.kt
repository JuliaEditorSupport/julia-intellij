package org.ice1000.julia.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.injected.StringLiteralEscaper
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.julia.lang.JuliaTokenType
import org.ice1000.julia.lang.psi.*

interface DocStringOwner {
	var docString: JuliaStringContent?
}

interface IJuliaFunctionDeclaration : PsiNameIdentifierOwner, DocStringOwner {
	// val exprList: List<JuliaExpr>
	val typeAndParams: String
}

abstract class JuliaDeclaration(astNode: ASTNode) : JuliaExprMixin(astNode), PsiNameIdentifierOwner {
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

interface IJuliaAssignOp : PsiNameIdentifierOwner

abstract class JuliaAssignOpMixin(astNode: ASTNode) : JuliaDeclaration(astNode), JuliaAssignOp {
	override fun getNameIdentifier() = children.firstOrNull { it is JuliaSymbol }
}

abstract class JuliaFunctionMixin(astNode: ASTNode) : JuliaDeclaration(astNode), JuliaFunction {
	override var docString: JuliaStringContent? = null
	override fun getNameIdentifier() = children.firstOrNull { it is JuliaSymbol } as JuliaSymbol?
	override val typeAndParams get() = typeParameters?.text ?: functionSignature?.text.orEmpty()
	override fun subtreeChanged() {
		docString = null
		super.subtreeChanged()
	}
}

abstract class JuliaCompactFunctionMixin(astNode: ASTNode) : JuliaDeclaration(astNode), JuliaCompactFunction {
	override var docString: JuliaStringContent? = null
	override fun getNameIdentifier(): JuliaExpr? = exprList.firstOrNull()
	override val typeAndParams: String get() = typeParameters?.text ?: functionSignature.text
	override fun subtreeChanged() {
		docString = null
		super.subtreeChanged()
	}
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

abstract class JuliaStatementsMixin(astNode: ASTNode) : ASTWrapperPsiElement(astNode), JuliaStatements {
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

abstract class JuliaTypeDeclarationMixin(astNode: ASTNode) : JuliaExprMixin(astNode), JuliaTypeDeclaration {
	override var docString: JuliaStringContent? = null
	override fun getNameIdentifier() = exprList.firstOrNull()
	override fun setName(name: String) = nameIdentifier?.replace(JuliaTokenType.fromText(name, project))
	override fun getName() = nameIdentifier?.text
	override fun subtreeChanged() {
		docString = null
		super.subtreeChanged()
	}
}

abstract class JuliaSymbolMixin(astNode: ASTNode) : ASTWrapperPsiElement(astNode), JuliaSymbol {
	private var reference: JuliaSymbolRef? = null
	override var type: String? = null
	override val isField: Boolean
		get() = parent is JuliaTypeDeclaration && this !== parent.children.firstOrNull { it is JuliaSymbol }
	override val isFunctionName get() = parent is JuliaFunction || (parent is JuliaCompactFunction && this === parent.children.firstOrNull())
	override val isMacroName get() = parent is JuliaMacro
	override val isModuleName get() = parent is JuliaModuleDeclaration
	override val isTypeName get() = (parent is JuliaTypeDeclaration && this === parent.children.firstOrNull { it is JuliaSymbol }) || parent is JuliaTypeAlias
	override val isAbstractTypeName get() = parent is JuliaAbstractTypeDeclaration
	override val isPrimitiveTypeName get() = parent is JuliaPrimitiveTypeDeclaration
	override fun getNameIdentifier() = this
	override fun setName(name: String) = replace(JuliaTokenType.fromText(name, project))
	override fun getName() = text
	override fun getReference() = reference ?: JuliaSymbolRef(this).also { reference = it }
	override fun subtreeChanged() {
		type = null
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

interface IJuliaModuleDeclaration : PsiNameIdentifierOwner, DocStringOwner

abstract class JuliaModuleDeclarationMixin(astNode: ASTNode) : ASTWrapperPsiElement(astNode), JuliaModuleDeclaration {
	override var docString: JuliaStringContent? = null
	override fun getNameIdentifier() = symbol
	override fun setName(name: String) = symbol.replace(JuliaTokenType.fromText(name, project))
	override fun getName() = nameIdentifier.text
	override fun subtreeChanged() {
		docString = null
		super.subtreeChanged()
	}
}
