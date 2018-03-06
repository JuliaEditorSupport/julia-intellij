package org.ice1000.julia.lang.module.ui

import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.ide.util.projectWizard.SettingsStep
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.progress.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.*
import com.intellij.platform.ProjectGeneratorPeer
import icons.JuliaIcons
import org.ice1000.julia.lang.JULIA_TABLE_HEADER_COLUMN
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.module.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.text.NumberFormat
import java.util.stream.Collectors
import javax.swing.JPanel
import javax.swing.table.DefaultTableModel
import javax.swing.text.DefaultFormatterFactory
import javax.swing.text.NumberFormatter

class JuliaSetupSdkWizardStepImpl(private val builder: JuliaModuleBuilder) : JuliaSetupSdkWizardStep() {
	init {
		usefulText.isVisible = false
		juliaWebsite.setListener({ _, _ -> BrowserLauncher.instance.open(juliaWebsite.text) }, null)
		initExeComboBox(juliaExeField) {
			val exePath = juliaExeField.comboBox.selectedItem as? String ?: return@initExeComboBox
			if (validateJuliaExe(exePath)) importPathField.text = importPathOf(exePath, 1500L)
		}
		juliaGlobalSettings.knownJuliaExes.forEach(juliaExeField.comboBox::addItem)
		importPathField.addBrowseFolderListener(TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor()))
	}

	@Throws(ConfigurationException::class)
	override fun validate(): Boolean {
		val selected = juliaExeField.comboBox.selectedItem as? String
		if (selected == null || !validateJuliaExe(selected)) {
			usefulText.isVisible = true
			throw ConfigurationException(JuliaBundle.message("julia.modules.invalid"))
		}
		usefulText.isVisible = false
		juliaGlobalSettings.knownJuliaExes += selected
		return super.validate()
	}

	override fun getComponent() = mainPanel
	override fun updateDataModel() {
		val settings = JuliaSettings()
		settings.exePath = juliaExeField.comboBox.selectedItem.toString()
		settings.initWithExe()
		builder.settings = settings
	}
}

class JuliaProjectGeneratorPeerImpl : JuliaProjectGeneratorPeer() {
	private val settings = JuliaSettings()
	private val listeners = emptyList<ProjectGeneratorPeer.SettingsListener>().toMutableList()

	init {
		setupLaterRadioButton.addChangeListener {
			juliaExeField.isEnabled = false
			selectJuliaExecutableRadioButton.isSelected = !setupLaterRadioButton.isSelected
		}
		selectJuliaExecutableRadioButton.addChangeListener {
			juliaExeField.isEnabled = true
			setupLaterRadioButton.isSelected = !selectJuliaExecutableRadioButton.isSelected
		}
		usefulText.isVisible = false
		juliaWebsite.setListener({ _, _ -> BrowserLauncher.instance.open(juliaWebsite.text) }, null)
		initExeComboBox(juliaExeField)
		selectJuliaExecutableRadioButton.isSelected = true
	}

	override fun getSettings() = settings.apply { initWithExe() }
	override fun buildUI(settingsStep: SettingsStep) = settingsStep.addExpertPanel(component)
	override fun isBackgroundJobRunning() = false
	override fun addSettingsListener(listener: ProjectGeneratorPeer.SettingsListener) {
		listeners += listener
	}

	/** Deprecated in 2017.3 But We must override it. */
	@Deprecated("", ReplaceWith("addSettingsListener"), level = DeprecationLevel.ERROR)
	override fun addSettingsStateListener(@Suppress("DEPRECATION") listener: com.intellij.platform.WebProjectGenerator.SettingsStateListener) = Unit

	override fun getComponent() = mainPanel
	override fun validate(): ValidationInfo? {
		if (setupLaterRadioButton.isSelected) return null
		val selected = juliaExeField.comboBox.selectedItem as? String
		return if (selected != null && validateJuliaExe(selected)) {
			listeners.forEach { it.stateChanged(true) }
			settings.exePath = selected
			juliaGlobalSettings.knownJuliaExes += selected
			null
		} else {
			usefulText.isVisible = true
			ValidationInfo(JuliaBundle.message("julia.modules.invalid"))
		}
	}
}

/**
 * Settings(Preference) | Language & Frameworks | Julia
 */
class JuliaProjectConfigurableImpl(project: Project) : JuliaProjectConfigurable() {
	private var settings = project.juliaSettings.settings

