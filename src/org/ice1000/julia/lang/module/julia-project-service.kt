package org.ice1000.julia.lang.module

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.util.messages.Topic
import com.intellij.openapi.components.State
import org.ice1000.julia.lang.JuliaBundle

/**
 * @author: zxj5470
 * @date: 2018/2/1
 */
interface JuliaProjectSettingsServiceI {
	data class JuliaConfigData(
		val settings: JuliaProjectSettings?
	)

	var configData: JuliaConfigData
//	val settings: JuliaProjectSettings? get() = configData.settings

	companion object {
		val sdkHomeTopic: Topic<SdkHomeListener> = Topic(
			"sdk Home change",
			SdkHomeListener::class.java
		)
	}

	interface SdkHomeListener {
		fun sdkHomeChanged()
	}
}

val Project.juliaSettings: JuliaProjectSettingsServiceI
	get() = ServiceManager.getService(this, JuliaProjectSettingsServiceI::class.java)
		?: error("Failed to get ProjectSettingsService for $this")

@State(name = "JuliaProjectSettings")
class JuliaProjectSettingsServiceImpl(private val project: Project) :
	JuliaProjectSettingsServiceI, PersistentStateComponent<JuliaProjectSettingsServiceImpl.State> {
	override fun getState(): State = state
	override fun loadState(newState: State) {
		state = newState
	}

	private var state: State = State()
	override var configData: JuliaProjectSettingsServiceI.JuliaConfigData
		get() {
			return JuliaProjectSettingsServiceI.JuliaConfigData(
				JuliaProjectSettings(state.sdkHome)
			)
		}
		set(value) {
			val newState = State(
				sdkHome = value.settings?.sdkHome.orEmpty()
			)
			if (state != newState) {
				state = newState
				notifySdkHomeChanged()
			}
		}

	data class State(
		var sdkHome: String = "",
		var sdkVersion: String = JuliaBundle.message("julia.modules.sdk.unknown-version")
	)

	private fun notifySdkHomeChanged() {
		project.messageBus.syncPublisher(JuliaProjectSettingsServiceI.sdkHomeTopic).sdkHomeChanged()
	}

}