@file:Suppress("DEPRECATION")

package org.ice1000.julia.lang.module

import com.intellij.ide.util.projectWizard.AbstractNewProjectStep
import com.intellij.ide.util.projectWizard.CustomStepProjectGenerator
import com.intellij.ide.util.projectWizard.ProjectSettingsStepBase
import com.intellij.ide.util.projectWizard.SettingsStep
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModifiableModelsProvider
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.DirectoryProjectGenerator
import com.intellij.platform.DirectoryProjectGeneratorBase
import com.intellij.platform.ProjectGeneratorPeer
import com.intellij.platform.WebProjectGenerator
import org.ice1000.julia.lang.*
import javax.swing.Icon
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.layout.Row
import com.intellij.ui.layout.panel
import javax.swing.JLabel


/**
 * @author: zxj5470
 * @date: 2018/1/31
 */
class JuliaProjectGenerator : DirectoryProjectGeneratorBase<Any>(), CustomStepProjectGenerator<Any> {
	override fun createStep(projectGenerator: DirectoryProjectGenerator<Any>, callback: AbstractNewProjectStep.AbstractCallback<Any>?)
		= JuliaProjectSettingsStep(projectGenerator)

	class JuliaProjectSettingsStep(generator: DirectoryProjectGenerator<Any>)
		: ProjectSettingsStepBase<Any>(generator, AbstractNewProjectStep.AbstractCallback<Any>())

	override fun getLogo(): Icon = JULIA_BIG_ICON
	override fun getName(): String = JULIA_LANGUAGE_NAME
	override fun createPeer(): ProjectGeneratorPeer<Any> {
		return JuliaProjectGeneratorPeer()
	}

	override fun generateProject(project: Project, baseDir: VirtualFile, settings: Any, module: Module) {
		ApplicationManager.getApplication().runWriteAction {
			val modifiableModel: ModifiableRootModel = ModifiableModelsProvider.SERVICE.getInstance().getModuleModifiableModel(module)
			JuliaModuleBuilder().setupRootModel(modifiableModel)
			ModifiableModelsProvider.SERVICE.getInstance().commitModuleModifiableModel(modifiableModel)
		}
	}
}

class JuliaProjectGeneratorPeer : ProjectGeneratorPeer<Any>, Disposable {

	override fun getComponent() = panel {
		row("Julia SDK Home Location:") { sdkEditor() }
		row("Julia SDK version:") { versionToLabel() }
		row("Download SDK:") { downloadJuliaSdkLink() }
	}

	override fun dispose() = Unit
	/**
	 * null is validate ... wtf
	 */
	override fun validate(): ValidationInfo? {
//		return ValidationInfo("Validate")
		return null
	}

	override fun buildUI(settingsStep: SettingsStep) {
		settingsStep.addExpertPanel(component)
	}

	private val sdkEditor = pathToDirectoryTextField(sdkHomePath)
	private fun pathToDirectoryTextField(content: String, onTextChanged: (e: javax.swing.event.DocumentEvent?) -> Unit = {})
		: TextFieldWithBrowseButton {
		val component = TextFieldWithBrowseButton(null, this)
		component.text = content
		component.addBrowseFolderListener(content, null, null,
			FileChooserDescriptorFactory.createSingleFolderDescriptor(),
			TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT)
		component.childComponent.document.addDocumentListener(object : DocumentAdapter() {
			override fun textChanged(e: javax.swing.event.DocumentEvent?) {
				onTextChanged(e)
			}
		})
		return component
	}

	override fun isBackgroundJobRunning() = false

	override fun getSettings(): Any {
		return ""
	}

	override fun addSettingsListener(listener: ProjectGeneratorPeer.SettingsListener) {

	}

	/**
	 * Deprecated in 2017.3 ! But We must override it.
	 */
	@Deprecated("", ReplaceWith("addSettingsListener"))
	override fun addSettingsStateListener(listener: WebProjectGenerator.SettingsStateListener) = Unit
}
