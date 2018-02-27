package org.ice1000.julia.lang.editing.hint

import com.intellij.codeInsight.hints.*
import com.intellij.psi.PsiElement
import org.ice1000.julia.lang.JuliaLanguage
import org.ice1000.julia.lang.executeJulia
import org.ice1000.julia.lang.module.juliaSettings
import org.ice1000.julia.lang.psi.*

enum class JuliaHintType(desc: String, enabled: Boolean) {
	EVAL_HINT("Show Eval value inline type hints", false) {
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
	val option = Option("SHOW_${this.name}", desc, enabled)

	companion object ResolverHolder {
		private fun resolve(elem: PsiElement) = JuliaHintType.values().find { it.isApplicable(elem) }
		fun resolveToEnabled(elem: PsiElement): JuliaHintType? {
			val resolved = resolve(elem) ?: return null
			return resolved.takeIf { it.option.get() }
		}

		fun providePropertyTypeHint(elem: JuliaExpr): List<InlayInfo> {
			val juliaExe = elem.project.juliaSettings.settings.exePath // 这不就是工程的Julia路径吗
			val output = buildString {
				val (stdout, stderr) = executeJulia(juliaExe, elem.text, 1000)
				if (stdout.isNotEmpty()) {
					append(" => ")
					stdout.forEach { append(it) }
				}
				if (stderr.isNotEmpty()) stderr.forEach { appendln(it) }
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