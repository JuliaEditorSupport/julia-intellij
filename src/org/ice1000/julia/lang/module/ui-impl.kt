package org.ice1000.julia.lang.module

import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.ide.util.PropertiesComponent
import com.intellij.ide.util.projectWizard.SettingsStep
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.platform.ProjectGeneratorPeer
import com.intellij.ui.DocumentAdapter
import org.ice1000.julia.lang.JULIA_SDK_HOME_PATH_ID
import org.ice1000.julia.lang.JuliaBundle
import java.text.NumberFormat
import javax.swing.event.DocumentEvent
import javax.swing.text.DefaultFormatterFactory
import javax.swing.text.NumberFormatter

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
		installAutoFormatButton.addActionListener(installAutoFormat(project, settings))
	}

	override fun getDisplayName() = JuliaBundle.message("julia.name")
	override fun createComponent() = mainPanel
	override fun isModified() = settings.importPath != importPathField.text ||
		settings.basePath != basePathField.text ||
		settings.exePath != juliaExeField.text ||
		unicodeInputCheckBox.isSelected != settings.unicodeEnabled ||
		settings.tryEvaluateTextLimit != textLimitField.value ||
		settings.tryEvaluateTimeLimit != timeLimitField.value

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
	}
}
