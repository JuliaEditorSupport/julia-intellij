package org.ice1000.julia.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.injected.StringLiteralEscaper
import com.intellij.psi.scope.PsiScopeProcessor
import org.ice1000.julia.lang.JuliaTokenType
import org.ice1000.julia.lang.psi.*

interface IJuliaFunctionDeclaration : PsiElement, PsiNameIdentifierOwner {
	// val exprList: List<JuliaExpr>
	var docString: JuliaStringContent?
	val typeAndParams: String
	override fun getReferences(): Array<out PsiReference>
}

abstract class JuliaFunctionDeclaration(astNode: ASTNode) : JuliaExprMixin(astNode), IJuliaFunctionDeclaration {
	private var refCache: Array<PsiReference>? = null
	override var docString: JuliaStringContent? = null
	override fun setName(newName: String) = nameIdentifier?.let { JuliaTokenType.fromText(newName, project).let(it::replace) }
		.also {
			if (it is JuliaFunctionDeclaration)
				it.refCache = references.mapNotNull { it.handleElementRename(newName).reference }.toTypedArray()
		}

	override fun getName(): String = nameIdentifier?.text.orEmpty()
	override fun getReferences(): Array<PsiReference> = refCache
		?: nameIdentifier?.let { collectFrom(parent.parent, it.text) }
			?.also { refCache = it } ?: emptyArray()

	override fun subtreeChanged() {
		refCache = null
		super.subtreeChanged()
	}

	override fun processDeclarations(
		processor: PsiScopeProcessor, substitutor: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		processDeclTrivial(processor, substitutor, lastParent, place) && true == nameIdentifier?.let { processor.execute(it, substitutor) }
}

abstract class JuliaFunctionMixin(astNode: ASTNode) : JuliaFunctionDeclaration(astNode),
	JuliaFunction {
	override fun getNameIdentifier(): JuliaExpr = children.first { it is JuliaSymbol } as JuliaSymbol
	override val typeAndParams get() = typeParameters?.text ?: functionSignature?.text.orEmpty()
}

abstract class JuliaCompactFunctionMixin(astNode: ASTNode) : JuliaFunctionDeclaration(astNode),
	JuliaCompactFunction {
	override fun getNameIdentifier(): JuliaExpr = exprList.first()
	override val typeAndParams: String get() = typeParameters?.text ?: functionSignature.text
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

interface IJuliaSymbol : JuliaExpr {
	// check if they are declarations
	val isFunctionName: Boolean
	val isMacroName: Boolean
	val isModuleName: Boolean
	val isTypeName: Boolean
	val isAbstractTypeName: Boolean
	val isPrimitiveTypeName: Boolean
}

abstract class JuliaSymbolMixin(astNode: ASTNode) : ASTWrapperPsiElement(astNode), JuliaSymbol {
	private var reference: JuliaSymbolRef? = null
	override var type: String? = null
	override val isFunctionName get() = parent is JuliaFunction || (parent is JuliaCompactFunction && this === parent.children.firstOrNull())
	override val isMacroName get() = parent is JuliaMacro
	override val isModuleName get() = parent is JuliaModuleDeclaration
	override val isTypeName get() = parent is JuliaTypeDeclaration || parent is JuliaTypeAlias
	override val isAbstractTypeName get() = parent is JuliaAbstractTypeDeclaration
	override val isPrimitiveTypeName get() = parent is JuliaPrimitiveTypeDeclaration
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
