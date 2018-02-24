package org.ice1000.julia.lang

import com.intellij.CommonBundle
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.EditorFactoryAdapter
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileTypes.*
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import icons.JuliaIcons
import org.ice1000.julia.lang.docfmt.DocfmtFileType
import org.ice1000.julia.lang.psi.impl.processDeclTrivial
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
	override fun processDeclarations(
		processor: PsiScopeProcessor,
		state: ResolveState,
		lastParent: PsiElement?,
		place: PsiElement) = processDeclTrivial(processor, state, lastParent, place)
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

class JuliaApplicationComponent : ApplicationComponent, Disposable {
	private companion object EditorListener : EditorFactoryAdapter() {
		private val isRelease by lazy {
			PluginManager.getPlugin(PluginId.getId(JULIA_PLUGIN_ID))?.run { '-' !in version } == true
		}

		private fun checkRelease() {
			if (!isRelease) {
// 			TODO something here
			}
		}
	}

	override fun getComponentName() = "JuliaApplicationComponent"
	override fun dispose() = disposeComponent()
	override fun initComponent() {
		EditorFactory.getInstance().addEditorFactoryListener(EditorListener, this)
	}
}