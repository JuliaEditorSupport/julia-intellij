package org.ice1000.julia.lang.module

import org.ice1000.julia.lang.executeCommand
import org.intellij.lang.annotations.Language
import java.io.File


data class InfoData(val name: String, val version: String, val latestVersion: String = "")

val packageInfos = emptyList<InfoData>().toMutableList()

/**
 * It's needed for UE (User Experience)
 * @notice Do not use list(or listFiles) with 2 parameter filter because the first param will be `let(::File)`'s dir
 */
fun packageNamesList(importPath: String = ""): Array<out String> {
	if (importPath.isBlank()) {
		@Language("Julia")
		val code = "Pkg.dir()"
		val (stdout) = executeCommand(juliaPath, code, 5000L)
		return stdout
			.firstOrNull()
			?.trim('"')
			?.let(::File)
			?.listFiles { dir -> dir.isDirectory && !dir.name.startsWith(".") && dir.name != "METADATA" }
			?.map { it.name.toString() }
			?.toTypedArray()
			.orEmpty()
	} else {
		return importPath
			.let(::File)
			.listFiles { dir -> dir.isDirectory && !dir.name.startsWith(".") && dir.name != "METADATA" }
			?.map { it.name.toString() }
			?.toTypedArray()
			.orEmpty()
	}
}

/**
 * very slow.
 */
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

