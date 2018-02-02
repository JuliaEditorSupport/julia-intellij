package org.ice1000.julia.lang.module

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

/**
 * @author zxj5470, ice1000
 * @date 2018/2/1
 */
interface JuliaProjectSettingsService {
	var settings: JuliaSettings
}

val Project.juliaSettings: JuliaProjectSettingsService
	get() = ServiceManager.getService(this, JuliaProjectSettingsService::class.java)

@State(name = "JuliaProjectSettings")
class JuliaProjectSettingsServiceImpl(private val project: Project) :
	JuliaProjectSettingsService, PersistentStateComponent<JuliaSettings> {
	override fun getState() = settings
	override fun loadState(state: JuliaSettings?) {
		state?.let { settings = it }
	}

	override var settings = JuliaSettings(exePath = defaultExePath)
}
