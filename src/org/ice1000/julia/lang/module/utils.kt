@file:JvmName("ModuleUtils")
@file:JvmMultifileClass

package org.ice1000.julia.lang.module

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.openapi.application.*
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.module.Module
import com.intellij.openapi.progress.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ComboboxWithBrowseButton
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.intellij.plugins.markdown.settings.MarkdownCssSettings
import java.awt.event.ActionListener
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.swing.JComboBox


/**
 * Can be used in test cases.
 */
val juliaPath by lazy {
	when {
		SystemInfo.isWindows -> findPathWindows() ?: "C:\\Program Files"
		SystemInfo.isMac -> findPathMac()
		else -> findPathLinux() ?: "/usr/bin/julia"
	}
}

val gitPath by lazy {
	PathEnvironmentVariableUtil.findInPath(if (SystemInfo.isWindows) "git.exe" else "git")?.canonicalPath ?: "git"
}

fun findPathMac(): String {
	val default = PathEnvironmentVariableUtil.findInPath("julia")?.absolutePath
	if (default != null) return default
	val appPath = Paths.get(MAC_APPLICATIONS)
	val result = Files.list(appPath).filter { application ->
		"$application".contains("julia", true)
	}.findFirst().orElse(appPath)
	val folderAfterPath = "/Contents/Resources/julia/bin/julia"
	return "${result.toAbsolutePath()}$folderAfterPath"
}

fun appPathSpecify(exePath: String): String {
	return when {
		!SystemInfo.isMac || !exePath.endsWith(".app") -> exePath
		exePath.contains("JuliaPro") -> "$exePath/Contents/Resources/julia/Contents/Resources/julia/bin/julia"
		else /*julia.app*/ -> "$exePath/Contents/Resources/julia/bin/julia"
	}
}

fun findPathWindows() =
	PathEnvironmentVariableUtil.findInPath("julia.exe")?.absolutePath
		?: executeCommandToFindPath("where julia")

fun findPathLinux() =
	PathEnvironmentVariableUtil.findInPath("julia")?.absolutePath
		?: executeCommandToFindPath("whereis julia")

fun findOrCreate(baseDir: VirtualFile, dir: String, module: Module) =
	baseDir.findChild(dir) ?: baseDir.createChildDirectory(module, dir)

class JuliaGlobalSettings2(
	var globalUnicodeInput: Boolean = false,
	var allJuliaExePath: String = "",
	var packagesInfo: String = "",
	var markdownCssText: String = "")

class JuliaSettings(
	var importPath: String = "",
	var exePath: String = "",
	var basePath: String = "",
	var version: String = "",
	var unicodeEnabled: Boolean = true,
	var showEvalHint: Boolean = false,
	var maxCharacterToConvertToCompact: Int = 140,
	var tryEvaluateTimeLimit: Long = 2500L,
	var tryEvaluateTextLimit: Int = 320,
	var replPrompt: String = "julia> ") {
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

/**
 *
 * @param exePath String
 * @param timeLimit Long.
 * 				How much the `timeLimit` is not important,
 * 				it depends on what the time when process exited if the timeLimit is very huge.
 * @return String
 */
fun versionOf(exePath: String, timeLimit: Long = 3000L) =
	executeJulia(exePath, null, timeLimit, "--version")
		.first
		.firstOrNull { it.startsWith("julia version", true) }
		?.dropWhile { it.isLetter() || it.isWhitespace() }
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
	val versionSplit = version.substringBefore('-').split('.')
	val version2Split = version2.substringBefore('-').split('.')
	if (versionSplit.size < 3) {
		return if (version2Split.size == 3) -1 else 0
	} else {
		if (version2Split.size < 3) return 1
	}
	val (v0, v1, v2) = versionSplit
	val (v20, v21, v22) = version2Split
	return when {
		v0 != v20 -> v0.trim('+').toInt() - v20.trim('+').toInt()
		v1 != v21 -> v1.trim('+').toInt() - v21.trim('+').toInt()
		else -> v2.trim('+').toInt() - v22.trim('+').toInt()
	}
}

