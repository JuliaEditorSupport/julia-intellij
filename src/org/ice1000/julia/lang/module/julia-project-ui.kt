@file:Suppress("DEPRECATION")

package org.ice1000.julia.lang.module

import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.components.Link
import com.intellij.ui.layout.panel
import org.ice1000.julia.lang.*

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
		if (validateJuliaExe(text)) {
			settings.settings = JuliaSettings(exePath = text)
			juliaProjectSettingsPanel.versionToLabel.text = juliaProjectSettings.settings.exePath.let { versionOf(it) }
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
}


val downloadJuliaSdkLink = Link(JULIA_WEBSITE, action = {
	BrowserLauncher.instance.open(JULIA_WEBSITE)
})
