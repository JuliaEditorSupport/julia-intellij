package org.ice1000.julia.lang.module

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.intellij.util.xmlb.XmlSerializerUtil
import java.nio.file.Files
import java.nio.file.Paths

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
class JuliaProjectSettingsServiceImpl :
	JuliaProjectSettingsService, PersistentStateComponent<JuliaSettings> {
	override var settings: JuliaSettings = JuliaSettings()

	init {
		with(settings) {
			exePath = defaultExePath
			version = versionOf(exePath)
			importPath = importPathOf(exePath)
			val exe = Paths.get(exePath)?.parent?.parent ?: return@with
			val exePathBase = Paths.get("$exe", "share", "julia", "base")?.toAbsolutePath() ?: return@with
			if (Files.exists(exePathBase)) basePath = exePathBase.toString()
			else if (SystemInfo.isLinux) basePath = "/usr/share/julia/base"
		}
	}

	override fun getState(): JuliaSettings? = XmlSerializerUtil.createCopy(settings)
	override fun loadState(state: JuliaSettings) {
		XmlSerializerUtil.copyBean(state, settings)
	}
}
