package org.ice1000.julia.lang.module

import org.ice1000.julia.lang.executeCommand
import org.intellij.lang.annotations.Language

data class InfoData(val name: String, val version: String)

object JuliaPackageManagerInfoList {
	val infos = emptyList<InfoData>().toMutableList()
}

/**
 * very slow.
 */
fun versionsList(settings: JuliaSettings): List<Pair<String, String>> {
	@Language("Julia")
	val code = "Pkg.installed()"
	val (ret) = executeCommand(settings.exePath, code)
	return ret
		.filter { "=>" in it }
		.map {
			//language=RegExp
			val (name, version) = it.replace(Regex("v?\"|\\s"), "")
				.split("=>")
			name to version
		}
}
