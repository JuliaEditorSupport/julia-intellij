package org.ice1000.julia.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.julia.lang.JuliaTokenType
import org.ice1000.julia.lang.editing.typeText
import org.ice1000.julia.lang.orFalse
import org.ice1000.julia.lang.psi.*

interface DocStringOwner {
	var docString: JuliaString?
}

interface IJuliaFunctionDeclaration : PsiNameIdentifierOwner, DocStringOwner {
	val returnType: Type
	val paramsText: String
	val typeParamsText: String
}

/**
 * Common super class for declaration
 */
abstract class JuliaDeclaration(node: ASTNode) : JuliaExprMixin(node), PsiNameIdentifierOwner {
	private var refCache: Array<PsiReference>? = null
	override fun setName(newName: String) = nameIdentifier?.let { JuliaTokenType.fromText(newName, project).let(it::replace) }
		.also {
			if (it is JuliaDeclaration)
				it.refCache = references.mapNotNull { it.handleElementRename(newName).reference }.toTypedArray()
		}

	open val startPoint: PsiElement
	// TODO workaround for KT-22916
		get() = PsiTreeUtil.getParentOfType(this, JuliaStatements::class.java) ?: parent

	override fun getName() = nameIdentifier?.text.orEmpty()
	override fun getReferences() = refCache
		?: nameIdentifier
			?.let { collectFrom(startPoint, it.text, nameIdentifier) }
			?.also { refCache = it }
		?: emptyArray()

	override fun subtreeChanged() {
		refCache = null
		super.subtreeChanged()
	}

	override fun processDeclarations(
		processor: PsiScopeProcessor, substitutor: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		processDeclTrivial(processor, substitutor, lastParent, place) and nameIdentifier?.let { processor.execute(it, substitutor) }.orFalse()
}

abstract class JuliaTypedNamedVariableMixin(node: ASTNode) : JuliaDeclaration(node), JuliaTypedNamedVariable {
	override var type: Type? = null
		set(value) = Unit

	override fun getNameIdentifier() = firstChild as? JuliaSymbol
	override val startPoint: PsiElement
		get() = parent.parent.let {
			when (it) {
				is JuliaFunction -> it.statements ?: it
				is JuliaCompactFunction -> it.lastChild
				else -> it
			}
		}
}

abstract class JuliaAssignOpMixin(node: ASTNode) : JuliaDeclaration(node), JuliaAssignOp {
	override fun getNameIdentifier() = children.firstOrNull { it is JuliaSymbol }
}

abstract class JuliaFunctionMixin(node: ASTNode) : JuliaDeclaration(node), JuliaFunction {
	override var docString: JuliaString? = null
	private var paramsTextCache: String? = null
	private var typeParamsTextCache: String? = null
	override val returnType: Type
		get() = "<unsupported>"

	override fun getNameIdentifier() = children.firstOrNull { it is JuliaSymbol } as JuliaSymbol?
	override val typeParamsText: String
		get() = typeParamsTextCache ?: typeParameters?.exprList
			?.joinToString(prefix = "{", postfix = "}") { it.text }
			.orEmpty()
			.also { typeParamsTextCache = it }

	override val paramsText: String
		get() = paramsTextCache ?: functionSignature
			?.typedNamedVariableList
			?.joinToString { it.typeAnnotation?.expr?.text ?: "Any" }
			.orEmpty()
			.let { "($it)" }
			.also { paramsTextCache = it }

	override fun subtreeChanged() {
		paramsTextCache = null
		typeParamsTextCache = null
		super.subtreeChanged()
	}

	override fun processDeclarations(
		processor: PsiScopeProcessor, substitutor: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		super.processDeclarations(processor, substitutor, lastParent, place) and
			functionSignature?.run {
				typedNamedVariableList.all { processor.execute(it.firstChild, substitutor) }
			}.orFalse()
}

abstract class JuliaCompactFunctionMixin(node: ASTNode) : JuliaDeclaration(node), JuliaCompactFunction {
	override var docString: JuliaString? = null
	private var body: JuliaExpr? = null
		get() {
			if (field == null) field = lastChild as? JuliaExpr ?: return null
			return field
		}

	private var paramsTextCache: String? = null
	override val returnType: Type get() = body?.type ?: "<unknown>"
	private var typeParamsTextCache: String? = null
	override fun getNameIdentifier() = exprList.firstOrNull()
	override val typeParamsText: String
		get() = typeParamsTextCache ?: typeParameters?.exprList
			?.joinToString(prefix = "{", postfix = "}") { it.text }
			.orEmpty()
			.also { typeParamsTextCache = it }

	override val paramsText: String
		get() = paramsTextCache ?: functionSignature
			.typedNamedVariableList
			.joinToString(prefix = "(", postfix = ")") { it.typeAnnotation?.expr?.text ?: "Any" }
			.also { paramsTextCache = it }

