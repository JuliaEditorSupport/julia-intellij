@file:JvmName("ModuleUtils")
@file:JvmMultifileClass

package org.ice1000.julia.lang.module

import org.ice1000.julia.lang.executeCommand
import org.ice1000.julia.lang.printJulia
import org.intellij.lang.annotations.Language
import java.io.File
import java.nio.file.*
import java.util.function.Predicate
import java.util.stream.Stream

data class InfoData(val name: String, val version: String, val latestVersion: String = "")

/**
 * It's needed for UE (User Experience)
 * @notice Do not use [File.list] (or [File.listFiles]) with 2 parameter filter
 *         because the first param will be `let(::File)`'s dir
 */
fun packageNamesList(importPathNullable: String? = null): Stream<String> {
	@Language("Julia")
	val importPath = importPathNullable
		?: printJulia(juliaPath, timeLimit = 5000L, expr = "Pkg.dir()")
			.first
			.firstOrNull()
			.let { it ?: return Stream.empty() }
			.trim('"')
	val path = try {
		Paths.get(importPath)
	} catch (e: NoSuchFileException) {
		return Stream.empty()
	}
	return Files
		.list(path)
		.filter(packagePredicate)
		.map { it.fileName.toString() }
}

private val packagePredicate = Predicate { dir: Path ->
	!dir.fileName.toString().let { it.startsWith(".") || it == "METADATA" } and
		Files.isDirectory(dir)
}

/**
 * very slow.
 */
@Language("Julia")
fun versionsList(settings: JuliaSettings) =
	executeCommand(settings.exePath, "Pkg.installed()", 20_000L)
		.first
		.filter { "=>" in it }
		.sorted()
		.map {
			//language=RegExp
			val (name, version) = it.split("=>")
			name.trim(' ', '"') to version.trim(' ', '"').removePrefix("v\"")
		}

fun loadNamesListByEnvFile(settings: JuliaSettings, envdir: String): List<String> = run {
	val versionDir = "v" + settings.version.substringBeforeLast(".")
	val projectTomlFile = Paths.get(envdir, versionDir, "Project.toml").toFile()
	if (!projectTomlFile.exists()) return emptyList()
	projectTomlFile.readLines().mapNotNull { if (it.first() == '[') null else it.substringBefore(' ') }.sorted()
}

fun getEnvDir(settings: JuliaSettings): String {
	//language=Julia
	return executeCommand(settings.exePath, "using Pkg\nPkg.envdir()\nexit()", timeLimit = 4000L)
		.first
		.firstOrNull()
		.let { it ?: "" }
		.trim('"')
}