package org.ice1000.julia.lang.action

import com.intellij.ide.actions.CreateFileAction
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.ide.fileTemplates.actions.AttributesDefaults
import com.intellij.ide.fileTemplates.ui.CreateFromTemplateDialog
import com.intellij.ide.util.projectWizard.AbstractNewProjectStep
import com.intellij.ide.util.projectWizard.ProjectSettingsStepBase
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.*
import com.intellij.util.IncorrectOperationException
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.module.JuliaProjectGenerator
import org.ice1000.julia.lang.module.JuliaSettings
import java.time.LocalDate
import java.util.Properties


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
 * Test Function, it has many problems, such as bug, bug and bug
 * @author LimbolRain
 */
class NewJuliaFileFromTemplate : CreateFileFromTemplateAction(
	JuliaBundle.message("julia.actions.new-file-template.title"),
	JuliaBundle.message("julia.actions.new-file-template.description"),
	JuliaIcons.JULIA_ICON
), DumbAware {
	companion object {
		private fun findOrCreateTarget(dir: PsiDirectory, name: String, directorySeparators: Array<Char>): Pair<String, PsiDirectory> {
			var className = name.removeSuffix(".jl")
			var targetDir = dir

			for (splitChar in directorySeparators) {
				if (splitChar in className) {
					val names = className.trim().split(splitChar)

					for (dirName in names.dropLast(1)) {
						targetDir = targetDir.findSubdirectory(dirName) ?: runWriteAction {
							targetDir.createSubdirectory(dirName)
						}
					}

					className = names.last()
					break
				}
			}
			return Pair(className, targetDir)
		}

		private fun createFromTemplate(dir: PsiDirectory, className: String, template: FileTemplate): PsiFile? {
			val project = dir.project
			val defaultProperties = FileTemplateManager.getInstance(project).defaultProperties

			val properties = Properties(defaultProperties)

			val element = try {
				CreateFromTemplateDialog(project, dir, template,
					AttributesDefaults(className).withFixedName(true),
					properties).create()
			}
			catch (e: IncorrectOperationException) {
				throw e
			}
			catch (e: Exception) {
				LOG.error(e)
				return null
			}

			return element?.containingFile
		}

		fun createFileFromTemplate(name: String, template: FileTemplate, dir: PsiDirectory): PsiFile? {
			val directorySeparators = if (template.name == "Julia File") arrayOf('/', '\\') else arrayOf('/', '\\', '.')
			val (className, targetDir) = findOrCreateTarget(dir, name, directorySeparators)

			val service = DumbService.getInstance(dir.project)
			service.isAlternativeResolveEnabled = true
			try {
				return createFromTemplate(targetDir, className, template)
			}
			finally {
				service.isAlternativeResolveEnabled = false
			}
		}
	}

	override fun getActionName(directory: PsiDirectory?, s: String?, s2: String?): String = JuliaBundle.message("julia.actions.new-file.title")
	override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
		builder
			.addKind("File", JuliaIcons.JULIA_ICON, "Julia File")
			.addKind("Module", JuliaIcons.JULIA_MODULE_ICON, "Julia Module")
			.addKind("Type", JuliaIcons.JULIA_TYPE_ICON, "Julia Type")
	}

	override fun createFileFromTemplate(name: String, template: FileTemplate, dir: PsiDirectory): PsiFile? =
		Companion.createFileFromTemplate(name, template, dir)


	override fun hashCode(): Int = 0
	override fun equals(other: Any?): Boolean = other is NewJuliaFileFromTemplate
}

/**
 * create project for CLion
 * @author zxj5470
 * @date 2018/1/30
 */
class NewJuliaProject : ProjectSettingsStepBase<JuliaSettings>(
	JuliaProjectGenerator(),
	AbstractNewProjectStep.AbstractCallback<JuliaSettings>())
