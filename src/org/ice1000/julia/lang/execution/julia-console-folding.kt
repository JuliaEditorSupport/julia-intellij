package org.ice1000.julia.lang.execution

import com.intellij.execution.ConsoleFolding

/**
 * Console folding
 * You will see the console with
 * `julia *.jl` instead of
 * `/PATH-TO-JULIA_HOME/bin/julia --COMMANDS /PATH-TO-SOURCE/_____.jl`
 * @author: zxj5470
 * @date: 2018/1/29
 */
class JuliaConsoleFolding : ConsoleFolding() {
	override fun getPlaceholderText(lines: MutableList<String>): String {
		val fileNameIndex = lines.firstOrNull()?.lastIndexOf("/") ?: return ""
		return "julia ${lines[0].substring(fileNameIndex + 1)}"
	}

	override fun shouldFoldLine(output: String) = "julia " in output && output.endsWith(".jl")
}