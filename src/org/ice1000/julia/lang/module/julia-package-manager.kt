@file:JvmName("ModuleUtils")
@file:JvmMultifileClass

package org.ice1000.julia.lang.module

import org.ice1000.julia.lang.executeCommand
import java.io.File
import java.nio.file.*
import java.util.function.Predicate
import java.util.stream.Stream

data class InfoData(val name: String, val version: String, val latestVersion: String = "")

val packageInfos = emptyList<InfoData>().toMutableList()

/**
 * It's needed for UE (User Experience)
 * @notice Do not use [File.list] (or [File.listFiles]) with 2 parameter filter
 *         because the first param will be `let(::File)`'s dir
 */
fun packageNamesList(importPathNullable: String? = null): Stream<out String> {
	val importPath = importPathNullable
		?: executeCommand(juliaPath, "Pkg.dir()", 5000L)
			.first
			.firstOrNull()
			.let { it ?: return Stream.empty() }
			.trim('"')
	return importPath
		.let { Files.list(Paths.get(it)) }
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

