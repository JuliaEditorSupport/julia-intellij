package org.ice1000.julia.lang.module.ui

import com.intellij.icons.AllIcons
import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.ide.highlighter.HighlighterFactory
import com.intellij.ide.util.projectWizard.SettingsStep
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.fileTypes.*
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.progress.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.SystemInfo
import com.intellij.platform.ProjectGeneratorPeer
import com.intellij.psi.stubs.StubIndex
import com.intellij.ui.components.labels.LinkListener
import icons.JuliaIcons
import org.ice1000.julia.lang.JULIA_MARKDOWN_DARCULA_CSS
import org.ice1000.julia.lang.JULIA_TABLE_HEADER_COLUMN
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.action.JuliaAddPkgAction
import org.ice1000.julia.lang.action.JuliaRemovePkgAction
import org.ice1000.julia.lang.module.*
import java.awt.BorderLayout
import java.nio.file.Files
import java.nio.file.Paths
import java.text.NumberFormat
import java.util.stream.Collectors
import javax.swing.JPanel
import javax.swing.table.DefaultTableModel
import javax.swing.text.DefaultFormatterFactory
import javax.swing.text.NumberFormatter

/**
 * IDEA `New -> Project...`
 */
class JuliaSetupSdkWizardStepImpl(private val builder: JuliaModuleBuilder) : JuliaSetupSdkWizardStep() {
	init {
		usefulText.isVisible = false
		juliaWebsite.setListener({ _, _ -> BrowserLauncher.instance.open(juliaWebsite.text) }, null)
		initExeComboBox(juliaExeField) {
			val exePath = juliaExeField.comboBox.selectedItem as? String ?: return@initExeComboBox
			if (validateJuliaExe(exePath)) importPathField.text = importPathOf(exePath, 1500L)
		}
		if (SystemInfo.isMac) usefulText.isVisible = true
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
		val settings = JuliaSettings(replPrompt = "julia> ")
		settings.exePath = juliaExeField.comboBox.selectedItem.toString()
		settings.initWithExe()
		builder.settings = settings
	}
}

/**
 * PyCharm and other IDEs' `New Project...`
 */
class JuliaProjectGeneratorPeerImpl : JuliaProjectGeneratorPeer() {
	private val settings = JuliaSettings()
	private val listeners = ArrayList<ProjectGeneratorPeer.SettingsListener>()

	init {
		setupLaterCheckBox.addChangeListener {
			juliaExeField.isEnabled = !setupLaterCheckBox.isSelected
		}
		usefulText.isVisible = false
		juliaWebsite.setListener({ _, _ -> BrowserLauncher.instance.open(juliaWebsite.text) }, null)
		initExeComboBox(juliaExeField)
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
		if (setupLaterCheckBox.isSelected) return null
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
class JuliaProjectConfigurableImpl(val project: Project) : JuliaProjectConfigurable() {
	private val settings = project.juliaSettings.settings
	private val globalSettings = juliaGlobalSettings

	init {
		version.text = settings.version
		replPromptField.text = settings.replPrompt
		val format = NumberFormat.getIntegerInstance()
		format.isGroupingUsed = false
		val factory = DefaultFormatterFactory(NumberFormatter(format))
		timeLimitField.formatterFactory = factory
		timeLimitField.value = settings.tryEvaluateTimeLimit
		textLimitField.formatterFactory = factory
		textLimitField.value = settings.tryEvaluateTextLimit.toLong()
		maxCharacterToConvertToCompact.formatterFactory = factory
		maxCharacterToConvertToCompact.value = settings.maxCharacterToConvertToCompact
		// TODO workaround for KT-23421
		val listener = LinkListener<Any> { _, _ -> BrowserLauncher.instance.open(juliaWebsite.text) }
		juliaWebsite.setListener(listener, null)
		importPathField.text = settings.importPath
		importPathField.addBrowseFolderListener(TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor(), project))
		basePathField.text = settings.basePath
		basePathField.addBrowseFolderListener(TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor(), project))
		initExeComboBox(juliaExeField) { reinit() }
		// TODO workaround for KT-23421
		val yetAnotherListener = LinkListener<Any> { _, _ -> reinit() }
		refreshButton.setListener(yetAnotherListener, null)
		refreshButton.icon = AllIcons.Actions.Refresh
		val currentExePath = settings.exePath
		if (validateJuliaExe(currentExePath)) {
			juliaExeField.comboBox.selectedItem = currentExePath
			if (settings.importPath.isEmpty()) {
				importPathField.text = importPathOf(currentExePath, 800L)
				settings.importPath = importPathField.text
			}
		}
		juliaExeField.comboBox.addActionListener {
			val exePath = juliaExeField.comboBox.selectedItem as? String ?: return@addActionListener
			importPathField.text = importPathOf(exePath, 800L)
			version.text = versionOf(exePath, 800L)
			tryGetBase(exePath)?.let { basePathField.text = it }
		}
		unicodeInputCheckBox.addChangeListener { globalUnicodeCheckBox.isEnabled = unicodeInputCheckBox.isSelected }
		globalUnicodeCheckBox.isSelected = globalSettings.globalUnicodeInput
		unicodeInputCheckBox.isSelected = settings.unicodeEnabled
		showEvalHintCheckBox.isSelected = settings.showEvalHint
		if (Files.exists(Paths.get(settings.importPath, "DocumentFormat"))) {
			installAutoFormatButton.isEnabled = false
			installAutoFormatButton.text = JuliaBundle.message("julia.messages.doc-format.already")
		} else installAutoFormatButton.addActionListener(installDocumentFormat(project, settings))
	}

