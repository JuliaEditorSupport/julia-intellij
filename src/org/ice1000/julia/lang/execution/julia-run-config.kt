package org.ice1000.julia.lang.execution

import com.intellij.execution.Executor
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.components.PathMacroManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.util.JDOMExternalizer
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.module.JuliaSdkType
import org.ice1000.julia.lang.module.projectSdk
import org.jdom.Element
import java.nio.file.Paths

class JuliaRunConfiguration(project: Project, factory: ConfigurationFactory) :
	ModuleBasedConfiguration<RunConfigurationModule>(
		JuliaBundle.message("julia.name"),
		RunConfigurationModule(project),
		factory) {
	private val juliaSdks get() = ProjectJdkTable.getInstance().getSdksOfType(JuliaSdkType.instance)
	private var sdkName = ""
	var sdkUsed = project.projectSdk
		set(value) {
			value?.let {
				sdkName = it.name
				field = it
				juliaExecutable = Paths.get(it.homePath, "bin", "julia").toAbsolutePath().toString()
			}
		}
	var workingDir = ""
	var targetFile = ""
	var programArgs = ""
	var juliaExecutable = sdkUsed?.run { Paths.get(homePath, "bin", "cs").toAbsolutePath().toString() }.orEmpty()
	override fun getConfigurationEditor() = JuliaRunConfigurationEditor(this)
	override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? = null // TODO
	override fun getValidModules() = allModules.filter { it.project.projectSdk?.sdkType is JuliaSdkType }
	override fun writeExternal(element: Element) {
		PathMacroManager.getInstance(project).expandPaths(element)
		super.writeExternal(element)
		JDOMExternalizer.write(element, "workingDir", workingDir)
		JDOMExternalizer.write(element, "targetFile", targetFile)
		JDOMExternalizer.write(element, "programArgs", programArgs)
		JDOMExternalizer.write(element, "juliaExecutive", juliaExecutable)
		JDOMExternalizer.write(element, "sdkName", sdkName)
	}

	override fun readExternal(element: Element) {
		super.readExternal(element)
		JDOMExternalizer.readString(element, "workingDir")?.let { workingDir = it }
		JDOMExternalizer.readString(element, "targetFile")?.let { targetFile = it }
		JDOMExternalizer.readString(element, "programArgs")?.let { programArgs = it }
		JDOMExternalizer.readString(element, "juliaExecutive")?.let { juliaExecutable = it }
		JDOMExternalizer.readString(element, "sdkName")?.let { name ->
			sdkUsed = juliaSdks.firstOrNull { it.name == name } ?: return@let
		}
		PathMacroManager.getInstance(project).collapsePathsRecursively(element)
	}
}

class JuliaRunConfigurationFactory(type: JuliaRunConfigurationType) : ConfigurationFactory(type) {
	override fun createTemplateConfiguration(project: Project) = JuliaRunConfiguration(project, this)
}

object JuliaRunConfigurationType : ConfigurationType {
	override fun getIcon() = JULIA_BIG_ICON
	override fun getConfigurationTypeDescription() = JuliaBundle.message("julia.run-config.description")
	override fun getId() = JULIA_RUN_CONFIG_ID
	override fun getDisplayName() = JuliaBundle.message("julia.name")
	override fun getConfigurationFactories() = arrayOf(JuliaRunConfigurationFactory(this))
}

class JuliaRunConfigurationProducer : RunConfigurationProducer<JuliaRunConfiguration>(JuliaRunConfigurationType) {
	override fun isConfigurationFromContext(
		configuration: JuliaRunConfiguration, context: ConfigurationContext) =
		configuration.targetFile == context
			.location
			?.virtualFile
			?.path

	override fun setupConfigurationFromContext(
		configuration: JuliaRunConfiguration, context: ConfigurationContext, ref: Ref<PsiElement>?): Boolean {
		if (context.psiLocation?.containingFile !is JuliaFile) return false
		configuration.targetFile = context.location?.virtualFile?.path.orEmpty()
		configuration.workingDir = context.project.basePath.orEmpty()
		return true
	}
}

