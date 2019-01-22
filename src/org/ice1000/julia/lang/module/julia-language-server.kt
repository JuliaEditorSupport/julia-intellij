package org.ice1000.julia.lang.module

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import org.jetbrains.rpc.LOG
import java.nio.file.Files
import java.nio.file.Paths

class JuliaLanguageServerService(val juliaSettings: JuliaProjectSettingsService) : Disposable {
	var myProcess: Process? = null

	init {
		recheckProcess()
	}

	fun recheckProcess() {
		if (myProcess != null) return
		val exePath = juliaSettings.settings.exePath
		if (exePath.isNotBlank())
			myProcess = Runtime.getRuntime().exec(exePath)
	}

	override fun dispose() {
		myProcess?.destroy()
	}

	companion object {
		@JvmStatic
		fun getInstance(project: Project): JuliaLanguageServerService {
			return ServiceManager.getService(project, JuliaLanguageServerService::class.java)
		}
	}

	fun searchFunctionsByName(name: String): String? {
		recheckProcess()
		val process = myProcess ?: return null
		val command = "using JSON;methods($name) |> collect .|> functionloc |> json\n"
		val outputStream = process.outputStream
		val inputStream = process.inputStream
		outputStream.write(command.toByteArray())
		outputStream.flush()
		return inputStream.bufferedReader().readLine()
	}

	fun searchSubTypesByName(name: String): String? {
		recheckProcess()
		val process = myProcess ?: return null
		val command = "using JSON;subtypes($name) |> json\n"
		val outputStream = process.outputStream
		val inputStream = process.inputStream
		outputStream.write(command.toByteArray())
		outputStream.flush()
		return inputStream.bufferedReader().readLine()
	}
}