	override fun subtreeChanged() {
		paramsTextCache = null
		typeParamsTextCache = null
		body = null
		super.subtreeChanged()
	}
}

abstract class JuliaMacroMixin(node: ASTNode) : JuliaDeclaration(node), JuliaMacro {
	override var docString: JuliaString? = null
	override var type: Type? = null
	override fun getNameIdentifier() = symbol
}

interface IJuliaString : PsiLanguageInjectionHost {
	override fun createLiteralTextEscaper(): LiteralTextEscaper<out JuliaString>
	override fun updateText(s: String): JuliaString
	var isDocString: Boolean
}

@Suppress("HasPlatformType")
abstract class JuliaStringMixin(node: ASTNode) : ASTWrapperPsiElement(node), JuliaString {
	override var type: Type? = "String"
	override var isDocString = false
	override fun isValidHost() = true
	override fun createLiteralTextEscaper() = LiteralTextEscaper.createSimple(this)
	override fun updateText(s: String) = ElementManipulators.handleContentChange(this, s)
}

interface IJuliaRegex : PsiLanguageInjectionHost {
	override fun createLiteralTextEscaper(): LiteralTextEscaper<out JuliaRegex>
	override fun updateText(s: String): JuliaRegex
}

@Suppress("HasPlatformType")
abstract class JuliaRegexMixin(node: ASTNode) : ASTWrapperPsiElement(node), JuliaRegex {
	override var type: Type? = "Regex"
	override fun isValidHost() = true
	override fun createLiteralTextEscaper() = LiteralTextEscaper.createSimple(this)
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
	val isFunctionParameter: Boolean
	val isVariableName: Boolean
	val isDeclaration: Boolean
}

interface IJuliaTypeDeclaration : JuliaExpr, PsiNameIdentifierOwner, DocStringOwner

abstract class JuliaTypeDeclarationMixin(node: ASTNode) : JuliaExprMixin(node), JuliaTypeDeclaration {
	override var docString: JuliaString? = null
	override fun getNameIdentifier() = exprList.firstOrNull()
	override fun setName(name: String) = nameIdentifier?.replace(JuliaTokenType.fromText(name, project))
	override fun getName() = nameIdentifier?.text
}

/**
 * Just to provide implementation of [PsiNameIdentifierOwner] for [JuliaMacroSymbolMixin]
 * and [JuliaSymbolMixin] (see [JuliaSymbolRef]). (for code reuse purpose)
 */
abstract class JuliaAbstractSymbol(node: ASTNode) : ASTWrapperPsiElement(node), PsiNameIdentifierOwner, JuliaExpr {
	private val referenceImpl by lazy {
		object : JuliaSymbolRef() {
			override fun getElement() = this@JuliaAbstractSymbol
		}
	}
	override var type: Type? = null

	/** For [JuliaSymbolMixin], we cannot have a reference if it's a declaration. */
	override fun getReference() = referenceImpl

	override fun getNameIdentifier(): JuliaAbstractSymbol? = this
	override fun setName(name: String) = replace(JuliaTokenType.fromText(name, project))
	override fun getName() = text
	override fun subtreeChanged() {
		type = null
		super.subtreeChanged()
	}
}

abstract class JuliaSymbolMixin(node: ASTNode) : JuliaAbstractSymbol(node), JuliaSymbol {
	override val isField: Boolean
		get() = parent is JuliaTypeDeclaration && this !== parent.children.firstOrNull { it is JuliaSymbol }
	final override val isFunctionName by lazy { parent is JuliaFunction || (parent is JuliaCompactFunction && this === parent.firstChild) }
	final override val isMacroName by lazy { parent is JuliaMacro }
	final override val isModuleName by lazy { parent is JuliaModuleDeclaration }
	final override val isTypeName by lazy { (parent is JuliaTypeDeclaration && this === parent.children.firstOrNull { it is JuliaSymbol }) || parent is JuliaTypeAlias }
	final override val isAbstractTypeName by lazy { parent is JuliaAbstractTypeDeclaration }
	final override val isPrimitiveTypeName by lazy { parent is JuliaPrimitiveTypeDeclaration }
	final override val isFunctionParameter by lazy { parent is JuliaTypedNamedVariable && this === parent.firstChild }
	final override val isVariableName by lazy { parent is JuliaAssignOp && this === parent.firstChild }
	final override val isDeclaration by lazy {
		isFunctionName or
			isVariableName or
			isFunctionParameter or
			isMacroName or
			isModuleName or
			isTypeName or
			isAbstractTypeName or
			isPrimitiveTypeName
	}

	override fun getNameIdentifier() = if (isDeclaration) null else super.getNameIdentifier()
}

abstract class JuliaMacroSymbolMixin(node: ASTNode) : JuliaAbstractSymbol(node), JuliaMacroSymbol

interface IJuliaExpr : PsiElement {
	var type: Type?
}

abstract class JuliaExprMixin(node: ASTNode) : ASTWrapperPsiElement(node), JuliaExpr {
	override var type: Type? = null
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
	override fun getName(): String = nameIdentifier.text
}
