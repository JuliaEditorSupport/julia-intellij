package org.ice1000.julia.lang.module

import com.intellij.openapi.components.State
import com.intellij.openapi.util.SystemInfo
import org.ice1000.julia.lang.*
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

val defaultExePath by lazy {
	when {
		SystemInfo.isWindows -> findPathWindows() ?: "C:\\Program Files"
		SystemInfo.isMac -> findPathMac()
		else -> findPathLinux() ?: "/usr/bin/julia"
	}
}

fun findPathMac(): String {
	val appPath = Paths.get(MAC_APPLICATIONS)
	val result = Files.list(appPath).collect(Collectors.toList()).firstOrNull { application ->
		application.toString().contains("julia", true)
	} ?: appPath
	val folderAfterPath = "/Contents/Resources/julia/bin/julia"
	return result.toAbsolutePath().toString() + folderAfterPath
}

fun findPathWindows() = executeCommandToFindPath("where julia")
private fun findPathLinux() = executeCommandToFindPath("whereis julia")

@State(name = "JULIA_SETTINGS", storages = [])
open class JuliaSettings(
	var importPath: String = "",
	var exePath: String = "",
	var basePath: String = "",
	var version: String = JuliaBundle.message("julia.modules.sdk.unknown-version"),
	var tryEvaluateTimeLimit: Long = 2500L,
	var tryEvaluateTextLimit: Int = 320)

fun versionOf(exePath: String, timeLimit: Long = 800L) =
	executeJulia(exePath, null, timeLimit, "--version")
		.first
		.firstOrNull { it.startsWith("julia version", true) }
		?.dropWhile { it.isLetter() or it.isWhitespace() }
		?: JuliaBundle.message("julia.modules.sdk.unknown-version")

fun importPathOf(exePath: String, timeLimit: Long = 800L) =
	executeJulia(exePath, null, timeLimit, "--print", "Pkg.dir()")
		.first
		.first()
		.trim('"')

fun validateJuliaExe(exePath: String) = versionOf(exePath) != JuliaBundle.message("julia.modules.sdk.unknown-version")
