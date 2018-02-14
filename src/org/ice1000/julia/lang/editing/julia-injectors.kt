package org.ice1000.julia.lang.editing

import com.intellij.openapi.util.TextRange
import com.intellij.psi.InjectedLanguagePlaces
import com.intellij.psi.LanguageInjector
import com.intellij.psi.PsiLanguageInjectionHost
import org.ice1000.julia.lang.psi.JuliaImplicitMultiplyOp
import org.ice1000.julia.lang.psi.JuliaStringContent
import org.ice1000.julia.lang.psi.JuliaSymbol
import org.intellij.lang.regexp.RegExpLanguage

class JuliaRegexpInjector : LanguageInjector {

	/** rule: r"RegExp" */
	private fun rule(host : JuliaStringContent) : Boolean {
		val parent = host.parent.parent as? JuliaImplicitMultiplyOp ?: return false
		val symbol = parent.children.firstOrNull() as? JuliaSymbol
		return "r" == symbol?.text
	}

	override fun getLanguagesToInject(host: PsiLanguageInjectionHost, places: InjectedLanguagePlaces) {
		(host as? JuliaStringContent)?.takeIf(::rule)?.let {
			places.addPlace(
				RegExpLanguage.INSTANCE,
				TextRange(0, it.text.length),
				null, null
			)
		}
	}
}