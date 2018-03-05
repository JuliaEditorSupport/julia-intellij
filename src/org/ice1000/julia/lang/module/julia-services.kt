package org.ice1000.julia.lang.module

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * @author zxj5470, ice1000
 * @date 2018/2/1
 */
interface JuliaProjectSettingsService {
	val settings: JuliaSettings
}

interface JuliaGlobalSettingsService {
	var settings: JuliaSettings
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
	override val settings = JuliaSettings()
	override fun getState(): JuliaSettings? = XmlSerializerUtil.createCopy(settings)
	override fun loadState(state: JuliaSettings) {
		XmlSerializerUtil.copyBean(state, settings)
	}
}

@State(
	name = "JuliaGlobalSettings",
	storages = [(Storage(file = "juliaGlobalConfig.xml", scheme = StorageScheme.DIRECTORY_BASED))]
)
class JuliaGlobalSettingsServiceImpl :
	JuliaGlobalSettingsService, PersistentStateComponent<JuliaSettings> {

	override var settings = JuliaSettings()
	override fun getState(): JuliaSettings? = XmlSerializerUtil.createCopy(settings)
	override fun loadState(state: JuliaSettings) {
		XmlSerializerUtil.copyBean(state, settings)
	}
}