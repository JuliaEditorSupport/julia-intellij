package org.ice1000.julia.lang.module

import org.ice1000.julia.lang.executeCommand
import org.intellij.lang.annotations.Language
import java.io.File

data class InfoData(val name: String, val version: String)

object JuliaPackageManagerInfoList {
	val infos = emptyList<InfoData>().toMutableList()
}

/**
 * FIXME @zxj5470 convert to top-level function.
 */
object JuliaPackageManagerUtil {
	fun packagesList(): List<String> {
		@Language("Julia")
		val code = "Pkg.dir()"
		val (stdout) = executeCommand(juliaPath, code)
		return stdout
			.firstOrNull()
			?.trim('"')
			?.toFile()
			?.listFiles()
			?.filter { it.isDirectory && !it.name.startsWith(".") && it.name != "METADATA" }
			?.map { it.name }
			?: emptyList()
	}

	fun versionsList(): List<Pair<String, String>> {
		@Language("Julia")
		val code = "Pkg.installed()"
		val (ret) = executeCommand(juliaPath, code)
		return ret
			.filter { "=>" in it }
			.map {
				//language=RegExp
				val (name, version) = it.replace(Regex("v?\"|\\s"), "")
					.split("=>")
				name to version
			}
	}
}

fun String.toFile() = File(this)