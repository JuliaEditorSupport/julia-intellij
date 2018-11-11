package org.ice1000.julia.lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.psi.*

interface DocStringOwner : PsiElement

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
	override fun setName(newName: String) = also {
		references.forEach { it.handleElementRename(newName) }
		nameIdentifier?.replace(JuliaTokenType.fromText(newName, project))
	}

	open val startPoint: PsiElement
	// TODO workaround for KT-22916
		get() = PsiTreeUtil.getParentOfType(this, JuliaStatements::class.java) ?: parent

	override fun getName() = nameIdentifier?.text.orEmpty()
	override fun getReferences() = refCache
		?: nameIdentifier
			?.let { collectFrom(startPoint, it.text, it) }
			?.also { refCache = it }
		?: emptyArray()

	override fun subtreeChanged() {
		refCache = null
		super.subtreeChanged()
	}

	override fun processDeclarations(
		processor: PsiScopeProcessor, substitutor: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		nameIdentifier?.let { processor.execute(it, substitutor) }.orFalse() and
			processDeclTrivial(processor, substitutor, lastParent, place)
}

abstract class JuliaForComprehensionMixin(node: ASTNode) : ASTWrapperPsiElement(node), JuliaForComprehension {
	override fun processDeclarations(
		processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		comprehensionElementList.all { it.processDeclarations(processor, state, lastParent, place) } and
			super.processDeclarations(processor, state, lastParent, place)

	override var type: Type? = null
}

abstract class JuliaComprehensionElementMixin(node: ASTNode) : ASTWrapperPsiElement(node), JuliaComprehensionElement {
	override fun processDeclarations(
		processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		singleComprehensionList.all { it.processDeclarations(processor, state, lastParent, place) } and
			super.processDeclarations(processor, state, lastParent, place)
}

abstract class JuliaSingleComprehensionMixin(node: ASTNode) : JuliaDeclaration(node), JuliaSingleComprehension {
	override fun getNameIdentifier(): PsiElement? = singleIndexer?.firstChild
		?: multiIndexer?.firstChild

	override fun processDeclarations(
		processor: PsiScopeProcessor, substitutor: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		singleIndexer?.let { processor.execute(it.firstChild, substitutor) }.orFalse() and
			multiIndexer?.let { it.children.all { processor.execute(it, substitutor) } }.orFalse() and
			super.processDeclarations(processor, substitutor, lastParent, place)
}

abstract class JuliaTypedNamedVariableMixin(node: ASTNode) : JuliaDeclaration(node), JuliaTypedNamedVariable {
	override var type: Type? = null
		set(_) = Unit

	override fun getNameIdentifier() = firstChild as? JuliaSymbol
	override val startPoint: PsiElement
		get() = parent.parent.let {
			when (it) {
				is JuliaFunction -> it.statements
				is JuliaCompactFunction -> it.lastChild
				else -> it
			}
		}
}

abstract class JuliaAssignOpMixin(node: ASTNode) : JuliaDeclaration(node), JuliaAssignOp {
	override var type: Type?
		get() = exprList.lastOrNull()?.type
		set(_) {}

	override fun getNameIdentifier() = children
		.firstOrNull { it is JuliaSymbol || it is JuliaSymbolLhs }
		?.let { (it as? JuliaSymbolLhs)?.symbolList?.firstOrNull() ?: it }
}

abstract class JuliaFunctionMixin(node: ASTNode) : JuliaDeclaration(node), JuliaFunction {
	private var paramsTextCache: String? = null
	private var typeParamsTextCache: String? = null
	override val returnType: Type get() = UNKNOWN_VALUE_PLACEHOLDER
	override fun getNameIdentifier() = children.firstOrNull { it is JuliaSymbol } as JuliaSymbol?
	override val typeParamsText: String
		get() = typeParamsTextCache ?: typeParameters?.exprList
			?.joinToString(prefix = "{", postfix = "}") { it.text }
			.orEmpty()
			.also { typeParamsTextCache = it }

	override val paramsText: String
		get() = paramsTextCache ?: functionSignature
			?.typedNamedVariableList
			?.joinToString { it.text.substringBefore("=") }
			.orEmpty()
			.let { "($it)" }
			.replaceCommaWithSemi(functionSignature?.typedNamedVariableList?.indexOfFirst { it.text.contains('=') })
			.also { paramsTextCache = it }

	override fun subtreeChanged() {
		paramsTextCache = null
		typeParamsTextCache = null
		super.subtreeChanged()
	}

