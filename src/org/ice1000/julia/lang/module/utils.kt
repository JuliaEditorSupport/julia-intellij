@file:JvmName("ModuleUtils")
@file:JvmMultifileClass

package org.ice1000.julia.lang.module

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.SystemInfo
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import java.awt.event.ActionListener
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors

val defaultExePath by lazy {
	val existPath = PropertiesComponent.getInstance().getValue(JULIA_SDK_HOME_PATH_ID, "")
	// Notice:
	// Files.isExecutable(Paths.get("")) == true
	// And the isExecutable is used to check whether you have permission to access the file.
	if (!existPath.isEmpty() && Files.isExecutable(Paths.get(existPath))) existPath else juliaPath
}

@Suppress("DEPRECATION")
val juliaPath by lazy {
	when {
		SystemInfo.isWindows -> PathEnvironmentVariableUtil.findInPath("julia.exe")?.absolutePath ?: "C:\\Program Files"
		SystemInfo.isMac -> findPathMac()
		else -> findPathLinux() ?: "/usr/bin/julia"
	}
}

val gitPath by lazy {
	PathEnvironmentVariableUtil.findInPath("julia")?.absolutePath ?: "git"
}

fun findPathMac(): String {
	val appPath = Paths.get(MAC_APPLICATIONS)
	val result = Files.list(appPath).collect(Collectors.toList()).firstOrNull { application ->
		application.toString().contains("julia", true)
	} ?: appPath
	val folderAfterPath = "/Contents/Resources/julia/bin/julia"
	return result.toAbsolutePath().toString() + folderAfterPath
}

@Deprecated("", ReplaceWith("PathEnvironmentVariableUtil.findInPath"))
fun findPathWindows() = executeCommandToFindPath("where julia")

@Deprecated("", ReplaceWith("PathEnvironmentVariableUtil.findInPath"))
fun findPathLinux() = executeCommandToFindPath("whereis julia")

class JuliaSettings(
	var importPath: String = "",
	var exePath: String = "",
	var basePath: String = "",
	var version: String = "",
	var unicodeEnabled: Boolean = true,
	var showEvalHint: Boolean = false,
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

/**
 * It's somewhat unsafe, need some robustness check.
 * In Julia, all versions are like "1.1.1", so it's probably safe.
 *
 * @author ice1000
 * @param version a version
 * @param version2 another version
 * @return 0 if equal, positive if newer, negative if lesser
 */
fun compareVersion(version: String, version2: String): Int {
	if (version == version2) return 0
	val (v0, v1, v2) = version.split('.')
	val (v20, v21, v22) = version2.split('.')
	return when {
		v0 != v20 -> v0.toInt() - v20.toInt()
		v1 != v21 -> v1.toInt() - v21.toInt()
		else -> v2.toInt() - v22.toInt()
	}
}

fun importPathOf(exePath: String, timeLimit: Long = 800L) =
	executeJulia(exePath, null, timeLimit, "--print", "Pkg.dir()")
		.first
		.lastOrNull()
		.orEmpty()
		.trim('"')

fun validateJuliaExe(exePath: String) = Files.isExecutable(Paths.get(exePath))
fun validateJulia(settings: JuliaSettings) = validateJuliaExe(settings.exePath)
fun installDocumentFormat(
	project: Project,
	settings: JuliaSettings): ActionListener = ActionListener {
	ProgressManager.getInstance()
		.run(object : Task.Backgroundable(project, JuliaBundle.message("julia.messages.doc-format.installing"), true) {
			override fun run(indicator: ProgressIndicator) {
				indicator.text = JuliaBundle.message("julia.messages.doc-format.installing")
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

/**
 * @ref https://gist.github.com/DemkaAge/8999236
 */
object JuliaUTF8Control : ResourceBundle.Control() {
	override fun newBundle(baseName: String?, locale: Locale?, format: String?, loader: ClassLoader?, reload: Boolean): ResourceBundle {
		val bundleName = toBundleName(baseName, locale)
		val connection = loader?.getResource(toResourceName(bundleName, "properties"))?.openConnection()
		if (connection != null) {
			connection.useCaches = false
			connection.getInputStream()?.use {
				return PropertyResourceBundle(InputStreamReader(it, "UTF-8"))
			}
		}
		return ResourceBundle.getBundle(baseName)
	}
}
