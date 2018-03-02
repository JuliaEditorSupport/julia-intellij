package org.ice1000.julia.lang.editing

import com.intellij.codeInsight.hints.*
import com.intellij.psi.PsiElement
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.module.juliaSettings
import org.ice1000.julia.lang.psi.*

enum class JuliaHintType(desc: String, enabled: Boolean) {
	EVAL_HINT(JuliaBundle.message("julia.lint.eval-hints.show"), false) {
		override fun provideHints(elem: PsiElement) =
			if (elem.project.juliaSettings.settings.showEvalHint &&
				elem is JuliaExpr &&
				elem.parent is JuliaStatements)
				providePropertyTypeHint(elem)
			else emptyList()

		override fun isApplicable(elem: PsiElement): Boolean = when (elem) {
			is JuliaApplyFunctionOp,
			is JuliaAssignLevelOp -> true
			else -> false
		}
	};

	abstract fun isApplicable(elem: PsiElement): Boolean
	abstract fun provideHints(elem: PsiElement): List<InlayInfo>
	val option = Option("SHOW_$name", desc, enabled)

	companion object ResolverHolder {
		private fun resolve(elem: PsiElement) = JuliaHintType.values().find { it.isApplicable(elem) }
		fun resolveToEnabled(elem: PsiElement): JuliaHintType? {
			val resolved = resolve(elem) ?: return null
			return resolved.takeIf { it.option.get() }
		}

		private fun providePropertyTypeHint(elem: JuliaExpr): List<InlayInfo> {
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
	override fun getHintInfo(element: PsiElement) = HintInfo.OptionInfo(JuliaHintType.EVAL_HINT.option)
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