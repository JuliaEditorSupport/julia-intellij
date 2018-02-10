package org.ice1000.julia.lang.editing.unicode

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.patterns.PlatformPatterns
import icons.JuliaIcons
import org.ice1000.julia.lang.editing.JuliaCompletionProvider

object JuliaUnicodeFileType : LanguageFileType(JuliaUnicodeLanguage.INSTANCE) {
	override fun getIcon() = JuliaIcons.JULIA_BIG_ICON
	override fun getName() = language.displayName
	override fun getDefaultExtension() = ""
	override fun getDescription() = ""
}

class JuliaUnicodeCompletionContributor : CompletionContributor() {
	private companion object {
		private val unicodeList = listOf(
			"alpha" to "α", "beta" to "β", "gamma" to "γ", "delta" to "δ", "epsilon" to "ϵ"
		).map { (a, b) ->
			LookupElementBuilder.create(b)
				.withLookupString(a)
				.withPresentableText(a)
				.withIcon(JuliaIcons.JULIA_BIG_ICON)
		}
	}

	init {
		extend(CompletionType.BASIC, PlatformPatterns.psiElement(), JuliaCompletionProvider(unicodeList))
	}
}
