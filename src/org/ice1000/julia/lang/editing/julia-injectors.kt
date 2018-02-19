package org.ice1000.julia.lang.editing

import com.intellij.lang.Language
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import org.ice1000.julia.lang.forceRun
import org.ice1000.julia.lang.psi.JuliaRegex
import org.ice1000.julia.lang.psi.JuliaString
import org.ice1000.julia.lang.psi.impl.DocStringOwner
import org.intellij.lang.regexp.RegExpLanguage

/**
 * Invoked before Annotator, so initialize DocStrings here
 * @author ice1000, HoshinoTented
 */
class JuliaLanguageInjector : LanguageInjector {

	private fun markdown(host: PsiLanguageInjectionHost) =
		host.parent.nextSibling?.let { it as? DocStringOwner ?: it.nextSibling as? DocStringOwner }

	override fun getLanguagesToInject(host: PsiLanguageInjectionHost, places: InjectedLanguagePlaces) {
		when (host) {
			is JuliaString -> if (host.isDocString || markdown(host)?.let { it.docString = host } != null) forceRun {
				host.isDocString = true
				val markdownLanguage = Language.findLanguageByID("Markdown")
					?: Language.findLanguageByID("MultiMarkdown")
					?: return@forceRun
				places.addPlace(markdownLanguage, TextRange(0, host.textLength), null, null)
			}
			is JuliaRegex ->
				places.addPlace(RegExpLanguage.INSTANCE, TextRange(0, host.textLength), "^", "$")
		}
	}
}
