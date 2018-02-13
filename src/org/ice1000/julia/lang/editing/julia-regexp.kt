package org.ice1000.julia.lang.editing

import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.InjectedLanguagePlaces
import com.intellij.psi.LanguageInjector
import com.intellij.psi.PsiLanguageInjectionHost
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.psi.impl.JuliaStringContentImpl
import org.intellij.lang.regexp.RegExpLanguage

class JuliaRegexpInjector : LanguageInjector {
	override fun getLanguagesToInject(host: PsiLanguageInjectionHost, places: InjectedLanguagePlaces) {
		if (host is JuliaStringContentImpl) {
			val prefix = JuliaBundle.message("regexp.pre-fix.text")
			val text = StringUtil.stripQuotesAroundValue(host.text)
			if (text.startsWith(prefix)) {
				places.addPlace(
					RegExpLanguage.INSTANCE,
					TextRange(
						prefix.length,
						text.length
					),
					null,
					null
				)
			}
		}
	}
}