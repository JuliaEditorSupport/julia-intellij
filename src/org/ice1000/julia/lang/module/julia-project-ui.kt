package org.ice1000.julia.lang.module

import com.intellij.openapi.Disposable
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.components.Link
import com.intellij.ui.layout.LayoutBuilder
import com.intellij.ui.layout.panel
import org.ice1000.julia.lang.JULIA_LANGUAGE_NAME
import org.ice1000.julia.lang.JULIA_WEBSITE
import java.awt.Desktop.getDesktop
import java.net.URI
import javax.swing.JLabel
import javax.swing.event.DocumentEvent

/**
 * @author: zxj5470
 * @date: 2018/1/30
 */
class JuliaProjectConfigurable : Configurable {
	private val rustProjectSettings = JuliaProjectSettingsPanel()
	override fun isModified() = false
	override fun getDisplayName() = JULIA_LANGUAGE_NAME
	override fun apply() {
	}

	override fun createComponent() = panel {
		rustProjectSettings.attachTo(this@panel)
	}

	override fun getHelpTopic(): String = "https://www.github.com/ice1000/julia-intellij"

}

class JuliaProjectSettingsPanel : Disposable {
	override fun dispose() = Unit
	/*Do we need checkboxes in SettingsPanel? */
//	class CheckboxDelegate(private val checkbox: JBCheckBox) {
//		operator fun getValue(thisRef: Any?, property: KProperty<*>)= checkbox.isSelected
//		operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean){
//			checkbox.isSelected = value
//		}
//	}

	private val sdkEditor = pathToDirectoryTextField(this, defaultSdkHome)

	fun attachTo(layout: LayoutBuilder) = with(layout) {
		row("Julia SDK Home Location:") { sdkEditor() }
		row("Julia SDK version:") { versionToLabel() }
		row("Download SDK:") { downloadJuliaSdkLink() }
	}
}

val downloadJuliaSdkLink = Link(JULIA_WEBSITE, action = {
	getDesktop().takeIf { it.isSupported(java.awt.Desktop.Action.BROWSE) }?.browse(URI.create(JULIA_WEBSITE))
})

val versionToLabel = JLabel(versionOf(defaultSdkHome))
fun pathToDirectoryTextField(
	disposable: Disposable,
	title: String,
	onTextChanged: ((e: DocumentEvent?) -> Unit)? = null)
	: TextFieldWithBrowseButton {
	val component = TextFieldWithBrowseButton(null, disposable)
	component.addBrowseFolderListener(title, null, null,
		FileChooserDescriptorFactory.createSingleFolderDescriptor(),
		TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT)
	component.childComponent.document.addDocumentListener(object : DocumentAdapter() {
		override fun textChanged(e: DocumentEvent?) {
			println(e?.document.toString())
			onTextChanged?.invoke(e)
		}
	})
	component.text = title
	return component
}
