package org.ice1000.julia.lang.module.ui

import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.ide.util.PropertiesComponent
import com.intellij.ide.util.projectWizard.SettingsStep
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.progress.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.platform.ProjectGeneratorPeer
import com.intellij.ui.DocumentAdapter
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.module.*
import java.nio.file.Files
import java.nio.file.Paths
import java.text.NumberFormat
import javax.swing.event.DocumentEvent
import javax.swing.table.DefaultTableModel
import javax.swing.text.DefaultFormatterFactory
import javax.swing.text.NumberFormatter
import javax.swing.SortOrder
import javax.swing.RowSorter
import java.util.ArrayList
import javax.swing.table.TableRowSorter


class JuliaSetupSdkWizardStepImpl(private val builder: JuliaModuleBuilder) : JuliaSetupSdkWizardStep() {
	init {
		usefulText.isVisible = false
		juliaWebsite.setListener({ _, _ -> BrowserLauncher.instance.open(juliaWebsite.text) }, null)
		juliaExeField.addBrowseFolderListener(TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor()))
		juliaExeField.textField.document.addDocumentListener(object : DocumentAdapter() {
			override fun textChanged(e: DocumentEvent) {
				importPathField.text = importPathOf(juliaExeField.text, 500L)
			}
		})
		if (validateJuliaExe(defaultExePath)) juliaExeField.text = defaultExePath
		importPathField.addBrowseFolderListener(TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor()))
		importPathField.text = importPathOf(defaultExePath, 800L)
	}

	@Throws(ConfigurationException::class)
	override fun validate(): Boolean {
		if (!validateJuliaExe(juliaExeField.text)) {
			usefulText.isVisible = true
			throw ConfigurationException(JuliaBundle.message("julia.modules.invalid"))
		}
		usefulText.isVisible = false
		PropertiesComponent.getInstance().setValue(JULIA_SDK_HOME_PATH_ID, juliaExeField.text)
		return super.validate()
	}

	override fun getComponent() = mainPanel
	override fun updateDataModel() {
		val settings = JuliaSettings()
		settings.exePath = juliaExeField.text
		settings.initWithExe()
		builder.settings = settings
	}
}

class JuliaProjectGeneratorPeerImpl(private val settings: JuliaSettings) : JuliaProjectGeneratorPeer() {
	init {
		setupLaterRadioButton.addChangeListener {
			juliaExeField.isEnabled = false
			juliaExeField.text = defaultExePath
			selectJuliaExecutableRadioButton.isSelected = !setupLaterRadioButton.isSelected
		}
		selectJuliaExecutableRadioButton.addChangeListener {
			juliaExeField.isEnabled = true
			setupLaterRadioButton.isSelected = !selectJuliaExecutableRadioButton.isSelected
		}
		usefulText.isVisible = false
		juliaWebsite.setListener({ _, _ -> BrowserLauncher.instance.open(juliaWebsite.text) }, null)
		juliaExeField.addBrowseFolderListener(TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor()))
//		default
		if (validateJuliaExe(defaultExePath)) {
			juliaExeField.text = defaultExePath
			settings.exePath = juliaExeField.text
		}
		selectJuliaExecutableRadioButton.isSelected = true
	}

	override fun getSettings() = settings
	override fun buildUI(settingsStep: SettingsStep) = settingsStep.addExpertPanel(component)
	override fun isBackgroundJobRunning() = false
	override fun addSettingsListener(listener: ProjectGeneratorPeer.SettingsListener) = Unit
	/** Deprecated in 2017.3 But We must override it. */
	@Deprecated("", ReplaceWith("addSettingsListener"))
	override fun addSettingsStateListener(@Suppress("DEPRECATION") listener: com.intellij.platform.WebProjectGenerator.SettingsStateListener) = Unit

	override fun getComponent() = mainPanel
	override fun validate(): ValidationInfo? {
		if (setupLaterRadioButton.isSelected) return null
		settings.exePath = juliaExeField.text
		settings.initWithExe()
		val validate = validateJulia(settings)
		if (validate) PropertiesComponent.getInstance().setValue(JULIA_SDK_HOME_PATH_ID, juliaExeField.text)
		else usefulText.isVisible = true
		return if (validate) null else ValidationInfo(JuliaBundle.message("julia.modules.invalid"))
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
		juliaExeField.addBrowseFolderListener(TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor(), project))
		juliaExeField.textField.document.addDocumentListener(object : DocumentAdapter() {
			override fun textChanged(e: DocumentEvent) {
				val exePath = juliaExeField.text
				importPathField.text = importPathOf(exePath, 800L)
				version.text = versionOf(exePath, 800L)
				tryGetBase(exePath)?.let { basePathField.text = it }
			}
		})
		if (settings.exePath.isNotBlank())
			juliaExeField.text = settings.exePath
		else
			juliaExeField.text = defaultExePath
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
		settings.exePath != juliaExeField.text ||
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
		if (!validateJuliaExe(juliaExeField.text)) throw ConfigurationException(JuliaBundle.message("julia.modules.invalid"))
		PropertiesComponent.getInstance().setValue(JULIA_SDK_HOME_PATH_ID, juliaExeField.text)
		settings.exePath = juliaExeField.text
		settings.version = version.text
		settings.basePath = basePathField.text
		settings.importPath = importPathField.text
		settings.unicodeEnabled = unicodeInputCheckBox.isSelected
		settings.showEvalHint = showEvalHintCheckBox.isSelected
	}
}

