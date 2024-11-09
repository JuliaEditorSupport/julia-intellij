package org.ice1000.julia.lang.module

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializationException
import com.intellij.util.xmlb.XmlSerializerUtil
import org.ice1000.julia.lang.JULIA_MARKDOWN_DARCULA_CSS
import org.ice1000.julia.lang.JULIA_MARKDOWN_INTELLIJ_CSS
import java.io.File

/**
 * @author zxj5470, ice1000
 * @date 2018/2/1
 */
interface JuliaProjectSettingsService {
	val settings: JuliaSettings
}

interface JuliaGlobalSettingsService {
	val knownJuliaExes: MutableSet<String>
	val packagesInfo: MutableSet<InfoData>
	var globalUnicodeInput: Boolean
	var darculaThemeCssText: String
	var intellijThemeCssText: String
}

val Project.juliaSettings: JuliaProjectSettingsService
	get() = ServiceManager.getService(this, JuliaProjectSettingsService::class.java)

val Project.languageServer: JuliaLanguageServerService
	get() = JuliaLanguageServerService.getInstance(this)

val juliaGlobalSettings: JuliaGlobalSettingsService
	get() = ServiceManager.getService(JuliaGlobalSettingsService::class.java)

@State(
	name = "JuliaProjectSettings",
	storages = [Storage(file = "juliaConfig.xml")])
class JuliaProjectSettingsServiceImpl :
	JuliaProjectSettingsService, PersistentStateComponent<JuliaSettings> {
	override val settings = JuliaSettings(replPrompt = "julia> ")
	override fun getState(): JuliaSettings? = XmlSerializerUtil.createCopy(settings)
	override fun loadState(state: JuliaSettings) {
		XmlSerializerUtil.copyBean(state, settings)
		try {
			settings.exePath.let {
				if (validateJuliaExe(it)) juliaGlobalSettings.knownJuliaExes += it
			}
		} catch (e: XmlSerializationException) {
		}
	}
}

@State(
	name = "JuliaGlobalSettings2",
	storages = [Storage(file = "juliaGlobalConfig2.xml")])
class JuliaGlobalSettingsServiceImpl :
	JuliaGlobalSettingsService, PersistentStateComponent<JuliaGlobalSettings2> {
	override val knownJuliaExes: MutableSet<String> = hashSetOf()
	override val packagesInfo: MutableSet<InfoData> = hashSetOf()
	override var globalUnicodeInput: Boolean = false
	override var darculaThemeCssText: String = JULIA_MARKDOWN_DARCULA_CSS
	override var intellijThemeCssText: String = JULIA_MARKDOWN_INTELLIJ_CSS
	private fun invalidate() = knownJuliaExes.removeAll { !validateJuliaExe(it) }
	override fun getState(): JuliaGlobalSettings2 {
		invalidate()
		return JuliaGlobalSettings2(
			globalUnicodeInput,
			knownJuliaExes.joinToString(File.pathSeparator),
			packagesInfo.joinToString(File.pathSeparator) { "${it.name} ${it.version} ${it.latestVersion}" },
			darculaThemeCssText,
			intellijThemeCssText)
	}

	override fun loadState(state: JuliaGlobalSettings2) {
		invalidate()
		globalUnicodeInput = state.globalUnicodeInput
		darculaThemeCssText = state.markdownCssText
		intellijThemeCssText = state.intellijThemeCssText
		knownJuliaExes += state.allJuliaExePath.split(File.pathSeparatorChar)
		state.packagesInfo.split(File.pathSeparatorChar).mapNotNullTo(packagesInfo) {
			val (name, version, latest) = it
				.split(" ")
				.takeIf { it.size >= 3 }
				?: return@mapNotNullTo null
			InfoData(name, version, latest)
		}
	}
}