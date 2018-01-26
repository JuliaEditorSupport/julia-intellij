package org.ice1000.julia.lang

import com.intellij.CommonBundle
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.*
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import org.ice1000.julia.lang.psi.impl.processDeclTrivial
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.*


object JuliaFileType : LanguageFileType(JuliaLanguage.INSTANCE) {
	override fun getDefaultExtension() = JULIA_EXTENSION
	override fun getName() = JuliaBundle.message("julia.name")
	override fun getIcon() = JULIA_ICON
	override fun getDescription() = JuliaBundle.message("julia.name.description")
}

class JuliaFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, JuliaLanguage.INSTANCE) {
	override fun getFileType() = JuliaFileType
	override fun processDeclarations(
			processor: PsiScopeProcessor,
			state: ResolveState,
			lastParent: PsiElement?,
			place: PsiElement) = processDeclTrivial(processor, state, lastParent, place)
}

class JuliaFileTypeFactory : FileTypeFactory() {
	override fun createFileTypes(consumer: FileTypeConsumer) = consumer.consume(JuliaFileType, JULIA_EXTENSION)
}

class JuliaContext: TemplateContextType("JULIA",JULIA_LANGUAGE_NAME){
	override fun isInContext(file: PsiFile, offset: Int)=file.name.endsWith(JULIA_EXTENSION)
}

object JuliaBundle {
	@NonNls private const val BUNDLE = "org.ice1000.julia.lang.julia-bundle"
	private val bundle: ResourceBundle by lazy { ResourceBundle.getBundle(BUNDLE) }

	@JvmStatic
	fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
			CommonBundle.message(bundle, key, *params)
}