/**
 * TODO PackageManager
 * Settings(Preference) | Language & Frameworks | Julia | Package Manager
 */
class JuliaPackageManagerImpl(private val project: Project) : JuliaPackageManager() {
	class JuliaPackageTableModel(data: Array<Array<String>>, columnNames: Array<String>) : DefaultTableModel(data, columnNames) {
		override fun isCellEditable(row: Int, column: Int) = false
	}

	init {
		packagesList.model = JuliaPackageTableModel(emptyArray(), JULIA_TABLE_HEADER_COLUMN)

		buttonAdd.addActionListener {
			Messages.showDialog("Title", "Nothing to add", arrayOf("♂"), 0, JuliaIcons.JOJO_ICON)
		}
		buttonRemove.addActionListener {
			Messages.showDialog("Title", "Nothing to remove", arrayOf("♂"), 0, JuliaIcons.JOJO_ICON)
		}

		if (packageNameFinished) {//FIXME
			val data = packageInfos.map { arrayOf(it.name, it.version, it.latestVersion) }.toTypedArray()
			val dataModel = JuliaPackageTableModel(data, JULIA_TABLE_HEADER_COLUMN)
			packagesList.model = dataModel
		}
		if (packageVersionFinished) {//FIXME
			val data = packageInfos.map { arrayOf(it.name, it.version, it.latestVersion) }.toTypedArray()
			val dataModel = JuliaPackageTableModel(data, JULIA_TABLE_HEADER_COLUMN)
			packagesList.model = dataModel
		}

		buttonRefresh.addActionListener {
			ProgressManager.getInstance()
				.run(object : Task.Backgroundable(project, JuliaBundle.message("julia.messages.doc-format.installing")+"-0", true) {
					override fun run(indicator: ProgressIndicator) {
						indicator.text = JuliaBundle.message("julia.messages.package.loding")
						val namesList = packageNamesList()
						val data = namesList.map { arrayOf(it) }.toTypedArray()
						val dataModel = JuliaPackageTableModel(data, JULIA_TABLE_HEADER_COLUMN)
						packagesList.model = dataModel
						packageInfos.addAll(namesList.map { InfoData(it, "") })
						packageNameFinished = true
					}
				})
			ProgressManager.getInstance()
				.run(object : Task.Backgroundable(project, JuliaBundle.message("julia.messages.doc-format.installing")+"- 1", true) {
					override fun run(indicator: ProgressIndicator) {
						indicator.text = JuliaBundle.message("julia.messages.package.loding")
						val versionList = versionsList(project.juliaSettings.settings)
						val data = versionList.map { arrayOf(it.first, it.second) }.toTypedArray()
						val dataModel = JuliaPackageTableModel(data, JULIA_TABLE_HEADER_COLUMN)
						packagesList.model = dataModel
						packageVersionFinished = true
					}

					override fun onSuccess() = ApplicationManager.getApplication().invokeLater {
						Messages.showDialog(
							project,
							JuliaBundle.message("julia.messages.package.loding.ok"),
							JuliaBundle.message("julia.messages.doc-format.installed.title"),
							arrayOf(JuliaBundle.message("julia.yes")),
							0,
							JuliaIcons.JOJO_ICON)
					}
				})
		}

		packagesList.autoCreateRowSorter = true
		packagesList.setShowGrid(false)
		val sorter = TableRowSorter<DefaultTableModel>(packagesList.model as DefaultTableModel)
		packagesList.rowSorter = sorter
		val sortKeys = ArrayList<RowSorter.SortKey>()
		sortKeys.add(RowSorter.SortKey(0, SortOrder.ASCENDING))
		sorter.sortKeys = sortKeys
		sorter.sort()

		// FIXME replace with a refresh button
		ProgressManager.getInstance()
			.run(object : Task.Backgroundable(project,
				JuliaBundle.message("julia.messages.doc-format.installing"), true) {
				override fun run(indicator: ProgressIndicator) {
				}
			})
	}

	override fun getDisplayName() = JuliaBundle.message("julia.pkg-manager.title")
	override fun createComponent() = mainPanel
	override fun isModified() = false
	override fun apply() {
	}
}