	init {
		version.text = settings.version
		val format = NumberFormat.getIntegerInstance()
		format.isGroupingUsed = false
		val factory = DefaultFormatterFactory(NumberFormatter(format))
		timeLimitField.formatterFactory = factory
		timeLimitField.value = settings.tryEvaluateTimeLimit
		textLimitField.formatterFactory = factory
		textLimitField.value = settings.tryEvaluateTextLimit.toLong()
		juliaWebsite.setListener({ _, _ -> BrowserLauncher.instance.open(juliaWebsite.text) }, null)
		importPathField.text = settings.importPath
		importPathField.addBrowseFolderListener(TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor(), project))
		basePathField.text = settings.basePath
		basePathField.addBrowseFolderListener(TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor(), project))
		initExeComboBox(juliaExeField) {
			val exePath = juliaExeField.comboBox.selectedItem as? String ?: return@initExeComboBox
			importPathField.text = importPathOf(exePath, 800L)
			version.text = versionOf(exePath, 800L)
			tryGetBase(exePath)?.let { basePathField.text = it }
		}
		val currentExePath = settings.exePath
		if (validateJuliaExe(currentExePath))
			juliaExeField.comboBox.selectedItem = currentExePath
		unicodeInputCheckBox.isSelected = settings.unicodeEnabled
		showEvalHintCheckBox.isSelected = settings.showEvalHint
		if (Files.exists(Paths.get(settings.importPath, "DocumentFormat"))) {
			installAutoFormatButton.isEnabled = false
			installAutoFormatButton.text = JuliaBundle.message("julia.messages.doc-format.already")
		} else installAutoFormatButton.addActionListener(installDocumentFormat(project, settings))
	}

	override fun getDisplayName() = JuliaBundle.message("julia.name")
	override fun createComponent() = mainPanel
	override fun isModified() = settings.importPath != importPathField.text ||
		settings.basePath != basePathField.text ||
		settings.exePath != juliaExeField.comboBox.selectedItem ||
		unicodeInputCheckBox.isSelected != settings.unicodeEnabled ||
		showEvalHintCheckBox.isSelected != settings.showEvalHint ||
		settings.tryEvaluateTextLimit != (textLimitField.value as Number).toInt() ||
		settings.tryEvaluateTimeLimit != (timeLimitField.value as Number).toLong()

	@Throws(ConfigurationException::class)
	override fun apply() {
		settings.tryEvaluateTextLimit = (textLimitField.value as? Number
			?: throw ConfigurationException(JuliaBundle.message("julia.modules.try-eval.invalid"))).toInt()
		settings.tryEvaluateTimeLimit = (timeLimitField.value as? Number
			?: throw ConfigurationException(JuliaBundle.message("julia.modules.try-eval.invalid"))).toLong()
		val exePath = juliaExeField.comboBox.selectedItem as? String
		if (exePath == null || !validateJuliaExe(exePath))
			throw ConfigurationException(JuliaBundle.message("julia.modules.invalid"))
		juliaGlobalSettings.knownJuliaExes += exePath
		settings.exePath = exePath
		settings.version = version.text
		settings.basePath = basePathField.text
		settings.importPath = importPathField.text
		settings.unicodeEnabled = unicodeInputCheckBox.isSelected
		settings.showEvalHint = showEvalHintCheckBox.isSelected
	}
}

/**
 * Settings(Preference) | Language & Frameworks | Julia | Package Manager
 */
class JuliaPackageManagerImpl(private val project: Project) : JuliaPackageManager() {
	private val settings = project.juliaSettings.settings

	private class JuliaPackageTableModel : DefaultTableModel {
		constructor(data: Array<Array<String>>, columnNames: Array<String>) : super(data, columnNames)
		constructor(row: Int, column: Int) : super(row, column)

		override fun isCellEditable(row: Int, column: Int) = false
	}

	init {
		packagesList.model = JuliaPackageTableModel(emptyArray(), JULIA_TABLE_HEADER_COLUMN)

		buttonAdd.addActionListener {
			Messages.showInputDialog(
				project,
				"Package name",
				"Add Package",
				JuliaIcons.JOJO_ICON,
				"",
				null
			)?.let {
				/**
				 * TODO install, see [installDocumentFormat]
				 */
			}
		}
		buttonRemove.addActionListener {
			Messages.showDialog(
				"Nothing to remove",
				"Remove Package",
				arrayOf(JuliaBundle.message("julia.yes")),
				0,
				JuliaIcons.JOJO_ICON)
		}
		buttonRefresh.addActionListener {
			loadPackages(false)
		}

		initExeComboBox(alternativeExecutables)
		alternativeExecutables.comboBox.selectedItem = settings.exePath
		packagesList.setShowGrid(false)
	}

	/**
	 * `Fill in my data parameters` INITIALIZATION
	 * @param default Value{true is called by dialog initialization, and false is called by refresh button.}
	 */
	private fun loadPackages(default: Boolean = true) {
		ProgressManager.getInstance().run(object :
			Task.Backgroundable(
				project,
				JuliaBundle.message("julia.messages.package.names.loading"),
				true) {
			override fun run(indicator: ProgressIndicator) {
				indicator.text = JuliaBundle.message("julia.messages.package.names.loading")
				val namesList = packageNamesList(settings.importPath).collect(Collectors.toList())
				val tempData = namesList.map { arrayOf(it) }.toTypedArray()
				val tempDataModel = JuliaPackageTableModel(tempData, JULIA_TABLE_HEADER_COLUMN)
				if (default) {
					packagesList.model = tempDataModel
					packageInfos.clear()
					namesList.mapTo(packageInfos) { InfoData(it, "") }
				}

				val sizeToDouble = namesList.size.toDouble()
				val versionList = namesList.mapIndexed { index, it ->
					val process = Runtime.getRuntime().exec(
						arrayOf(gitPath, "describe", "--abbrev=0", "--tags"),
						emptyArray(),
						"${settings.importPath}${File.separator}$it".let(::File))
					indicator.fraction = index / sizeToDouble
					val second = process.inputStream.reader().use { it.readText().removePrefix("v").trim() }
					tempDataModel.setValueAt(second, index, 1)
					it to second
				}.toList()
				packagesList.model = tempDataModel
				packageInfos.clear()
				versionList.mapTo(packageInfos) { InfoData(it.first, it.second) }
			}
		})

	}

	override fun getDisplayName() = JuliaBundle.message("julia.pkg-manager.title")
	override fun createComponent(): JPanel {
		if (packageInfos.isEmpty()) {
			if (validateJuliaExe(settings.exePath)) loadPackages()
		} else {
			val data = packageInfos.map {
				arrayOf(it.name, it.version, it.latestVersion)
			}.toTypedArray()
			val dataModel = JuliaPackageTableModel(data, JULIA_TABLE_HEADER_COLUMN)
			packagesList.model = dataModel
		}
		return mainPanel
	}

	override fun isModified() = false
	override fun apply() {
		val selected = alternativeExecutables.comboBox.selectedItem.toString()
		juliaGlobalSettings.knownJuliaExes += selected
// TODO packageInfo needs to be cached
	}
}