	override fun processDeclarations(
		processor: PsiScopeProcessor, substitutor: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		functionSignature?.run {
			typedNamedVariableList.all { processor.execute(it.firstChild, substitutor) }
		}.orFalse() && super.processDeclarations(processor, substitutor, lastParent, place)
}

/**
 * @param index means which comma will be replaced.
 */
fun String.replaceCommaWithSemi(index: Int?): String {
	// null or -1 => no `keyword arguments`, and 0 means no simple arguments so that we don't need to replace with.
	if (index == null || index == -1 || index == 0) return this
	var count = 0
	// the position of `,` will be replaced.
	var range = 0
	for (i in this.indices) {
		if (this[i] == ',') count++
		if (count == index) {
			range = i
			break
		}
	}
	return this.replaceRange(range, range + 1, ";")
}

abstract class JuliaCompactFunctionMixin(node: ASTNode) : JuliaDeclaration(node), JuliaCompactFunction {
	private var body: JuliaExpr? = null
		get() {
			if (field == null) field = lastChild as? JuliaExpr
			return field
		}

	private var paramsTextCache: String? = null
	override val returnType: Type get() = body?.type ?: UNKNOWN_VALUE_PLACEHOLDER
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
	override var type: Type? = null
	override fun getNameIdentifier() = symbol
}

interface IJuliaString : PsiLanguageInjectionHost {
	override fun createLiteralTextEscaper(): LiteralTextEscaper<out JuliaString>
	override fun updateText(s: String): JuliaString
}

@Suppress("HasPlatformType")
abstract class JuliaStringMixin(node: ASTNode) : ASTWrapperPsiElement(node), JuliaString {
	override var type: Type? = "String"
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
	val isTypeParameterName: Boolean
	val isAbstractTypeName: Boolean
	val isPrimitiveTypeName: Boolean
	val isFunctionParameter: Boolean
	val isVariableName: Boolean
	val isGlobalName: Boolean
	val isCatchSymbol: Boolean
	val isDeclaration: Boolean
	val isIndexParameter: Boolean
	val isLambdaParameter: Boolean
}

interface IJuliaTypeDeclaration : JuliaExpr, PsiNameIdentifierOwner, DocStringOwner

abstract class JuliaTypeDeclarationMixin(node: ASTNode) : JuliaExprMixin(node), JuliaTypeDeclaration {
	private var nameCache: JuliaExpr? = null
	override fun getNameIdentifier() = nameCache ?: exprList.firstOrNull()?.also { nameCache = it }
	override fun setName(name: String) = also { nameIdentifier?.replace(JuliaTokenType.fromText(name, project)) }
	override fun getName() = nameIdentifier?.text
	override fun processDeclarations(
		processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		nameIdentifier?.let { processor.execute(it, state) }.orFalse() &&
			super.processDeclarations(processor, state, lastParent, place)

	override fun subtreeChanged() {
		nameCache = null
		super.subtreeChanged()
	}
}

/**
 * Just to provide implementation of [PsiNameIdentifierOwner] for [JuliaMacroSymbolMixin]
 * and [JuliaSymbolMixin] (see [JuliaSymbolRef]). (for code reuse purpose)
 */
sealed class JuliaAbstractSymbol(node: ASTNode) : ASTWrapperPsiElement(node), PsiNameIdentifierOwner, JuliaExpr {
	private var referenceImpl: JuliaSymbolRef? = null

	/** For [JuliaSymbolMixin], we cannot have a reference if it's a declaration. */
	override fun getReference() = referenceImpl ?: JuliaSymbolRef(this).also { referenceImpl = it }

