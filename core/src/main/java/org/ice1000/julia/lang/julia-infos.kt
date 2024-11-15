package org.ice1000.julia.lang

import com.intellij.CommonBundle
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.*
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile
import icons.JuliaIcons
import org.ice1000.julia.lang.docfmt.DocfmtFileType
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.*

object JuliaFileType : LanguageFileType(JuliaLanguage.INSTANCE) {
	override fun getDefaultExtension() = JULIA_EXTENSION
	override fun getName() = JuliaBundle.message("julia.name")
	override fun getIcon() = JuliaIcons.JULIA_ICON
	override fun getDescription() = JuliaBundle.message("julia.name.description")
}

class JuliaFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, JuliaLanguage.INSTANCE) {
	override fun getFileType() = JuliaFileType
}

class JuliaFileTypeFactory : FileTypeFactory() {
	override fun createFileTypes(consumer: FileTypeConsumer) {
		consumer.consume(JuliaFileType, JULIA_EXTENSION)
		consumer.consume(DocfmtFileType, ExactFileNameMatcher(".$DOCFMT_EXTENSION"))
	}
}

class JuliaContext : TemplateContextType(JULIA_CONTEXT_ID, JULIA_LANGUAGE_NAME) {
	override fun isInContext(file: PsiFile, offset: Int) = file.fileType == JuliaFileType
}

class JuliaLiveTemplateProvider : DefaultLiveTemplatesProvider {
	private companion object DefaultHolder {
		private val DEFAULT = arrayOf("liveTemplates/Julia")
	}

	override fun getDefaultLiveTemplateFiles() = DEFAULT
	override fun getHiddenLiveTemplateFiles(): Array<String>? = null
}

object JuliaBundle {
	@NonNls private const val BUNDLE = "org.ice1000.julia.lang.julia-bundle"
	private val bundle: ResourceBundle by lazy { ResourceBundle.getBundle(BUNDLE) }

	@JvmStatic
	fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
		CommonBundle.message(bundle, key, *params)
}
