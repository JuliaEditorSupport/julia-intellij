package org.ice1000.julia.lang.module

import org.ice1000.julia.lang.executeCommand

data class InfoData(val name: String, val version: String)

object JuliaPackageManagerInfoList {
	val infos = emptyList<InfoData>().toMutableList()
}

/**
 * very slow.
 */
fun versionsList(settings: JuliaSettings) =
	executeCommand(settings.exePath, "Pkg.installed()", 20_000L)
		.first
		.filter { "=>" in it }
		.map {
			//language=RegExp
			val (name, version) = it.split("=>")
			name.trim(' ', '"') to version.trim(' ', '"').removePrefix("v\"")
		}