	override fun getNameIdentifier(): JuliaAbstractSymbol? = this
	override fun setName(name: String) = replace(JuliaTokenType.fromText(name, project))
	override fun getName() = text
	override fun subtreeChanged() {
		type = null
		referenceImpl = null
		super.subtreeChanged()
	}
}

abstract class JuliaSymbolMixin(node: ASTNode) : JuliaAbstractSymbol(node), JuliaSymbol {
	final override val isField: Boolean by lazy {
		this !== parent.children.firstOrNull { it is JuliaSymbol } &&
			parent is JuliaTypeDeclaration
	}
	final override val isFunctionName by lazy {
		(parent is JuliaCompactFunction && this === parent.firstChild) ||
			parent is JuliaFunction
	}
	final override val isMacroName get() = parent is JuliaMacro
	final override val isModuleName get() = parent is JuliaModuleDeclaration
	final override val isTypeName by lazy {
		parent is JuliaTypeOp && this === parent.children.getOrNull(1) ||
			parent is JuliaTypeAlias ||
			parent is JuliaType ||
			parent is JuliaTypeAnnotation ||
			parent is JuliaTypeDeclaration
	}
	final override val isTypeParameterName by lazy {
		parent is JuliaTypeParameters ||
			parent.parent is JuliaType ||
			parent is JuliaWhereClause
	}
	final override val isAbstractTypeName get() = parent is JuliaAbstractTypeDeclaration
	final override val isPrimitiveTypeName get() = parent is JuliaPrimitiveTypeDeclaration
	final override val isFunctionParameter
		get() = parent is JuliaTypedNamedVariable && this === parent.firstChild

	final override val isGlobalName: Boolean get() = parent is JuliaGlobalStatement
	final override val isCatchSymbol: Boolean get() = parent is JuliaCatchClause
	final override val isLambdaParameter: Boolean by lazy {
		parent is JuliaLambda || (parent is JuliaTuple && parent.parent is JuliaLambda)
	}
	final override val isIndexParameter: Boolean by lazy {
		parent is JuliaSingleIndexer ||
			parent.parent is JuliaMultiIndexer
	}
	final override val isVariableName by lazy {
		parent is JuliaAssignOp && this === parent.firstChild ||
			parent is JuliaSymbolLhs
	}
	final override val isDeclaration by lazy {
		isFunctionName ||
			isVariableName ||
			isFunctionParameter ||
			isMacroName ||
			isModuleName ||
			isTypeName ||
			isAbstractTypeName ||
			isGlobalName ||
			isPrimitiveTypeName ||
			isCatchSymbol ||
			isIndexParameter ||
			isLambdaParameter
	}

	override var type: Type? = null
		get() = if (isVariableName) PsiTreeUtil
			.getParentOfType(this, JuliaAssignOp::class.java, true, JuliaStatements::class.java)
			?.children
			?.lastOrNull { it is JuliaExpr }
			?.let { it as? JuliaExpr }
			?.takeIf { it != this }
			?.type
			?.also { field = it }
		else field
}

abstract class JuliaMacroSymbolMixin(node: ASTNode) : JuliaAbstractSymbol(node), JuliaMacroSymbol {
	override var type: Type? = null
}

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

abstract class JuliaModuleDeclarationMixin(node: ASTNode) : JuliaDeclaration(node), JuliaModuleDeclaration {
	override fun getNameIdentifier() = symbol
}

abstract class JuliaCatchDeclarationMixin(node: ASTNode) : JuliaDeclaration(node), JuliaCatchClause {
	override fun getNameIdentifier() = symbol
}

abstract class JuliaLoopDeclarationMixin(node: ASTNode) : JuliaDeclaration(node), JuliaForExpr {
	override fun getNameIdentifier(): PsiElement? =
		singleIndexerList.firstOrNull()?.firstChild
			?: multiIndexerList.firstOrNull()?.firstChild

	override fun processDeclarations(
		processor: PsiScopeProcessor, substitutor: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		singleIndexerList.all { processor.execute(it.firstChild, substitutor) } &&
			multiIndexerList.all { it.children.all { processor.execute(it, substitutor) } } &&
			super.processDeclarations(processor, substitutor, lastParent, place)
}

abstract class JuliaLambdaMixin(node: ASTNode) : JuliaDeclaration(node), JuliaLambda {
	private val paramCandidates get() = childrenBefore(JuliaTypes.LAMBDA_ABSTRACTION)
	override fun getNameIdentifier() = children.firstOrNull { it is JuliaExpr }?.let {
		it as? JuliaSymbol ?: (it as? JuliaTuple)?.children?.firstOrNull { it is JuliaSymbol }
	}

	override fun processDeclarations(
		processor: PsiScopeProcessor,
		substitutor: ResolveState,
		lastParent: PsiElement?,
		place: PsiElement) = paramCandidates.let {
		(it as? JuliaSymbol)?.processDeclarations(processor, substitutor, lastParent, place)
			?: (it as? JuliaTuple)?.run {
				children
					.toList()
					.asReversed()
					.all { it.processDeclarations(processor, substitutor, lastParent, place) }
			}
	}.orFalse() && super.processDeclarations(processor, substitutor, lastParent, place)
}