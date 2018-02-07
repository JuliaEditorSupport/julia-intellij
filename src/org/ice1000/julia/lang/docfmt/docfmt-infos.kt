package org.ice1000.julia.lang.docfmt

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileNameMatcher
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.psi.FileViewProvider
import icons.JuliaIcons
import org.ice1000.julia.lang.*

object DocfmtFileType : LanguageFileType(DocfmtLanguage.INSTANCE) {
	override fun getDefaultExtension() = DOCFMT_EXTENSION
	override fun getName() = JuliaBundle.message("docfmt.name")
	override fun getIcon() = JuliaIcons.DOCFMT_ICON
	override fun getDescription() = JuliaBundle.message("docfmt.name.description")
}

class DocfmtFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, DocfmtLanguage.INSTANCE) {
	override fun getFileType() = DocfmtFileType
}
