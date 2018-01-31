@file:Suppress("DEPRECATION")

package org.ice1000.julia.lang.module

import com.intellij.ide.util.projectWizard.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModifiableModelsProvider
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.ui.*
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.*
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.layout.panel
import com.intellij.util.PlatformUtils
import org.ice1000.julia.lang.JULIA_BIG_ICON
import org.ice1000.julia.lang.JULIA_LANGUAGE_NAME
import javax.swing.Icon


/**
 * @author: zxj5470
 * @date: 2018/1/31
 */
class JuliaProjectGenerator : DirectoryProjectGeneratorBase<JuliaProjectSettings>(), CustomStepProjectGenerator<JuliaProjectSettings> {
	override fun createStep(
		projectGenerator: DirectoryProjectGenerator<JuliaProjectSettings>,
		callback: AbstractNewProjectStep.AbstractCallback<JuliaProjectSettings>?) = JuliaProjectSettingsStep(projectGenerator)

	override fun getLogo(): Icon = JULIA_BIG_ICON
	override fun getName(): String = JULIA_LANGUAGE_NAME
	override fun createPeer() = JuliaProjectGeneratorPeer()

	override fun generateProject(project: Project, baseDir: VirtualFile, settings: JuliaProjectSettings, module: Module) {
		ApplicationManager.getApplication().runWriteAction {
			val modifiableModel: ModifiableRootModel = ModifiableModelsProvider.SERVICE.getInstance().getModuleModifiableModel(module)
			JuliaModuleBuilder().setupRootModel(modifiableModel)
			ModifiableModelsProvider.SERVICE.getInstance().commitModuleModifiableModel(modifiableModel)
			if(PlatformUtils.isCLion()){
				generateCMakeFile(project, baseDir)
			}
		}
	}
	private fun generateCMakeFile(project: Project, baseDir: VirtualFile) = runWriteAction {
		val cmakeList = baseDir.createChildData(this, "CMakeLists.txt")
		VfsUtil.saveText(cmakeList, """
                project(${project.name})
                add_executable(${project.name}
                        main.jl""".trimIndent())
	}
}

open class JuliaProjectSettingsStep(generator: DirectoryProjectGenerator<JuliaProjectSettings>)
	: ProjectSettingsStepBase<JuliaProjectSettings>(generator, AbstractNewProjectStep.AbstractCallback<Any>())

class JuliaProjectSettings(
	var sdkHome: String = defaultSdkHome,
	tryEvaluateTimeLimit: Long = 2500L,
	tryEvaluateTextLimit: Int = 320) : JuliaSdkData(tryEvaluateTimeLimit, tryEvaluateTextLimit)

class JuliaProjectGeneratorPeer : ProjectGeneratorPeer<JuliaProjectSettings>, Disposable {
	override fun getComponent() = panel {
		row("Julia SDK Home Location:") { sdkEditor() }
		row("Julia SDK version:") { versionToLabel() }
		row("Download SDK:") { downloadJuliaSdkLink() }
	}

	override fun dispose() = Unit
	override fun validate(): ValidationInfo? {
//		return ValidationInfo("Validate")
		return null
	}

	override fun buildUI(settingsStep: SettingsStep) {
		settingsStep.addExpertPanel(component)
	}

	private val sdkEditor = pathToDirectoryTextField(defaultSdkHome)
	private fun pathToDirectoryTextField(
		content: String,
		onTextChanged: (e: javax.swing.event.DocumentEvent?) -> Unit = {})
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
	override fun getSettings() = JuliaProjectSettings()
	override fun addSettingsListener(listener: ProjectGeneratorPeer.SettingsListener) {
	}

	/** Deprecated in 2017.3 But We must override it. */
	@Deprecated("", ReplaceWith("addSettingsListener"))
	override fun addSettingsStateListener(listener: WebProjectGenerator.SettingsStateListener) = Unit
}
