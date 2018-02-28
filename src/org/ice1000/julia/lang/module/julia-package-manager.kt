package org.ice1000.julia.lang.module

import org.ice1000.julia.lang.executeCommandUntilResult
import org.intellij.lang.annotations.Language
import java.io.File

object JuliaPackageManagerInfoList {
	data class InfoData(val nameList: ArrayList<String>, val versionList: ArrayList<String>)
	val infoList = InfoData(arrayListOf(), arrayListOf())
}

object JuliaPackageManagerUtil {
	fun packagesList(): List<String> {
		@Language("Julia")
		val code = """Pkg.dir()"""
		val stdout = executeCommandUntilResult(juliaPath, code, "\"")
		return stdout
			.firstOrNull()
			?.trim('"')
			?.toFile()
			?.listFiles()
			?.filter { it.isDirectory && !it.name.startsWith(".") && it.name != "METADATA" }
			?.map { it.name }
			?: emptyList()
	}

	fun versionsList(): Map<String, String> {
		@Language("Julia")
		val code = """Pkg.installed()"""
		val ret = executeCommandUntilResult(juliaPath, code, "\"", 30_000L)
		return ret.filter { "=>" in it }
			.map {
				it.replace("v\"", "")
					.replace("\"", "")
					.replace(" ", "")
					.split("=>")
			}.map { it[0] to it[1] }.toMap()
	}
}

fun String.toFile() = File(this)