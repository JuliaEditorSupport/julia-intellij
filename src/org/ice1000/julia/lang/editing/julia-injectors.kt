package org.ice1000.julia.lang.editing

import com.intellij.lang.Language
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import org.ice1000.julia.lang.forceRun
import org.ice1000.julia.lang.psi.*
import org.intellij.lang.regexp.RegExpLanguage

class JuliaLanguageInjector : LanguageInjector {
	/** regex: r"RegExp" */
	private fun regex(host: JuliaStringContent): Boolean {
		val parent = host.parent.parent as? JuliaImplicitMultiplyOp ?: return false
		val symbol = parent.children.firstOrNull() as? JuliaSymbol
		return "r" == symbol?.text
	}

	private fun markdown(host: PsiLanguageInjectionHost) =
		host.parent.nextSibling?.let { it is JuliaFunction || it.nextSibling is JuliaFunction } == true

	override fun getLanguagesToInject(host: PsiLanguageInjectionHost, places: InjectedLanguagePlaces) {
		host as? JuliaStringContent ?: return
		when {
			regex(host) -> places.addPlace(RegExpLanguage.INSTANCE, TextRange(0, host.textLength), null, null)
			markdown(host) -> forceRun {
				val markdownLanguage = Language.findLanguageByID("Markdown")
					?: Language.findLanguageByID("MultiMarkdown")
					?: return@forceRun
				places.addPlace(markdownLanguage, TextRange(0, host.textLength), null, null)
			}
		}
	}
}
