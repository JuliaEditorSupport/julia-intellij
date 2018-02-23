package org.ice1000.julia.lang.editing.hint

import com.intellij.codeInsight.hints.*
import com.intellij.psi.PsiElement
import org.ice1000.julia.lang.JuliaLanguage
import org.ice1000.julia.lang.psi.*

enum class JuliaHintType(desc: String, enabled: Boolean) {
	EVAL_HINT("Show Eval value inline type hints (Warning: it will make your editor become very slow!!!)", false) {
		override fun provideHints(elem: PsiElement) = providePropertyTypeHint(elem)
		override fun isApplicable(elem: PsiElement): Boolean = when(elem){
			is JuliaApplyFunctionOp,
			is JuliaAssignLevelOp->true
			else -> false
		}
	};

	abstract fun isApplicable(elem: PsiElement): Boolean
	abstract fun provideHints(elem: PsiElement): List<InlayInfo>
	val option = Option("SHOW_${this.name}", desc, enabled)
	val enabled
		get() = option.get()

	companion object {
		fun resolve(elem: PsiElement): JuliaHintType? = JuliaHintType.values().find { it.isApplicable(elem) }
		fun resolveToEnabled(elem: PsiElement?): JuliaHintType? {
			val resolved = elem?.let { resolve(it) } ?: return null
			return if (resolved.enabled) {
				resolved
			} else {
				null
			}
		}
	}
}

class JuliaInlayParameterHintsProvider : InlayParameterHintsProvider {


	override fun getBlackListDependencyLanguage() = JuliaLanguage.INSTANCE
	override fun getHintInfo(element: PsiElement): HintInfo? {
		return HintInfo.OptionInfo(JuliaHintType.EVAL_HINT.option)
	}

	override fun getParameterHints(element: PsiElement) = JuliaHintType.resolveToEnabled(element)?.provideHints(element) ?: emptyList()

	override fun canShowHintsWhenDisabled() = true
	override fun getDefaultBlackList() = setOf("")
	override fun isBlackListSupported() = false
	override fun getSupportedOptions(): List<Option> = JuliaHintType.values().map { it.option }

	/**
	 * @param inlayText String: `text` in InlayInfo
	 * default value append a `:` after inlayText so that we must override it.
	 */
	override fun getInlayPresentation(inlayText: String) = inlayText

}