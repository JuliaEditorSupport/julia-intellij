package org.ice1000.julia.lang.module

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.SystemInfo
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import java.awt.event.ActionListener
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

val defaultExePath by lazy {
	val existPath = PropertiesComponent.getInstance().getValue(JULIA_SDK_HOME_PATH_ID).orEmpty()
	when {
		validateJuliaExe(existPath) -> existPath
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

class JuliaSettings(
	var importPath: String = "",
	var exePath: String = "",
	var basePath: String = "",
	var version: String = "",
	var tryEvaluateTimeLimit: Long = 2500L,
	var tryEvaluateTextLimit: Int = 320) {
	fun initWithExe() {
		version = versionOf(exePath)
		importPath = importPathOf(exePath)
		tryGetBase(exePath)?.let { basePath = it }
	}
}

fun tryGetBase(exePath: String): String? {
	val home = Paths.get(exePath)?.parent?.parent ?: return null
	val exePathBase = Paths.get("$home", "share", "julia", "base")?.toAbsolutePath() ?: return null
	if (Files.exists(exePathBase)) return exePathBase.toString()
	else if (SystemInfo.isLinux) return "/usr/share/julia/base"
	return null
}

fun versionOf(exePath: String, timeLimit: Long = 800L) =
	executeJulia(exePath, null, timeLimit, "--version")
		.first
		.firstOrNull { it.startsWith("julia version", true) }
		?.dropWhile { it.isLetter() or it.isWhitespace() }
		?: JuliaBundle.message("julia.modules.sdk.unknown-version")

fun importPathOf(exePath: String, timeLimit: Long = 800L) =
	executeJulia(exePath, null, timeLimit, "--print", "Pkg.dir()")
		.first
		.lastOrNull()
		.orEmpty()
		.trim('"')

fun validateJuliaExe(exePath: String) = versionOf(exePath) != JuliaBundle.message("julia.modules.sdk.unknown-version")
fun validateJulia(settings: JuliaSettings) = settings.version != JuliaBundle.message("julia.modules.sdk.unknown-version")

fun installAutoFormat(
	project: Project,
	settings: JuliaSettings): ActionListener = ActionListener {
	ProgressManager.getInstance()
		.run(object : Task.Backgroundable(project, JuliaBundle.message("julia.messages.doc-format.installing"), true) {
			override fun run(indicator: ProgressIndicator) {
				// indicator.text = JuliaBundle.message("julia.messages.auto-format.installing")
				executeJulia(settings.exePath, DOCFMT_INSTALL, 1000000L)
			}

			override fun onSuccess() = ApplicationManager.getApplication().invokeLater {
				Messages.showDialog(
					project,
					JuliaBundle.message("julia.messages.doc-format.installed"),
					JuliaBundle.message("julia.messages.doc-format.installed.title"),
					arrayOf(JuliaBundle.message("julia.yes")),
					0,
					JuliaIcons.JOJO_ICON)
			}
		})
}

