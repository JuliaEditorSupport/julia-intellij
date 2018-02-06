package org.ice1000.julia.lang.action

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.actions.AttributesDefaults
import com.intellij.ide.fileTemplates.ui.CreateFromTemplateDialog
import com.intellij.ide.util.projectWizard.AbstractNewProjectStep
import com.intellij.ide.util.projectWizard.ProjectSettingsStepBase
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import icons.JuliaIcons
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.editing.JuliaNameValidator
import org.ice1000.julia.lang.module.*

/**
 * Create a Julia file from template
 *
 * @author LimbolRain, ice1000
 */
class NewJuliaFile : CreateFileFromTemplateAction(
	JuliaBundle.message("julia.actions.new-file.title"),
	JuliaBundle.message("julia.actions.new-file.description"),
	JuliaIcons.JULIA_ICON), DumbAware {
	private fun createFromTemplate(dir: PsiDirectory, className: String, template: FileTemplate): PsiFile {
		val project = dir.project
		val properties = FileTemplateManager.getInstance(project).defaultProperties
		val settings = project.juliaSettings.settings
		properties += "JULIA_VERSION" to settings.version
		properties += "NAME" to className
		return CreateFromTemplateDialog(project, dir, template, AttributesDefaults(className).withFixedName(true), properties)
			.create()
			.containingFile
	}

	override fun getActionName(directory: PsiDirectory?, s: String?, s2: String?) =
		JuliaBundle.message("julia.actions.new-file.title")

	override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
		builder
			.setTitle(JuliaBundle.message("julia.actions.new-file.title"))
			.setValidator(JuliaNameValidator)
			.addKind("File", JuliaIcons.JULIA_ICON, "Julia File")
			.addKind("Module", JuliaIcons.JULIA_MODULE_ICON, "Julia Module")
			.addKind("Type", JuliaIcons.JULIA_TYPE_ICON, "Julia Type")
	}

	override fun createFileFromTemplate(name: String, template: FileTemplate, dir: PsiDirectory) = try {
		createFromTemplate(dir, FileUtilRt.getNameWithoutExtension(name), template)
	} catch (e: Exception) {
		LOG.error("Error while creating new file", e)
		null
	}
}

/**
 * create project for CLion
 * @author zxj5470
 * @date 2018/1/30
 */
class NewJuliaProject : ProjectSettingsStepBase<JuliaSettings>(
	JuliaProjectGenerator(),
	AbstractNewProjectStep.AbstractCallback<JuliaSettings>())
