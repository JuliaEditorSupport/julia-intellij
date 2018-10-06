package org.ice1000.julia.lang.editing

import com.intellij.codeInsight.hints.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.lang.parameterInfo.*
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.module.juliaSettings
import org.ice1000.julia.lang.psi.*

enum class JuliaHintType(desc: String, enabled: Boolean) {
	EVAL_HINT(JuliaBundle.message("julia.lint.eval-hints.show"), false) {
		override fun provideHints(elem: PsiElement) =
			if (elem.project.juliaSettings.settings.showEvalHint &&
				elem is JuliaExpr &&
				elem.parent is JuliaStatements)
				provideEvalHint(elem)
			else emptyList()

		override fun isApplicable(elem: PsiElement): Boolean = when (elem) {
			is JuliaApplyFunctionOp -> elem.project.juliaSettings.settings.showEvalHint
			else -> false
		}
	},
	TYPE_HINT(JuliaBundle.message("julia.lint.type-hints.show"), true) {
		override fun provideHints(elem: PsiElement) = provideTypeHint(elem)

		// is JuliaAssignOp or JuliaTypedNamedVariable (which is in function signature)
		override fun isApplicable(elem: PsiElement): Boolean =
			elem is JuliaAssignOp &&
				elem.parent is JuliaStatements &&
				elem.exprList.first() !is JuliaTypeOp

	},
	FUNCTION_SIGNATURE_PARAMETERS_HINT(JuliaBundle.message("julia.lint.function-param-hints.show"), false) {
		override fun isApplicable(elem: PsiElement): Boolean = (elem is JuliaTypedNamedVariable && elem.parent is JuliaFunctionSignature)

		override fun provideHints(elem: PsiElement): List<InlayInfo> = provideTypeHint(elem)
	},
	// TODO
	APPLY_FUNCTION_PARAMETERS_HINT(JuliaBundle.message("julia.lint.apply-function-param-hints.show"), false) {
		override fun isApplicable(elem: PsiElement): Boolean =
			(elem is JuliaApplyFunctionOp)

		override fun provideHints(elem: PsiElement): List<InlayInfo> = emptyList()
	};

	abstract fun isApplicable(elem: PsiElement): Boolean
	abstract fun provideHints(elem: PsiElement): List<InlayInfo>
	val option = Option("SHOW_$name", desc, enabled)

	companion object ResolverHolder {
		fun resolve(elem: PsiElement) = JuliaHintType.values().find { it.isApplicable(elem) }
		fun resolveToEnabled(elem: PsiElement): JuliaHintType? {
			val resolved = resolve(elem) ?: return null
			return resolved.takeIf { it.option.get() }
		}

		private fun provideTypeHint(elem: PsiElement): List<InlayInfo> {
			(elem as? JuliaAssignOp)?.run {
				val offset = exprList.first().textLength
				val rValue = exprList.lastOrNull() ?: return emptyList()
				val type = parseType(rValue) ?: return emptyList()
				return listOf(InlayInfo("::$type", elem.textOffset + offset))
			}
			// function parameters
			(elem as? JuliaTypedNamedVariable)?.run {
				if (elem.typeAnnotation != null) return emptyList()
				val offset = exprList.first().textLength
				val rValue = exprList.lastOrNull() ?: return emptyList()
				val type = parseType(rValue) ?: return emptyList()
				return listOf(InlayInfo("::$type", (elem as PsiElement).textOffset + offset))
			}
			return emptyList()
		}

		private fun parseType(elem: PsiElement): String? {
			return when (elem) {
				is JuliaArray -> JuliaRValueLiteral.array(elem)
				is JuliaInteger -> JuliaRValueLiteral.integer(elem)
				is JuliaCharLit -> "Char"
				is JuliaCommand -> "Cmd"
				is JuliaString -> "String"
				is JuliaVersionNumber -> "VersionNumber"
				is JuliaExpr -> elem.type.takeIf { it != null }
				else -> null
			}
		}

		private fun provideEvalHint(elem: JuliaExpr): List<InlayInfo> {
			val juliaExe = elem.project.juliaSettings.settings.exePath // 这不就是工程的Julia路径吗
			val output = buildString {
				val (stdout, stderr) = executeJulia(juliaExe, elem.text, 1000)
				if (stdout.isNotEmpty()) stdout.joinTo(this, separator = ";", prefix = "=> ")
				if (stderr.isNotEmpty()) stderr.joinTo(this, separator = ";", prefix = "=!> ")
			}
			val text = if ("print" in elem.text) output.substringBefore("nothing") else output
			return listOf(InlayInfo(text, elem.textOffset + elem.text.length))
		}
	}
}

class JuliaInlayParameterHintsProvider : InlayParameterHintsProvider {
	override fun getBlackListDependencyLanguage() = JuliaLanguage.INSTANCE
	override fun getHintInfo(element: PsiElement): HintInfo? {
		val hintType = JuliaHintType.resolve(element) ?: return null
		return HintInfo.OptionInfo(hintType.option)
	}

	override fun getParameterHints(element: PsiElement) = JuliaHintType.resolveToEnabled(element)?.provideHints(element)
		?: emptyList()

