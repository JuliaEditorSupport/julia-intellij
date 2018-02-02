@file:Suppress("DEPRECATION")

package org.ice1000.julia.lang.module

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.Disposable
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.components.Link
import com.intellij.ui.layout.LayoutBuilder
import com.intellij.ui.layout.panel
import org.ice1000.julia.lang.*
import java.awt.Desktop.getDesktop
import java.net.URI
import javax.swing.JLabel
import javax.swing.event.DocumentEvent

/**
 * JuliaProjectConfigurable
 * Setting in : Preferences -> Languages&Frameworks -> Julia
 * @author: zxj5470
 * @date: 2018/1/30
 */
class JuliaProjectConfigurable(private val project: Project) : Configurable {

	private val juliaProjectSettings = project.juliaSettings
	private val juliaProjectSettingsPanel = JuliaProjectSettingsPanel(juliaProjectSettings)

	override fun isModified() = true
	override fun getDisplayName() = JULIA_LANGUAGE_NAME
	override fun apply() {
		val settings = project.juliaSettings
		val text = juliaProjectSettingsPanel.sdkEditor.text
		if (JuliaSdkType().isValidSdkHome(text)) {
			settings.configData = JuliaProjectSettingsServiceI.JuliaConfigData(JuliaProjectSettings(text))
			juliaProjectSettingsPanel.versionToLabel.text = juliaProjectSettings.configData.settings?.exePath?.let { versionOf(it) }.orEmpty()
//			"juliaSdkHome" name needed in Bundle
			PropertiesComponent.getInstance().setValue(JULIA_SDK_HOME_PATH_ID, text)
		}
	}

	override fun reset() {
		// TODO
	}

	override fun createComponent() = panel {
		juliaProjectSettingsPanel.attachTo(this@panel)
	}

	override fun getHelpTopic(): String = "https://www.github.com/ice1000/julia-intellij"

}

class JuliaProjectSettingsPanel(projectSettings: JuliaProjectSettingsServiceI) : Disposable {
	override fun dispose() = Unit
	/*Do we need checkboxes in SettingsPanel?
//	class CheckboxDelegate(private val checkbox: JBCheckBox) {
//		operator fun getValue(thisRef: Any?, property: KProperty<*>)= checkbox.isSelected
//		operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean){
//			checkbox.isSelected = value
//		}
//	}
 */
	val sdkEditor = pathToDirectoryTextField(this)
	val versionToLabel = JLabel(projectSettings.configData.settings?.exePath?.let { versionOf(it) }.orEmpty())
	fun attachTo(layout: LayoutBuilder) = with(layout) {
		row("Julia SDK Home Location:") { sdkEditor() }
		row("Julia SDK version:") { versionToLabel() }
		row("Download SDK:") { downloadJuliaSdkLink() }
	}
}

val downloadJuliaSdkLink = Link(JULIA_WEBSITE, action = {
	getDesktop().takeIf { it.isSupported(java.awt.Desktop.Action.BROWSE) }?.browse(URI.create(JULIA_WEBSITE))
})

fun pathToDirectoryTextField(
	disposable: Disposable,
	title: String = JuliaBundle.message("julia.modules.sdk.selection.title"),
	onTextChanged: ((e: DocumentEvent?) -> Unit)? = null): TextFieldWithBrowseButton {
	val component = TextFieldWithBrowseButton(null, disposable)
	component.addBrowseFolderListener(title, null, null,
		FileChooserDescriptorFactory.createSingleFolderDescriptor(),
		TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT)
	component.childComponent.document.addDocumentListener(object : DocumentAdapter() {
		override fun textChanged(e: DocumentEvent?) {
			onTextChanged?.invoke(e)
		}
	})
	val existPath = PropertiesComponent.getInstance().getValue(JULIA_SDK_HOME_PATH_ID).orEmpty()
	if (validateJuliaSDK(existPath)) {
		component.text = existPath
	}
	return component
}
