@file:JvmName("ModuleUtils")
@file:JvmMultifileClass

package org.ice1000.julia.lang.module

import org.ice1000.julia.lang.executeCommand
import org.intellij.lang.annotations.Language
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Stream

data class InfoData(val name: String, val version: String, val latestVersion: String = "")

val packageInfos = emptyList<InfoData>().toMutableList()

/**
 * It's needed for UE (User Experience)
 * @notice Do not use [File.list] (or [File.listFiles]) with 2 parameter filter
 *         because the first param will be `let(::File)`'s dir
 */
fun packageNamesList(importPath: String? = null): Stream<out String> {
	if (null == importPath) {
		@Language("Julia")
		val code = "Pkg.dir()"
		val (stdout) = executeCommand(juliaPath, code, 5000L)
		return stdout
			.firstOrNull()
			.let { it ?: return Stream.empty() }
			.trim('"')
			.let { Files.list(Paths.get(it)) }
			.filter { dir ->
				!dir.fileName.toString().let { it.startsWith(".") && it != "METADATA" } and
					Files.isDirectory(dir)
			}
			.map { it.fileName.toString() }
	} else return importPath
		.let { Files.list(Paths.get(it)) }
		.filter { dir ->
			!dir.fileName.toString().let { it.startsWith(".") && it != "METADATA" } and
				Files.isDirectory(dir)
		}
		.map { it.fileName.toString() }
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

