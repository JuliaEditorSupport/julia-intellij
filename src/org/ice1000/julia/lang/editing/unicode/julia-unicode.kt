package org.ice1000.julia.lang.editing.unicode

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.CharFilter
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.util.textCompletion.TextCompletionProvider
import icons.JuliaIcons

object JuliaUnicodeFileType : LanguageFileType(JuliaUnicodeLanguage.INSTANCE) {
	override fun getIcon() = JuliaIcons.JULIA_BIG_ICON
	override fun getName() = language.displayName
	override fun getDefaultExtension() = ""
	override fun getDescription() = ""
}

object JuliaUnicodeCompletionProvider : TextCompletionProvider {
	private val unicodeList = listOf(
		"alpha" to "α", "beta" to "β", "gamma" to "γ", "delta" to "δ", "epsilon" to "ϵ"
	).map { (a, b) ->
		LookupElementBuilder.create(b)
			.withLookupString(a)
			.withPresentableText(a)
			.withIcon(JuliaIcons.JULIA_BIG_ICON)
	}

	override fun applyPrefixMatcher(result: CompletionResultSet, prefix: String): CompletionResultSet {
		return result
	}

	override fun getAdvertisement(): String? {
		return null
	}

	override fun getPrefix(text: String, offset: Int): String? {
		return null
	}

	override fun fillCompletionVariants(parameters: CompletionParameters, prefix: String, result: CompletionResultSet) {
		if (prefix == "\\") unicodeList.forEach(result::addElement)
	}

	override fun acceptChar(c: Char): CharFilter.Result? {
		return null
	}
}
