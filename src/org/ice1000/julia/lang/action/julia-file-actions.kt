package org.ice1000.julia.lang.action

import com.intellij.ide.actions.CreateFileAction
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.ide.util.projectWizard.AbstractNewProjectStep
import com.intellij.ide.util.projectWizard.ProjectSettingsStepBase
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.*
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.module.JuliaProjectGenerator
import org.ice1000.julia.lang.module.JuliaSettings
import java.time.LocalDate


inline fun createFile( name : String , directory : PsiDirectory , template : () -> String = {""} ) : Array<PsiElement> {
	val fixedExtension = when (FileUtilRt.getExtension(name)) {
		JULIA_EXTENSION -> name
		else -> "$name.$JULIA_EXTENSION"
	}
	return arrayOf(
		directory.add(
			PsiFileFactory
				.getInstance(directory.project)
				.createFileFromText(
					fixedExtension, JuliaFileType,
					"""
						|$JULIA_BLOCK_COMMENT_BEGIN
						|# ${FileUtilRt.getNameWithoutExtension(name)}
						|${JuliaBundle.message("julia.actions.new-file.content", System.getProperty("user.name"), LocalDate.now())}
						|$JULIA_BLOCK_COMMENT_END
						|
						|${template()}
					""".trimMargin()
				)
		)
	)
}


class NewJuliaFile : CreateFileAction(
	JuliaBundle.message("julia.actions.new-file.title"),
	JuliaBundle.message("julia.actions.new-file.description"),
	JuliaIcons.JULIA_ICON) {

	override fun getActionName(directory : PsiDirectory?, s : String?) = JuliaBundle.message("julia.actions.new-file.title")
	override fun getDefaultExtension() = JULIA_EXTENSION
	override fun create(name : String, directory : PsiDirectory) : Array<PsiElement> =
		createFile(name, directory)
}


/**
 * Create a Julia file from template
 * Test Function
 * @author LimbolRain
 */
class NewJuliaFileFromTemplate : CreateFileFromTemplateAction(
	JuliaBundle.message("julia.actions.new-file-template.title"),
	JuliaBundle.message("julia.actions.new-file-template.description"),
	JuliaIcons.JULIA_ICON
) {
	override fun getActionName(directory: PsiDirectory?, s: String?, s2: String?): String = JuliaBundle.message("julia.actions.new-file.title")
	override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
		builder
			.addKind("File", JuliaIcons.JULIA_ICON, "Julia File")
			.addKind("Module", JuliaIcons.JULIA_MODULE_ICON, "Julia Module")
			.addKind("Type", JuliaIcons.JULIA_TYPE_ICON, "Julia Type")
	}

	override fun createFile(name: String, templateName: String, dir: PsiDirectory): PsiFile? {
		//val fileName = FileUtilRt.getNameWithoutExtension(name)
		val templateManager = FileTemplateManager.getInstance(dir.project)

		val defaultConfig = templateManager.defaultProperties
		val template : FileTemplate = templateManager.getInternalTemplate(templateName)
		return try {
			// 这里不应该抛出异常吧
			FileTemplateUtil.createFromTemplate(template, name, defaultConfig, dir) as? PsiFile
		} catch (e: Exception) {
			LOG.error(e)
			null
		}
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