	private fun reinit() {
		val exePath = juliaExeField.comboBox.selectedItem as? String ?: return
		importPathField.text = importPathOf(exePath, 800L)
		version.text = versionOf(exePath, 800L)
		tryGetBase(exePath)?.let { basePathField.text = it }
		StubIndex.getInstance().forceRebuild(RuntimeException("Rebuild Index Error!"))
	}

	override fun getDisplayName() = JuliaBundle.message("julia.name")
	override fun createComponent() = mainPanel
	override fun isModified() = settings.importPath != importPathField.text ||
		settings.basePath != basePathField.text ||
		settings.replPrompt != replPromptField.text ||
		settings.exePath != juliaExeField.comboBox.selectedItem ||
		globalUnicodeCheckBox.isSelected != globalSettings.globalUnicodeInput ||
		unicodeInputCheckBox.isSelected != settings.unicodeEnabled ||
		showEvalHintCheckBox.isSelected != settings.showEvalHint ||
		settings.maxCharacterToConvertToCompact != (maxCharacterToConvertToCompact.value as Number).toInt() ||
		settings.tryEvaluateTextLimit != (textLimitField.value as Number).toInt() ||
		settings.tryEvaluateTimeLimit != (timeLimitField.value as Number).toLong()

	@Throws(ConfigurationException::class)
	override fun apply() {
		settings.maxCharacterToConvertToCompact = (maxCharacterToConvertToCompact.value as? Number
			?: throw ConfigurationException(JuliaBundle.message("julia.settings.max-char-for-compact.invalid"))).toInt()
		settings.tryEvaluateTextLimit = (textLimitField.value as? Number
			?: throw ConfigurationException(JuliaBundle.message("julia.modules.try-eval.invalid"))).toInt()
		settings.tryEvaluateTimeLimit = (timeLimitField.value as? Number
			?: throw ConfigurationException(JuliaBundle.message("julia.modules.try-eval.invalid"))).toLong()
		val exePath = juliaExeField.comboBox.selectedItem as? String
		if (exePath == null || !validateJuliaExe(exePath))
			throw ConfigurationException(JuliaBundle.message("julia.modules.invalid"))
		globalSettings.knownJuliaExes += exePath
		globalSettings.globalUnicodeInput = globalUnicodeCheckBox.isSelected
		settings.exePath = exePath
		settings.version = version.text
		settings.basePath = basePathField.text
		settings.importPath = importPathField.text
		settings.replPrompt = replPromptField.text
		settings.unicodeEnabled = unicodeInputCheckBox.isSelected
		settings.showEvalHint = showEvalHintCheckBox.isSelected
		project.reloadSdkAndIndex()
	}
}

