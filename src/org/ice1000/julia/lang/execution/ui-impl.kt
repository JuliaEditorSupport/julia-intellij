package org.ice1000.julia.lang.execution

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.JuliaFileType
import org.ice1000.julia.lang.module.*
import org.jetbrains.annotations.Contract
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class JuliaRunConfigurationEditorImpl(configuration: JuliaRunConfiguration, project: Project) :
	JuliaRunConfigurationEditor() {
	init {
		workingDirField.addBrowseFolderListener(JuliaBundle.message("julia.messages.run.select-working-dir"),
			JuliaBundle.message("julia.messages.run.select-working-dir.description"),
			project,
			FileChooserDescriptorFactory.createSingleFolderDescriptor())
		targetFileField.addBrowseFolderListener(JuliaBundle.message("julia.messages.run.select-julia-file"),
			JuliaBundle.message("julia.messages.run.select-julia-file.description"),
			project,
			FileChooserDescriptorFactory.createSingleFileDescriptor(JuliaFileType))
		val def = "2 (" + JuliaBundle.message("julia.run-config.opt-level.default") + ")"
		val rec = "3 (" + JuliaBundle.message("julia.run-config.opt-level.recommended") + ")"
		systemImageCheckBox.addChangeListener { systemImageField.isEnabled = systemImageCheckBox.isSelected }
		Arrays.asList("0", "1", def, rec).forEach(optimizationLevelComboBox::addItem)
		Arrays.asList("yes", "no", "all", "min").forEach(jitCompilerOptions::addItem)
		Arrays.asList("yes", "no", "error").forEach(depWarnOptions::addItem)
		listOf("none", "user", "all").let { noneUserAll ->
			noneUserAll.forEach(codeCovOptions::addItem)
			noneUserAll.forEach(trackAllocOptions::addItem)
		}
		initExeComboBox(juliaExeField, project)
		resetEditorFrom(configuration)
	}

	override fun resetEditorFrom(configuration: JuliaRunConfiguration) {
		juliaExeField.comboBox.selectedItem = configuration.juliaExecutable
		targetFileField.text = configuration.targetFile
		workingDirField.text = configuration.workingDir
		inlineCheckBox.isSelected = configuration.inlineOption
		checkBoundsCheckBox.isSelected = configuration.checkBoundsOption
		colorCheckBox.isSelected = configuration.colorOption
		historyCheckBox.isSelected = configuration.historyOption
		handleSignalCheckBox.isSelected = configuration.handleSignalOption
		unsafeFloatCheckBox.isSelected = configuration.unsafeFloatOption
		startupFileCheckBox.isSelected = configuration.startupFileOption
		launchReplCheckBox.isSelected = configuration.launchReplOption
		systemImageCheckBox.isSelected = configuration.systemImageOption
		systemImageField.isEnabled = systemImageCheckBox.isSelected
		systemImageField.text = configuration.systemImage
		additionalOptionsField.text = configuration.additionalOptions
		programArgumentsField.text = configuration.programArgs
		optimizationLevelComboBox.selectedIndex = configuration.optimizationLevel
		jitCompilerOptions.selectedItem = configuration.jitCompiler
		depWarnOptions.selectedItem = configuration.deprecationWarning
		codeCovOptions.selectedItem = configuration.codeCoverage
		trackAllocOptions.selectedItem = configuration.trackAllocation
	}

	@Throws(ConfigurationException::class)
	override fun applyEditorTo(configuration: JuliaRunConfiguration) {
		val juliaExecutable = juliaExeField.comboBox.selectedItem as? String
		if (juliaExecutable != null && Files.isExecutable(Paths.get(juliaExecutable))) {
			configuration.juliaExecutable = juliaExecutable
			juliaGlobalSettings.knownJuliaExes += juliaExecutable
		} else reportInvalidPath(juliaExecutable.toString())
		val targetFile = targetFileField.text
		if (Files.isReadable(Paths.get(targetFile))) configuration.targetFile = targetFile
		else reportInvalidPath(targetFile)
		val workingDirectory = workingDirField.text
		if (Files.isDirectory(Paths.get(workingDirectory))) configuration.workingDir = workingDirectory
		else reportInvalidPath(workingDirectory)
		configuration.inlineOption = inlineCheckBox.isSelected
		configuration.checkBoundsOption = checkBoundsCheckBox.isSelected
		configuration.colorOption = colorCheckBox.isSelected
		configuration.historyOption = historyCheckBox.isSelected
		configuration.handleSignalOption = handleSignalCheckBox.isSelected
		configuration.unsafeFloatOption = unsafeFloatCheckBox.isSelected
		configuration.startupFileOption = startupFileCheckBox.isSelected
		configuration.launchReplOption = launchReplCheckBox.isSelected
		configuration.systemImageOption = systemImageCheckBox.isSelected
		configuration.systemImage = systemImageField.text
		configuration.additionalOptions = additionalOptionsField.text
		configuration.programArgs = programArgumentsField.text
		configuration.optimizationLevel = optimizationLevelComboBox.selectedIndex
		configuration.jitCompiler = jitCompilerOptions.selectedItem.toString()
		configuration.deprecationWarning = depWarnOptions.selectedItem.toString()
		configuration.codeCoverage = codeCovOptions.selectedItem.toString()
		configuration.trackAllocation = trackAllocOptions.selectedItem.toString()
	}

	override fun createEditor() = mainPanel

	@Contract("_ -> fail")
	@Throws(ConfigurationException::class)
	private fun reportInvalidPath(path: String) {
		throw ConfigurationException(JuliaBundle.message("julia.run-config.invalid-path", path))
	}
}