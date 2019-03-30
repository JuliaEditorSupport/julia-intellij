package org.ice1000.julia.lang.psi

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.psi.impl.*
import javax.swing.Icon

/**
 * @author ice1000
 * element should be only [JuliaSymbol] or [JuliaMacroSymbol]
 */
class JuliaSymbolRef(
	private val symbol: JuliaAbstractSymbol,
	private var refTo: PsiElement? = null) : PsiPolyVariantReference {
	private val range = TextRange(0, element.textLength)
	private val isDeclaration = (element as? JuliaSymbol)?.symbolKind?.isDeclaration.orFalse()
	private val resolver by lazy {
		when (symbol) {
			is JuliaMacroSymbolMixin -> macroResolver
			is JuliaSymbolMixin -> symbolResolver
		}
	}

	override fun equals(other: Any?) = (other as? JuliaSymbolRef)?.element == element
	override fun getElement() = symbol
	override fun hashCode() = element.hashCode()
	override fun getRangeInElement() = range
	override fun isSoft() = true
	override fun resolve() = if (isDeclaration) null else multiResolve(false).firstOrNull()?.element
	override fun isReferenceTo(o: PsiElement) = o === refTo || o === resolve()
	override fun getVariants(): Array<LookupElementBuilder> {
		val variantsProcessor = CompletionProcessor(this, true)
		treeWalkUp(variantsProcessor, element, element.containingFile)
		return variantsProcessor.candidateSet.toTypedArray()
	}

	override fun getCanonicalText(): String = element.text
	override fun handleElementRename(newName: String): PsiElement = JuliaTokenType.fromText(newName, element.project).let(element::replace)
	override fun bindToElement(element: PsiElement) = element.also { refTo = element }
	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = element.containingFile ?: return emptyArray()
		if (isDeclaration || !element.isValid || element.project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(element.project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	private companion object ResolverHolder {
		private val symbolResolver = ResolveCache.PolyVariantResolver<JuliaSymbolRef> { ref, incompleteCode ->
			resolveWith(SymbolResolveProcessor(ref, incompleteCode), ref)
		}

		private val macroResolver = ResolveCache.PolyVariantResolver<JuliaSymbolRef> { ref, incompleteCode ->
			resolveWith(MacroSymbolResolveProcessor(ref, incompleteCode), ref)
		}

		private inline fun <reified T> resolveWith(processor: ResolveProcessor<T>, ref: JuliaSymbolRef): Array<T> {
			val file = ref.element.containingFile ?: return emptyArray()
			treeWalkUp(processor, ref.element, file)
			return processor.candidateSet.toTypedArray()
		}
	}
}

abstract class ResolveProcessor<ResolveResult>(private val place: PsiElement) : PsiScopeProcessor {
	abstract val candidateSet: ArrayList<ResolveResult>
	override fun handleEvent(event: PsiScopeProcessor.Event, o: Any?) = Unit
	override fun <T : Any?> getHint(hintKey: Key<T>): T? = null
	protected val PsiElement.hasNoError get() = (this as? StubBasedPsiElement<*>)?.stub != null || !PsiTreeUtil.hasErrorElements(this)
	fun isInScope(element: PsiElement) = element is JuliaSymbol && when (element.symbolKind) {
		JuliaSymbolKind.FunctionParameter -> PsiTreeUtil.isAncestor(
			PsiTreeUtil.getParentOfType(element, IJuliaFunctionDeclaration::class.java), place, true)
		JuliaSymbolKind.CatchSymbol -> PsiTreeUtil.isAncestor(
			PsiTreeUtil.getParentOfType(element, JuliaCatchClause::class.java), place, true)
		JuliaSymbolKind.IndexParameter -> PsiTreeUtil.isAncestor(
			PsiTreeUtil.getParentOfType(element, JuliaForExpr::class.java)
				?: PsiTreeUtil.getParentOfType(element, JuliaForComprehension::class.java), place, true)
		JuliaSymbolKind.LambdaParameter -> PsiTreeUtil.isAncestor(
			PsiTreeUtil.getParentOfType(element, JuliaLambda::class.java), place, false)
		else -> element.symbolKind.isDeclaration &&
			PsiTreeUtil.isAncestor(PsiTreeUtil.getParentOfType(element, JuliaStatements::class.java), place, false)
	}
}

open class SymbolResolveProcessor(
	@JvmField protected val name: String,
	place: PsiElement,
	private val incompleteCode: Boolean) :
	ResolveProcessor<PsiElementResolveResult>(place) {
	constructor(ref: JuliaSymbolRef, incompleteCode: Boolean) : this(ref.canonicalText, ref.element, incompleteCode)

	override val candidateSet = ArrayList<PsiElementResolveResult>(3)
	protected open fun accessible(element: PsiElement) = name == element.text
	override fun execute(element: PsiElement, resolveState: ResolveState) = when {
		candidateSet.isNotEmpty() -> false
		element is JuliaSymbol -> {
			val accessible = accessible(element)
			if (accessible) candidateSet += PsiElementResolveResult(element, element.hasNoError)
			!accessible
		}
		else -> true
	}
}

class MacroSymbolResolveProcessor(name: String, place: PsiElement, incompleteCode: Boolean) :
	SymbolResolveProcessor(name, place, incompleteCode) {
	constructor(ref: JuliaSymbolRef, incompleteCode: Boolean) : this(ref.canonicalText, ref.element, incompleteCode)

	override fun accessible(element: PsiElement) = "@${element.text}" == name && isInScope(element)
}

class CompletionProcessor(place: PsiElement, private val incompleteCode: Boolean) :
	ResolveProcessor<LookupElementBuilder>(place) {
	constructor(ref: JuliaSymbolRef, incompleteCode: Boolean) : this(ref.element, incompleteCode)

	override val candidateSet = ArrayList<LookupElementBuilder>(20)
	override fun execute(element: PsiElement, resolveState: ResolveState): Boolean {
		if (element is JuliaSymbol) {
			val (icon: Icon,
				value: String,
				tail: String?,
				type: Type?,
				handler: InsertHandler<LookupElement>?
			) = when (element.symbolKind) {
				JuliaSymbolKind.VariableName,
				JuliaSymbolKind.CatchSymbol,
				JuliaSymbolKind.IndexParameter -> tuple5(
					JuliaIcons.JULIA_VARIABLE_ICON,
					element.text,
					null,
					element.type ?: UNKNOWN_VALUE_PLACEHOLDER
				)
				JuliaSymbolKind.ModuleName -> tuple5(
					JuliaIcons.JULIA_MODULE_ICON,
					element.text,
					null,
					element.containingFile.name.let { "in $it" }
				)
				JuliaSymbolKind.MacroName -> tuple5(
					JuliaIcons.JULIA_MACRO_ICON,
					"@${element.text}",
					null,
					null
				)
				JuliaSymbolKind.FunctionName -> (element.parent as? IJuliaFunctionDeclaration)?.let { function ->
					tuple5(
						JuliaIcons.JULIA_FUNCTION_ICON,
						element.text,
						function.paramsText,
						function.returnType,
						InsertHandler { context, _: LookupElement ->
							val editor = context.editor
							editor.document.insertString(editor.caretModel.offset, "()")
							editor.caretModel.moveCaretRelatively(1, 0, false, false, true)
						})
				}
					?: tuple5<Icon, String, String?, String?, InsertHandler<LookupElement>?>(
						JuliaIcons.JULIA_FUNCTION_ICON, element.text, null, null)
				JuliaSymbolKind.TypeName,
				JuliaSymbolKind.PrimitiveTypeName,
				JuliaSymbolKind.AbstractTypeName -> tuple5(
					JuliaIcons.JULIA_TYPE_ICON,
					element.text,
					null,
					null
				)
				JuliaSymbolKind.FunctionParameter,
				JuliaSymbolKind.LambdaParameter -> tuple5(
					JuliaIcons.JULIA_VARIABLE_ICON,
					element.text,
					null,
					element.type
				)
				JuliaSymbolKind.GlobalName -> tuple5(
					JuliaIcons.JULIA_VARIABLE_ICON,
					element.text,
					null,
					element.type
				)
				else -> return true
			}
			if (element.symbolKind.isDeclaration && element.hasNoError && isInScope(element))
				candidateSet += LookupElementBuilder
					.create(value)
					.withIcon(icon)
					// tail text, it will not be completed by Enter Key press
					.withTailText(tail, true)
					// the type of return value,show at right of popup
					.withTypeText(type, true)
					.withInsertHandler(handler)
		}
		return true
	}
}