/**
 * Settings(Preference) | Language & Frameworks | Julia | Package Manager
 */
class JuliaPackageManagerImpl(private val project: Project) : JuliaPackageManager() {
	private val settings = project.juliaSettings.settings
	private val packagesInfo = juliaGlobalSettings.packagesInfo

	private class JuliaPackageTableModel : DefaultTableModel {
		constructor(data: Array<Array<String>>, columnNames: Array<String>) : super(data, columnNames)
		constructor(row: Int, column: Int) : super(row, column)

		override fun isCellEditable(row: Int, column: Int) = false
	}

	init {
		val beforeVersion07 = compareVersion(settings.version, "0.7.0") < 0
		packagesList.model = JuliaPackageTableModel(emptyArray(), JULIA_TABLE_HEADER_COLUMN)
		val actions = DefaultActionGroup(
			JuliaAddPkgAction(alternativeExecutables, beforeVersion07, this::loadPackages),
			JuliaRemovePkgAction(alternativeExecutables, packagesList, beforeVersion07, this::loadPackages),
			object : AnAction(JuliaIcons.REFRESH_ICON) {
				override fun actionPerformed(e: AnActionEvent) = loadPackages()
			})
		actionsPanel.add(ActionManager
			.getInstance()
			.createActionToolbar(ActionPlaces.MAIN_TOOLBAR, actions, false)
			.component, BorderLayout.CENTER)

		initExeComboBox(alternativeExecutables)
		alternativeExecutables.comboBox.selectedItem = settings.exePath
		packagesList.setShowGrid(false)
	}

	/**
	 * `Fill in my data parameters` INITIALIZATION
	 * @param default Value (true is called by dialog initialization, and false is called by refresh button.)
	 */
	fun loadPackages(default: Boolean = true) {
		ProgressManager.getInstance().run(object :
			Task.Backgroundable(
				project,
				JuliaBundle.message("julia.messages.package.names.loading"),
				true) {
			override fun run(indicator: ProgressIndicator) {
				indicator.text = JuliaBundle.message("julia.messages.package.names.loading")
				val beforeVersion07 = compareVersion(settings.version, "0.7.0") < 0
				var envdir = ""
				val namesList: List<String> = if (beforeVersion07) {
					packageNamesList(settings.importPath).collect(Collectors.toList())
				} else {
					envdir = getEnvDir(settings)
					loadNamesListByEnvFile(settings, envdir)
				}
				val tempData = namesList.map { arrayOf(it) }.toTypedArray()
				val tempDataModel = JuliaPackageTableModel(tempData, JULIA_TABLE_HEADER_COLUMN)
				if (default) {
					packagesList.model = tempDataModel
					packagesInfo.clear()
					namesList.mapTo(packagesInfo) { InfoData(it, "") }
				}

				val sizeToDouble = namesList.size.coerceAtLeast(1).toDouble()

				val versionList =
					if (beforeVersion07) { // legacy package manager use git to get the version
						namesList.asSequence().mapIndexed { index, it ->
							val dir = Paths.get(settings.importPath, it).toFile()
							// process bar indicator percentage
							indicator.fraction = index / sizeToDouble
							if (!dir.exists()) it to ""
							else {
								val process = Runtime.getRuntime().exec(
									arrayOf(gitPath, "describe", "--abbrev=0", "--tags"),
									emptyArray(),
									dir)
								// second column value
								val secondValue = process.inputStream.use { it.reader().use { it.readText().trim() } }
								tempDataModel.setValueAt(secondValue, index, 1)
								it to secondValue
							}
						}.toList()
					} else {
						val versionDir = "v" + settings.version.substringBeforeLast(".")
						val manifestTomlFile = Paths.get(envdir, versionDir, "Manifest.toml").toFile()
						var cur = ""
						val map: Map<String, String> = manifestTomlFile.readLines().mapNotNull {
							when {
								it.startsWith("[[") -> {
									cur = it.substring(2, it.lastIndex - 1)
									null
								}
								it.startsWith("version") -> {
									val ver = it.split(" ").last().trim('"')
									cur to ver
								}
								else -> null
							}
						}.toMap()
						namesList.asSequence().mapIndexed { index, it ->
							// process bar indicator percentage
							indicator.fraction = index / sizeToDouble
							val currentVersionString = map[it] ?: ""
							// second column value
							tempDataModel.setValueAt(currentVersionString, index, 1)
							it to currentVersionString
						}.toList()
					}

				packagesList.model = tempDataModel
				packagesInfo.clear()
				versionList.mapTo(packagesInfo) { InfoData(it.first, it.second) }
			}
		})

	}

