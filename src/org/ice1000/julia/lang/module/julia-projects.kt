package org.ice1000.julia.lang.module

import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.ide.util.projectWizard.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModifiableModelsProvider
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.DirectoryProjectGenerator
import com.intellij.platform.DirectoryProjectGeneratorBase
import com.intellij.psi.PsiManager
import com.intellij.util.PlatformUtils
import icons.JuliaIcons
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.action.NewJuliaFile
import org.ice1000.julia.lang.module.ui.JuliaProjectGeneratorPeerImpl

/**
 * For non-IntelliJ IDE (PyCharm,CLion...)
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
	override fun createPeer() = JuliaProjectGeneratorPeerImpl()

	override fun generateProject(project: Project, baseDir: VirtualFile, settings: JuliaSettings, module: Module) {
		ApplicationManager.getApplication().runWriteAction {
			val modifiableModel: ModifiableRootModel = ModifiableModelsProvider.SERVICE.getInstance().getModuleModifiableModel(module)
			createDir(module, baseDir, "src")
			ModifiableModelsProvider.SERVICE.getInstance().commitModuleModifiableModel(modifiableModel)
			project.forCLion()
		}
	}

	/**
	 * wait until Julia v0.7 or later if Julia can compile to executable easily
	 */
	private fun Project.forCLion() {
		if (PlatformUtils.isCLion()) {
			fun generateCMakeFile(baseDir: VirtualFile) = runWriteAction {
				val cmakeList = baseDir.createChildData(this, "CMakeLists.txt")
				VfsUtil.saveText(cmakeList, """
project($name)
""")
			}

			val template = FileTemplateManager
				.getInstance(this)
				.getTemplate("Julia Module")
			PsiManager.getInstance(this).findDirectory(baseDir.createChildDirectory(null, "src"))?.let { srcDir ->
				FileTemplateUtil.createFromTemplate(template, "main.jl", NewJuliaFile.createProperties(this, name), srcDir)
			}
			generateCMakeFile(baseDir)
		}
	}
}
