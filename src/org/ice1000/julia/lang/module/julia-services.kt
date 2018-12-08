package org.ice1000.julia.lang.module

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializationException
import com.intellij.util.xmlb.XmlSerializerUtil
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
}

val Project.juliaSettings: JuliaProjectSettingsService
	get() = ServiceManager.getService(this, JuliaProjectSettingsService::class.java)

val juliaGlobalSettings: JuliaGlobalSettingsService
	get() = ServiceManager.getService(JuliaGlobalSettingsService::class.java)

@State(
	name = "JuliaProjectSettings",
	storages = [Storage(file = "juliaConfig.xml", scheme = StorageScheme.DIRECTORY_BASED)])
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
	storages = [Storage(file = "juliaGlobalConfig2.xml", scheme = StorageScheme.DIRECTORY_BASED)])
class JuliaGlobalSettingsServiceImpl :
	JuliaGlobalSettingsService, PersistentStateComponent<JuliaGlobalSettings2> {
	override val knownJuliaExes: MutableSet<String> = hashSetOf()
	override val packagesInfo: MutableSet<InfoData> = hashSetOf()
	override var globalUnicodeInput: Boolean = false
	private fun invalidate() = knownJuliaExes.removeAll { !validateJuliaExe(it) }
	override fun getState(): JuliaGlobalSettings2 {
		invalidate()
		return JuliaGlobalSettings2(
			globalUnicodeInput,
			knownJuliaExes.joinToString(File.pathSeparator),
			packagesInfo.joinToString(File.pathSeparator) { "${it.name} ${it.version} ${it.latestVersion}" })
	}

	override fun loadState(state: JuliaGlobalSettings2) {
		invalidate()
		globalUnicodeInput = state.globalUnicodeInput
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