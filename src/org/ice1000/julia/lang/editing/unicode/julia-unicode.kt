package org.ice1000.julia.lang.editing.unicode

import com.intellij.openapi.fileTypes.LanguageFileType
import icons.JuliaIcons

object JuliaUnicodeFileType : LanguageFileType(JuliaUnicodeLanguage.INSTANCE) {
	override fun getIcon() = JuliaIcons.JULIA_BIG_ICON
	override fun getName() = language.displayName
	override fun getDefaultExtension() = ""
	override fun getDescription() = ""
}
