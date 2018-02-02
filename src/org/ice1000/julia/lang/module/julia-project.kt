@file:Suppress("DEPRECATION")

package org.ice1000.julia.lang.module

import com.intellij.ide.util.PropertiesComponent
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
import com.intellij.psi.PsiFileFactory
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.layout.panel
import com.intellij.util.Consumer
import com.intellij.util.PlatformUtils
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.editing.JULIA_BIG_ICON
import java.time.LocalDate
import javax.swing.JLabel
import javax.swing.event.DocumentEvent

/**
 * @author: zxj5470
 * @date: 2018/1/31
 */
class JuliaProjectGenerator : DirectoryProjectGeneratorBase<JuliaSettings>(),
	CustomStepProjectGenerator<JuliaSettings> {
	override fun createStep(
		projectGenerator: DirectoryProjectGenerator<JuliaSettings>,
		callback: AbstractNewProjectStep.AbstractCallback<JuliaSettings>?) = JuliaProjectSettingsStep(projectGenerator)

	override fun getLogo() = JULIA_BIG_ICON
	override fun getName() = JuliaBundle.message("julia.name")
	override fun createPeer() = JuliaProjectGeneratorPeer(JuliaSettings())

	override fun generateProject(project: Project, baseDir: VirtualFile, settings: JuliaSettings, module: Module) {
		ApplicationManager.getApplication().runWriteAction {
			val modifiableModel: ModifiableRootModel = ModifiableModelsProvider.SERVICE.getInstance().getModuleModifiableModel(module)
			JuliaModuleBuilder().setupRootModel(modifiableModel)
			ModifiableModelsProvider.SERVICE.getInstance().commitModuleModifiableModel(modifiableModel)
		}
	}

	@Deprecated("wait until Julia v0.7 or later if Julia can compile to executable easily")
	private fun Project.forCLion() {
		fun generateCMakeFile(project: Project, baseDir: VirtualFile) = runWriteAction {
			val cmakeList = baseDir.createChildData(this, "CMakeLists.txt")
			VfsUtil.saveText(cmakeList, """
project(${project.name})
add_executable(${project.name}
main.jl)""")
		}
		if (PlatformUtils.isCLion()) {
			val fileName = "main.jl"
			//CreateFileAction.create is prote
			PsiFileFactory
				.getInstance(this)
				.createFileFromText(fileName, JuliaFileType, """
					$JULIA_BLOCK_COMMENT_BEGIN
					# $fileName
					${JuliaBundle.message("julia.actions.new-file.content", System.getProperty("user.name"), LocalDate.now())}
					$JULIA_BLOCK_COMMENT_END

					""".trimIndent())
			generateCMakeFile(this, baseDir)
		}
	}
}

open class JuliaProjectSettingsStep(generator: DirectoryProjectGenerator<JuliaSettings>)
	: ProjectSettingsStepBase<JuliaSettings>(generator, AbstractNewProjectStep.AbstractCallback<Any>())

/**
 * for other platform
 * FIXME replace soon
 */
class JuliaProjectGeneratorPeer(private val projectSettings: JuliaSettings) :
	ProjectGeneratorPeer<JuliaSettings>, Disposable {
	override fun getSettings() = projectSettings
	override fun dispose() = Unit
	override fun getComponent() = panel {
		// FIXME move to bundle file
		row("Julia Executable Path:") { sdkEditor() }
		row("Julia SDK version:") { versionToLabel() }
//		row("Download SDK:") { downloadJuliaSdkLink() }
	}

	override fun validate() =
		if (validateJuliaExe(settings.exePath)) null
		else ValidationInfo(JuliaBundle.message("julia.projects.sdk.invalid"))

	override fun buildUI(settingsStep: SettingsStep) = settingsStep.addExpertPanel(component)
	override fun isBackgroundJobRunning() = false
	override fun addSettingsListener(listener: ProjectGeneratorPeer.SettingsListener) = Unit
	/** Deprecated in 2017.3 But We must override it. */
	@Deprecated("", ReplaceWith("addSettingsListener"))
	override fun addSettingsStateListener(listener: WebProjectGenerator.SettingsStateListener) = Unit

	private val sdkEditor = pathToDirectoryTextField(defaultExePath)
	private val versionToLabel = JLabel(versionOf(projectSettings.exePath))
	private fun pathToDirectoryTextField(
		content: String,
		onTextChanged: Consumer<DocumentEvent?>? = null): TextFieldWithBrowseButton {
		val component = TextFieldWithBrowseButton(null, this)
		component.text = content
		component.addBrowseFolderListener(content, null, null,
			FileChooserDescriptorFactory.createSingleFolderDescriptor(),
			TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT)
		component.childComponent.document.addDocumentListener(object : DocumentAdapter() {
			override fun textChanged(e: DocumentEvent?) {
				onTextChanged?.consume(e)
			}
		})
		val existPath = PropertiesComponent.getInstance().getValue(JULIA_SDK_HOME_PATH_ID).orEmpty()
		if (validateJuliaExe(existPath)) {
			component.text = existPath
		}
		return component
	}
}
