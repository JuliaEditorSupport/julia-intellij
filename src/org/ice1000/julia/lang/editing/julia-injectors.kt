package org.ice1000.julia.lang.editing

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import org.ice1000.julia.lang.psi.JuliaRegex
import org.ice1000.julia.lang.psi.JuliaString
import org.ice1000.julia.lang.psi.impl.isDocString
import org.intellij.lang.regexp.RegExpLanguage

/**
 * Invoked before Annotator, so initialize DocStrings here
 * @author ice1000, HoshinoTented
 */
class JuliaLanguageInjector : LanguageInjector {

	override fun getLanguagesToInject(host: PsiLanguageInjectionHost, places: InjectedLanguagePlaces) {
		when (host) {
			is JuliaString -> if (host.isDocString) {
				val markdownLanguage: Language = Language.findLanguageByID("Markdown")
					?: Language.findLanguageByID("MultiMarkdown")
					?: PlainTextLanguage.INSTANCE
				val start = host.firstChild.textLength
				val length = host.textLength - host.lastChild.textLength
				places.addPlace(markdownLanguage, TextRange(start, length), null, null)
			}
			is JuliaRegex ->
				places.addPlace(RegExpLanguage.INSTANCE, TextRange(2, host.textLength - 1), null, null)
		}
	}
}
