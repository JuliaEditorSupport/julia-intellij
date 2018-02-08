@file:Suppress("DEPRECATION")

package org.ice1000.julia.lang.module

import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.ide.util.PropertiesComponent
import com.intellij.ide.util.projectWizard.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModifiableModelsProvider
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.*
import com.intellij.psi.PsiManager
import com.intellij.util.PlatformUtils
import icons.JuliaIcons
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.action.NewJuliaFile

/**
 * @author zxj5470
 * @date 2018/1/31
 */
class JuliaProjectGenerator : DirectoryProjectGeneratorBase<JuliaSettings>(),
	CustomStepProjectGenerator<JuliaSettings> {
	override fun createStep(
		projectGenerator: DirectoryProjectGenerator<JuliaSettings>,
		callback: AbstractNewProjectStep.AbstractCallback<JuliaSettings>) = ProjectSettingsStepBase(projectGenerator, AbstractNewProjectStep.AbstractCallback<JuliaSettings>())

	override fun getLogo() = JuliaIcons.JULIA_BIG_ICON
	override fun getName() = JuliaBundle.message("julia.name")
	override fun createPeer() = JuliaProjectGeneratorPeerKt(JuliaSettings())

	override fun generateProject(project: Project, baseDir: VirtualFile, settings: JuliaSettings, module: Module) {
		ApplicationManager.getApplication().runWriteAction {
			val modifiableModel: ModifiableRootModel = ModifiableModelsProvider.SERVICE.getInstance().getModuleModifiableModel(module)
			JuliaModuleBuilder().setupRootModel(modifiableModel)
			ModifiableModelsProvider.SERVICE.getInstance().commitModuleModifiableModel(modifiableModel)
		}
	}

	@Deprecated("wait until Julia v0.7 or later if Julia can compile to executable easily")
	private fun Project.forCLion() {
		fun generateCMakeFile(baseDir: VirtualFile) = runWriteAction {
			val cmakeList = baseDir.createChildData(this, "CMakeLists.txt")
			VfsUtil.saveText(cmakeList, """
project($name)
add_executable($name
main.jl)""")
		}
		if (PlatformUtils.isCLion() || PlatformUtils.isRider()) {
			val template = FileTemplateManager
				.getInstance(this)
				.getTemplate("Julia Module")
			val root = PsiManager.getInstance(this).findDirectory(baseDir)
			if (root != null)
				FileTemplateUtil.createFromTemplate(template, "main.jl", NewJuliaFile.createProperties(this, name), root)
			generateCMakeFile(baseDir)
		}
	}
}


class JuliaProjectGeneratorPeerKt(private val settings : JuliaSettings) : JuliaProjectGeneratorPeer() {
	init {
		setListeners()
	}

	override fun getSettings() = settings
	override fun dispose() = Unit

	override fun buildUI(settingsStep: SettingsStep) = settingsStep.addExpertPanel(component)
	override fun isBackgroundJobRunning() = false

	override fun addSettingsListener(listener: ProjectGeneratorPeer.SettingsListener) = Unit
	/** Deprecated in 2017.3 But We must override it. */
	@Deprecated("", ReplaceWith("addSettingsListener"))
	override fun addSettingsStateListener(listener: WebProjectGenerator.SettingsStateListener) = Unit

	private fun setListeners() {
		this.useLocalJuliaDistributionRadioButton.addActionListener {
			this.juliaExeField.isEnabled = false
			this.juliaExeField.text = juliaPath
		}

		this.selectJuliaExecutableRadioButton.addActionListener {
			this.juliaExeField.isEnabled = true
			this.juliaExeField.text = settings.exePath
		}
	}
}