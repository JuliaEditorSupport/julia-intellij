package org.ice1000.julia.lang.editing

import com.intellij.lang.Language
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import org.ice1000.julia.lang.forceRun
import org.ice1000.julia.lang.psi.*
import org.intellij.lang.regexp.RegExpLanguage

/**
 * Invoked before Annotator, so initialize DocStrings here
 * @author ice1000, HoshinoTented
 */
class JuliaLanguageInjector : LanguageInjector {
	/** regex: r"RegExp" */
	private fun regex(host: PsiLanguageInjectionHost): Boolean {
		val parent = host.parent.parent as? JuliaImplicitMultiplyOp ?: return false
		val symbol = parent.children.firstOrNull() as? JuliaSymbol
		return "r" == symbol?.text
	}

	private fun markdown(host: PsiLanguageInjectionHost) =
		host.parent.nextSibling?.let { it as? JuliaFunction ?: it.nextSibling as? JuliaFunction }

	override fun getLanguagesToInject(host: PsiLanguageInjectionHost, places: InjectedLanguagePlaces) {
		if (host !is JuliaStringContent) return
		when {
			host.isRegex || regex(host) -> {
				host.isRegex = true
				places.addPlace(RegExpLanguage.INSTANCE, TextRange(0, host.textLength), null, null)
			}
			host.isDocString || markdown(host)?.let { it.docString = host } != null -> forceRun {
				host.isDocString = true
				val markdownLanguage = Language.findLanguageByID("Markdown")
					?: Language.findLanguageByID("MultiMarkdown")
					?: return@forceRun
				places.addPlace(markdownLanguage, TextRange(0, host.textLength), null, null)
			}
		}
	}
}
