package org.ice1000.julia.lang.module

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project

class JuliaLanguageServerService(val project: Project) : Disposable {
	var myProcess: Process?
	init {
		val process: Process = Runtime.getRuntime().exec(project.juliaSettings.settings.exePath)
		myProcess = process
	}

	override fun dispose() {
		val process = myProcess ?: return
		process.destroy()
	}

	companion object {
		@JvmStatic
		fun getInstance(project: Project): JuliaLanguageServerService {
			return ServiceManager.getService(project, JuliaLanguageServerService::class.java)
		}
	}

	fun searchByName(name: String): String? {
		val process = myProcess ?: return null
		val command = "using JSON;methods($name) |> collect .|> functionloc |> json\n"
		val outputStream = process.outputStream
		val inputStream = process.inputStream
		outputStream.write(command.toByteArray())
		outputStream.flush()
		return inputStream.bufferedReader().readLine()
	}
}