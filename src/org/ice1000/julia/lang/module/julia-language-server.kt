package org.ice1000.julia.lang.module

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project

/**
 * + should use `try` expression when sending codes to language server.
 * + shouldn't use `use` in (IO)Stream
 * @property juliaSettings JuliaProjectSettingsService
 * @property myProcess Process?
 * @constructor
 */
class JuliaLanguageServerService(val juliaSettings: JuliaProjectSettingsService) : Disposable {
	private var myProcess: Process? = null

	init {
		recheckProcess()
	}

	private fun recheckProcess() {
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

	fun sendCommand(command: String): String? {
		recheckProcess()
		val process = myProcess ?: return null
		val outputStream = process.outputStream
		outputStream.write(command.toByteArray())
		outputStream.flush()
		return process.inputStream.bufferedReader().readLine()
	}

	fun searchFunctionsByName(name: String): String? {
		recheckProcess()
		val process = myProcess ?: return null
		// language=Julia
		val command = """
using JSON
try
methods($name) |> collect .|> functionloc |> json
catch e
println("__INTELLIJ__"*repr(e))
end
"""
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

	fun searchDocsByName(name: String): String? {
		recheckProcess()
		val process = myProcess ?: return null
		// language=Julia
		val command = """
try
    repr(Docs.doc($name))
catch e
    println("__INTELLIJ__"*repr(e))
end
"""
		val outputStream = process.outputStream
		val inputStream = process.inputStream
		outputStream.write(command.toByteArray())
		outputStream.flush()
		return inputStream.bufferedReader().readLine()
	}
}