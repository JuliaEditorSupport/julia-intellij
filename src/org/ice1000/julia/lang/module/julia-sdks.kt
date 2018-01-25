package org.ice1000.julia.lang.module

import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.projectRoots.ui.ProjectJdksEditor
import com.intellij.openapi.util.SystemInfo
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.ComboboxWithBrowseButton
import org.ice1000.julia.lang.*
import org.jdom.Element
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.DefaultComboBoxModel
import javax.swing.JList

class JuliaSdkType : SdkType(JuliaBundle.message("julia.name")) {
	override fun getPresentableName() = JuliaBundle.message("julia.modules.sdk.name")
	override fun getIcon() = JULIA_BIG_ICON
	override fun getIconForAddAction() = icon
	override fun isValidSdkHome(sdkHome: String?) = validateJuliaSDK(sdkHome.orEmpty())
	override fun suggestSdkName(s: String?, p1: String?) = JuliaBundle.message("julia.modules.sdk.name")
	override fun suggestHomePath() = when {
		SystemInfo.isLinux -> POSSIBLE_SDK_HOME_LINUX
		SystemInfo.isWindows -> System.getenv("LOCALAPPDATA")
		SystemInfo.isMac -> TODO()
		else -> null
	}
	override fun getDownloadSdkUrl() = JULIA_WEBSITE
	override fun createAdditionalDataConfigurable(md: SdkModel, m: SdkModificator) = null
	override fun getVersionString(sdkHome: String?) = versionOf(sdkHome.orEmpty())
	override fun saveAdditionalData(additionalData: SdkAdditionalData, element: Element) = Unit // leave blank
	override fun setupSdkPaths(sdk: Sdk, sdkModel: SdkModel): Boolean {
		val modificator = sdk.sdkModificator
		// modificator.sdkAdditionalData = sdk.sdkAdditionalData ?: Default
		modificator.versionString = getVersionString(sdk) ?: JuliaBundle.message("julia.modules.sdk.unknown-version")
		modificator.commitChanges()
		return true
	}

	companion object InstanceHolder {
		val instance get() = SdkType.findInstance(JuliaSdkType::class.java)
	}
}

fun versionOf(sdkHome: String, timeLimit: Long = 500L) =
		executeJulia(sdkHome, null, timeLimit, "--version")
				.first
				.firstOrNull { it.startsWith("julia version", true) }
				?.dropWhile { it.isLetter() or it.isWhitespace() }
				?: JuliaBundle.message("julia.modules.sdk.unknown-version")

fun validateJuliaSDK(sdkHome: String) = Files.isExecutable(Paths.get(sdkHome, "bin", "julia"
		+ if (SystemInfo.isWindows) ".exe" else ""))


class JuliaSdkCombobox : ComboboxWithBrowseButton() {
	val selectedSdk get() = comboBox.selectedItem as? Sdk
	val sdkName get() = selectedSdk?.name.orEmpty()

	init {
		comboBox.setRenderer(object : ColoredListCellRenderer<Sdk?>() {
			override fun customizeCellRenderer(
					list: JList<out Sdk?>,
					value: Sdk?,
					index: Int,
					selected: Boolean,
					hasFocus: Boolean) {
				value?.name?.let(::append)
			}
		})
		addActionListener {
			var selectedSdk = selectedSdk
			val project = ProjectManager.getInstance().defaultProject
			val editor = ProjectJdksEditor(selectedSdk, project, this@JuliaSdkCombobox)
			editor.title = JuliaBundle.message("julia.modules.sdk.selection.title")
			editor.show()
			if (editor.isOK) {
				selectedSdk = editor.selectedJdk
				updateSdkList(selectedSdk)
			}
		}
		updateSdkList()
	}

	private fun updateSdkList(sdkToSelectOuter: Sdk? = null) {
		ProjectJdkTable.getInstance().getSdksOfType(JuliaSdkType.instance).run {
			comboBox.model = DefaultComboBoxModel(toTypedArray())
			comboBox.selectedItem = sdkToSelectOuter ?: firstOrNull()
		}
	}
}