fun importPathOf(exePath: String, timeLimit: Long = 800L) =
	executeJulia(exePath, null, timeLimit, "--print", "Pkg.dir()")
		.first
		.lastOrNull()
		.orEmpty()
		.trim('"')

/**
 * if [exePath] is empty, Files.isExecutable still return true!
 */
fun validateJuliaExe(exePath: String) = try {
	if (exePath.isEmpty()) false
	else Files.isExecutable(Paths.get(exePath))
} catch (e: Exception) {
	false
}

fun validateJulia(settings: JuliaSettings) = validateJuliaExe(settings.exePath)

fun installDocumentFormat(
	project: Project, settings: JuliaSettings): ActionListener = ActionListener {
	ProgressManager.getInstance()
		.run(object : Task.Backgroundable(project, JuliaBundle.message("julia.messages.doc-format.installing"), true) {
			override fun run(indicator: ProgressIndicator) {
				indicator.text = JuliaBundle.message("julia.messages.doc-format.installing")
				executeJulia(settings.exePath, DOCFMT_INSTALL, 1000000L)
			}

			override fun onSuccess() = ApplicationManager.getApplication().invokeLater {
				Messages.showDialog(
					project,
					JuliaBundle.message("julia.messages.package.installed", DOCFMT_LANGUAGE_NAME),
					JuliaBundle.message("julia.messages.package.success"),
					arrayOf(JuliaBundle.message("julia.yes")),
					0,
					JuliaIcons.JOJO_ICON)
			}
		})
}

inline fun initExeComboBox(
	juliaExeField: ComboboxWithBrowseButton,
	project: Project? = null,
	crossinline addListener: (ComboboxWithBrowseButton) -> Unit = {}) {
	juliaExeField.addBrowseFolderListener(JuliaBundle.message("julia.messages.run.select-compiler"),
		JuliaBundle.message("julia.messages.run.select-compiler.description"),
		project,
		FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor(),
		object : TextComponentAccessor<JComboBox<Any>> {
			override fun getText(component: JComboBox<Any>) = component.selectedItem as? String ?: ""
			override fun setText(component: JComboBox<Any>, text: String) {
				val item = text.let(::appPathSpecify)
				if (!juliaGlobalSettings.knownJuliaExes.contains(item)) {
					component.addItem(item)
					component.selectedItem = item
				}
				addListener(juliaExeField)
			}
		})
	juliaGlobalSettings.knownJuliaExes.forEach(juliaExeField.comboBox::addItem)
}

/**
 * @see <a href="https://gist.github.com/DemkaAge/8999236">Reference</a>
 * @usage julia-infos.kt:
 * 				private val bundle: ResourceBundle by lazy { ResourceBundle.getBundle(BUNDLE,JuliaUTF8Control) }
 */
object JuliaUTF8Control : ResourceBundle.Control() {
	override fun newBundle(
		baseName: String, locale: Locale, format: String, loader: ClassLoader, reload: Boolean): ResourceBundle {
		val bundleName = toBundleName(baseName, locale)
		val connection = loader.getResource(toResourceName(bundleName, "properties"))?.openConnection()
		if (connection != null) {
			connection.useCaches = false
			val res = connection.getInputStream()?.use {
				PropertyResourceBundle(it.reader())
			}
			if (null != res) return res
		}
		return ResourceBundle.getBundle(baseName)
	}
}

// These code doesn't work at all when `apply` settings.
fun Project.syncJuliaLibrary() {
	val sdkTable = ProjectJdkTable.getInstance()
	val juliaSDK = JuliaSdkType.instance
	val oldSDK = sdkTable.findJdk(juliaSDK.presentableName)
	val newSDK = ProjectJdkImpl(juliaSDK.presentableName, juliaSDK)
	ApplicationManager.getApplication().runWriteAction {
		when {
			newSDK.homePath.isNullOrEmpty() -> return@runWriteAction
			oldSDK == null -> ProjectJdkTable.getInstance().addJdk(newSDK)
			else -> ProjectJdkTable.getInstance().updateJdk(oldSDK, newSDK)
		}
	}
}

fun Project.reloadSdkAndIndex() {
	if (withJulia) {
		syncJuliaLibrary()
	}
}