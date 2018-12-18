@file:Suppress("DEPRECATION")

package org.ice1000.julia.lang.execution

import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.PathMacroManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.JDOMExternalizer
import com.intellij.openapi.util.Ref
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.PsiElement
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.module.*
import org.jdom.Element
import org.jetbrains.debugger.DebuggableRunConfiguration
import java.net.InetSocketAddress

class JuliaRunConfiguration(project: Project, factory: ConfigurationFactory) :
	LocatableConfigurationBase<JuliaCommandLineState>(project, factory, JuliaBundle.message("julia.name")), DebuggableRunConfiguration {
	var workingDir = ""
	var targetFile = ""
	var additionalOptions = ""
	var programArgs = ""
	var juliaExecutable = ""
	var jitCompiler = "yes"
	var deprecationWarning = "yes"
	var codeCoverage = "none"
	var trackAllocation = "none"
	var systemImage = ""
	var systemImageOption = false
	var inlineOption = false
	var checkBoundsOption = false
	var colorOption = true
	var unsafeFloatOption = false
	var handleSignalOption = false
	var startupFileOption = false
	var historyOption = false
	var launchReplOption = false
	var optimizationLevel = 3
		set(value) {
			field = if (value > 3) 3 else if (value < 0) 0 else value
		}

	override fun createDebugProcess(socketAddress: InetSocketAddress, session: XDebugSession, executionResult: ExecutionResult?, environment: ExecutionEnvironment): XDebugProcess =
		JuliaDebugProcess(socketAddress,session,executionResult,environment)
	override fun getConfigurationEditor() = JuliaRunConfigurationEditorImpl(this, project)
	override fun getState(executor: Executor, env: ExecutionEnvironment) = JuliaCommandLineState(this, env)
	override fun writeExternal(element: Element) {
		super<LocatableConfigurationBase>.writeExternal(element)
		JDOMExternalizer.write(element, "workingDir", workingDir)
		JDOMExternalizer.write(element, "targetFile", targetFile)
		JDOMExternalizer.write(element, "additionalOptions", additionalOptions)
		JDOMExternalizer.write(element, "programArgs", programArgs)
		JDOMExternalizer.write(element, "juliaExecutive", juliaExecutable)
		JDOMExternalizer.write(element, "jitCompiler", jitCompiler)
		JDOMExternalizer.write(element, "deprecationWarning", deprecationWarning)
		JDOMExternalizer.write(element, "codeCoverage", codeCoverage)
		JDOMExternalizer.write(element, "trackAllocation", trackAllocation)
		JDOMExternalizer.write(element, "systemImage", systemImage)
		JDOMExternalizer.write(element, "systemImageOption", systemImageOption)
		JDOMExternalizer.write(element, "inlineOption", inlineOption)
		JDOMExternalizer.write(element, "checkBoundsOption", checkBoundsOption)
		JDOMExternalizer.write(element, "colorOption", colorOption)
		JDOMExternalizer.write(element, "mathModeOption", unsafeFloatOption)
		JDOMExternalizer.write(element, "handleSignalOption", handleSignalOption)
		JDOMExternalizer.write(element, "startupFileOption", startupFileOption)
		JDOMExternalizer.write(element, "historyOption", historyOption)
		JDOMExternalizer.write(element, "launchReplOption", launchReplOption)
		JDOMExternalizer.write(element, "optimizationLevel", optimizationLevel)
		PathMacroManager.getInstance(project).collapsePathsRecursively(element)
	}

	override fun readExternal(element: Element) {
		super<LocatableConfigurationBase>.readExternal(element)
		PathMacroManager.getInstance(project).expandPaths(element)
		JDOMExternalizer.readString(element, "workingDir")?.let { workingDir = it }
		JDOMExternalizer.readString(element, "targetFile")?.let { targetFile = it }
		JDOMExternalizer.readString(element, "additionalOptions")?.let { additionalOptions = it }
		JDOMExternalizer.readString(element, "programArgs")?.let { programArgs = it }
		JDOMExternalizer.readString(element, "juliaExecutive")?.let { juliaExecutable = it }
		JDOMExternalizer.readString(element, "jitCompiler")?.let { jitCompiler = it }
		JDOMExternalizer.readString(element, "deprecationWarning")?.let { deprecationWarning = it }
		JDOMExternalizer.readString(element, "codeCoverage")?.let { codeCoverage = it }
		JDOMExternalizer.readString(element, "trackAllocation")?.let { trackAllocation = it }
		JDOMExternalizer.readString(element, "systemImage")?.let { systemImage = it }
		JDOMExternalizer.readBoolean(element, "systemImageOption").let { systemImageOption = it }
		JDOMExternalizer.readBoolean(element, "inlineOption").let { inlineOption = it }
		JDOMExternalizer.readBoolean(element, "checkBoundsOption").let { checkBoundsOption = it }
		JDOMExternalizer.readBoolean(element, "colorOption").let { colorOption = it }
		JDOMExternalizer.readBoolean(element, "mathModeOption").let { unsafeFloatOption = it }
		JDOMExternalizer.readBoolean(element, "handleSignalOption").let { handleSignalOption = it }
		JDOMExternalizer.readBoolean(element, "startupFileOption").let { startupFileOption = it }
		JDOMExternalizer.readBoolean(element, "historyOption").let { historyOption = it }
		JDOMExternalizer.readBoolean(element, "launchReplOption").let { launchReplOption = it }
		JDOMExternalizer.readInteger(element, "optimizationLevel", 3).let { optimizationLevel = it }
	}
}

class JuliaRunConfigurationFactory(type: JuliaRunConfigurationType) : ConfigurationFactory(type) {
	override fun createTemplateConfiguration(project: Project) = JuliaRunConfiguration(project, this)
}

object JuliaRunConfigurationType : ConfigurationType {
	private val factories = arrayOf(JuliaRunConfigurationFactory(this))
	override fun getIcon() = JuliaIcons.JULIA_BIG_ICON
	override fun getConfigurationTypeDescription() = JuliaBundle.message("julia.run-config.description")
	override fun getId() = JULIA_RUN_CONFIG_ID
	override fun getDisplayName() = JuliaBundle.message("julia.name")
	override fun getConfigurationFactories() = factories
}

class JuliaRunConfigurationProducer : RunConfigurationProducer<JuliaRunConfiguration>(JuliaRunConfigurationType) {
	override fun isConfigurationFromContext(
		configuration: JuliaRunConfiguration, context: ConfigurationContext) =
		configuration.targetFile == context.dataContext.getData(CommonDataKeys.VIRTUAL_FILE)?.path

	override fun setupConfigurationFromContext(
		configuration: JuliaRunConfiguration, context: ConfigurationContext, ref: Ref<PsiElement>?): Boolean {
		val file = context.dataContext.getData(CommonDataKeys.VIRTUAL_FILE)
		if (file?.fileType != JuliaFileType) return false
		configuration.targetFile = file.path
		configuration.workingDir = context.project.basePath.orEmpty()
		configuration.name = FileUtilRt
			.getNameWithoutExtension(configuration.targetFile)
			.takeLastWhile { it != '/' && it != '\\' }
		val existPath = context.project
			.juliaSettings
			.settings
			.exePath
		if (validateJuliaExe(existPath)) configuration.juliaExecutable = existPath
		else {
			val exePath = juliaGlobalSettings.knownJuliaExes.firstOrNull() ?: return true
			if (validateJuliaExe(exePath)) configuration.juliaExecutable = exePath
		}
		return true
	}
}