	override fun getDisplayName() = JuliaBundle.message("julia.pkg-manager.title")
	override fun createComponent(): JPanel {
		if (packagesInfo.isEmpty()) {
			if (validateJuliaExe(settings.exePath)) loadPackages()
		} else {
			val data = packagesInfo.map {
				arrayOf(it.name, it.version, it.latestVersion)
			}.sortedBy { it.first() }.toTypedArray()
			val dataModel = JuliaPackageTableModel(data, JULIA_TABLE_HEADER_COLUMN)
			packagesList.model = dataModel
		}
		return mainPanel
	}

	override fun isModified() = false
	override fun apply() {
		val selected = alternativeExecutables.comboBox.selectedItem.toString()
		juliaGlobalSettings.knownJuliaExes += selected
		/**
		 * TODO packageInfo needs to be cached, see [juliaGlobalSettings]
		 */
	}
}

class JuliaDocumentConfigurableImpl(private val project: Project) : JuliaDocumentConfigurable() {
	override fun getDisplayName() = JuliaBundle.message("julia.pkg-manager.title")

	private var editorEx: EditorEx? = null

	private fun createEditor(): EditorEx {
		val editorFactory = EditorFactory.getInstance()
		val defaultText = juliaGlobalSettings.markdownCssText.takeIf { it.isNotEmpty() } ?: JULIA_MARKDOWN_DARCULA_CSS
		val editorDocument = editorFactory.createDocument(defaultText)
		var editor: EditorEx? = null
		ApplicationManager.getApplication().runWriteAction {
			editor = editorFactory.createEditor(editorDocument) as EditorEx
			val e = editor ?: return@runWriteAction
			fillEditorSettings(e.settings)
			setHighlighting(e)
		}

		return editor!!
	}

	private fun setHighlighting(editor: EditorEx) {
		val cssFileType = FileTypeManager.getInstance().getFileTypeByExtension("css")
		if (cssFileType !== UnknownFileType.INSTANCE) {
			val editorHighlighter = HighlighterFactory.createHighlighter(cssFileType, EditorColorsManager.getInstance().globalScheme, null as Project?)
			editor.highlighter = editorHighlighter
		}
	}

	override fun disposeUIResources() {
		val editorEx = editorEx ?: return
		val editorFactory = EditorFactory.getInstance()
		ApplicationManager.getApplication().runWriteAction {
			editorFactory.releaseEditor(editorEx)
		}
	}

	private fun fillEditorSettings(editorSettings: EditorSettings) {
		editorSettings.isWhitespacesShown = false
		editorSettings.isLineMarkerAreaShown = false
		editorSettings.isIndentGuidesShown = false
		editorSettings.isLineNumbersShown = true
		editorSettings.isFoldingOutlineShown = false
		editorSettings.additionalColumnsCount = 1
		editorSettings.additionalLinesCount = 1
		editorSettings.isUseSoftWraps = false
	}

	override fun createComponent(): JPanel {
		editorEx = createEditor().apply {
			contentComponent.isEnabled = true
			setCaretEnabled(true)
			editorPanel.add(this.component, "Center")
		}
		return mainPanel
	}

	override fun isModified(): Boolean {
		val editorEx = editorEx ?: return false
		return ApplicationManager.getApplication().runReadAction<Boolean> {
			FileDocumentManager.getInstance().saveDocument(editorEx.document)
			editorEx.document.text != juliaGlobalSettings.markdownCssText
		}
	}

	override fun apply() {
		val editorEx = editorEx ?: return
		ApplicationManager.getApplication().runWriteAction {
			FileDocumentManager.getInstance().saveDocument(editorEx.document)
			juliaGlobalSettings.markdownCssText = editorEx.document.text
		}
	}
}