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
	override fun getPlaceholderText(p0: MutableList<String>): String? {
		if (p0.size > 0) {
			val fileNameIndex = p0[0].lastIndexOf("/")
			return "julia ${p0[0].substring(fileNameIndex + 1)}"
		} else return ""
	}

	override fun shouldFoldLine(output: String) = output.contains("julia ") && output.endsWith(".jl")
}