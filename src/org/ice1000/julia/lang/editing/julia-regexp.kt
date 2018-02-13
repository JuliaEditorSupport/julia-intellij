package org.ice1000.julia.lang.editing

import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.InjectedLanguagePlaces
import com.intellij.psi.LanguageInjector
import com.intellij.psi.PsiLanguageInjectionHost
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.psi.JuliaImplicitMultiplyOp
import org.ice1000.julia.lang.psi.JuliaSymbol
import org.ice1000.julia.lang.psi.impl.JuliaStringContentImpl
import org.intellij.lang.regexp.RegExpLanguage

class JuliaRegexpInjector : LanguageInjector {

	/**
	 * rule: r"RegExp"
	 */
	private fun rule(host : JuliaStringContentImpl) : Boolean {
		val parent = host.parent.parent

		if( parent is JuliaImplicitMultiplyOp ) {
			val symbol = parent.children.firstOrNull() as? JuliaSymbol
			//println(symbol?.text)
			if (symbol != null && symbol.text == "r") {
				return true
			}
		}

		return false
	}

	override fun getLanguagesToInject(host: PsiLanguageInjectionHost, places: InjectedLanguagePlaces) {
		if (host is JuliaStringContentImpl) {
			if (rule(host)) {
				places.addPlace(
					RegExpLanguage.INSTANCE,
					TextRange(0, host.text.length),
					null, null
				)
			}
		}
	}
}