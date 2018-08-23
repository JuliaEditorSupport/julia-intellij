package org.ice1000.julia.lang.action

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.actions.AttributesDefaults
import com.intellij.ide.fileTemplates.ui.CreateFromTemplateDialog
import com.intellij.ide.util.projectWizard.AbstractNewProjectStep
import com.intellij.ide.util.projectWizard.ProjectSettingsStepBase
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.PsiDirectory
import com.intellij.util.ui.JBUI
import icons.JuliaIcons
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.editing.JuliaNameValidator
import org.ice1000.julia.lang.module.*
import java.util.*
import javax.swing.*

/**
 * Create a Julia file from template
 *
 * @author HoshinoTented, ice1000
 */
class NewJuliaFile : CreateFileFromTemplateAction(
	JuliaBundle.message("julia.actions.new-file.title"),
	JuliaBundle.message("julia.actions.new-file.description"),
	JuliaIcons.JULIA_ICON), DumbAware {
	companion object {
		fun createProperties(project: Project, className: String): Properties {
			val settings = project.juliaSettings.settings
			val properties = FileTemplateManager.getInstance(project).defaultProperties
			properties += "JULIA_VERSION" to settings.version
			properties += "NAME" to className
			return properties
		}
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
			.addKind("Function", JuliaIcons.JULIA_FUNCTION_ICON, "Julia Function")
	}

	override fun createFileFromTemplate(name: String, template: FileTemplate, dir: PsiDirectory) = try {
		val className = FileUtilRt.getNameWithoutExtension(name)
		val project = dir.project
		val properties = createProperties(project, className)
		CreateFromTemplateDialog(project, dir, template, AttributesDefaults(className).withFixedName(true), properties)
			.create()
			.containingFile
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
	AbstractNewProjectStep.AbstractCallback<JuliaSettings>()) {
	override fun actionPerformed(e: AnActionEvent) {
		val panel = createPanel()
		panel.preferredSize = JBUI.size(600, 300)
		JuliaNewProjectDialog(panel).show()
	}
	private class JuliaNewProjectDialog(private val centerPanel: JPanel) : DialogWrapper(true) {
		init {
			title = JuliaBundle.message("julia.actions.new-proj.dialog.title")
			init()
		}
		override fun createCenterPanel() = centerPanel
		override fun createSouthPanel(): JComponent? = null
	}
}