	override fun canShowHintsWhenDisabled() = true
	override fun getDefaultBlackList(): Set<String> = emptySet()
	override fun isBlackListSupported() = false
	override fun getSupportedOptions() = JuliaHintType.values().map { it.option }

	/**
	 * @param inlayText String: `text` in InlayInfo
	 * default value append a `:` after inlayText so that we must override it.
	 */
	override fun getInlayPresentation(inlayText: String) = inlayText
}

/**
 * Thanks to Kotlin plugin and IntelliJ-Rust plugin.
 */
class JuliaParameterInfo : ParameterInfoHandler<PsiElement, JuliaArgumentsDescription> {
	var hintText: String = ""

	override fun showParameterInfo(element: PsiElement, context: CreateParameterInfoContext) {
		/**
		 * itemsToShow is very important, and [description] is provided to [updateUI]
		 */
		val description = JuliaArgumentsDescription.findDescription(element) ?: return
		context.itemsToShow = arrayOf(description)
		context.showHint(element, element.textRange.startOffset, this)
	}

	override fun updateParameterInfo(elem: PsiElement, context: UpdateParameterInfoContext) {
		val grand = elem.parent.parent
		if (grand is JuliaApplyFunctionOp) {
			val index = grand.exprList.indexOfFirst { it === elem.parent }
			if (index != -1) {
				context.setCurrentParameter(index - 1)
			}
		} else if (grand is JuliaArguments) {
			// TODO `keyword arguments`
//			context.setCurrentParameter(magicIndex)
		}
	}

	override fun updateUI(p: JuliaArgumentsDescription?, context: ParameterInfoUIContext) {
		if (p == null) {
			context.isUIComponentEnabled = false
			return
		}
		val range = p.getArgumentRange(context.currentParameterIndex)
		hintText = p.presentText
		context.setupUIComponentPresentation(
			hintText,
			range.startOffset,
			range.endOffset,
			!context.isUIComponentEnabled,
			false,
			false,
			context.defaultParameterColor)
	}

	override fun getParametersForLookup(item: LookupElement, context: ParameterInfoContext?): Array<out Any>? {
		val elem = item.`object` as? PsiElement ?: return null
		val parent = elem.parent ?: return null
		val isCall = parent is JuliaApplyFunctionOp
		return if (isCall) arrayOf(parent) else emptyArray()
	}

	override fun couldShowInLookup() = true

	override fun findElementForUpdatingParameterInfo(context: UpdateParameterInfoContext): PsiElement? {
		return context.file.findElementAt(context.editor.caretModel.offset)
	}

	override fun findElementForParameterInfo(context: CreateParameterInfoContext): PsiElement? {
		val contextElement = context.file.findElementAt(context.editor.caretModel.offset) ?: return null
		val ret = PsiTreeUtil.getParentOfType(contextElement, JuliaApplyFunctionOp::class.java, /* strict */ true)
		return ret
	}
}

class JuliaArgumentsDescription(val arguments: Array<String>) {
	fun getArgumentRange(index: Int): TextRange {
		if (index < 0 || index >= arguments.size) return TextRange.EMPTY_RANGE
		val start = arguments.take(index).sumBy { it.length + 2 }
		return TextRange(start, start + arguments[index].length)
	}

	// TODO: add `;` for the rest
	val presentText = if (arguments.isEmpty()) "<no arguments>" else arguments.joinToString(", ")

	companion object {
		/**
		 * Finds declaration of the func/method and creates description of its arguments
		 * @param current JuliaApplyFunctionOp
		 */
		fun findDescription(current: PsiElement): JuliaArgumentsDescription? {
			val callInfo = when (current) {
				is JuliaApplyFunctionOp -> CallInfo.resolve(current)
				else -> null
			} ?: return null
			val params = callInfo.parameters.map { "${it.parameterName}::${it.parameterType}" }
			return JuliaArgumentsDescription(params.toTypedArray())
		}
	}
}

class CallInfo private constructor(
	val methodName: String?,
	val selfParameter: String?,
	val parameters: List<Parameter>) {
	class Parameter(val parameterName: String, val parameterType: String)

	companion object {
		fun resolve(call: JuliaApplyFunctionOp): CallInfo? {
			var stringBeforeCall = ""
			val first = call.exprList.first()

			val ref = if (first is JuliaMemberAccessOp) {
				// get function name for memberAssessOp
				stringBeforeCall = first.exprList.first().toString()
				first.exprList.lastOrNull()?.reference
			} else {
				first.reference
			} ?: return null

			val functionRef = ref.resolve()?.parent
			val parameters = when (functionRef) {
				is JuliaFunction -> functionRef.functionSignature?.generateParameters()
				is JuliaCompactFunction -> functionRef.functionSignature.generateParameters()
				else -> null
			} ?: emptyList()
			return CallInfo(first.text, stringBeforeCall, parameters)
		}

		private fun JuliaFunctionSignature.generateParameters() =
			typedNamedVariableList.map {
				CallInfo.Parameter(it.firstChild.text, it.typeAnnotation?.text?.substringAfter("::") ?: "Any")
			}
	}
}