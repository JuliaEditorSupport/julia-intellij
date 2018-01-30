package org.ice1000.julia.lang.execution

import com.intellij.execution.ConsoleFolding
import com.intellij.execution.filters.*
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import org.ice1000.julia.lang.JULIA_ERROR_FILE_LOCATION_REGEX
import org.ice1000.julia.lang.JULIA_STACK_FRAME_LOCATION_REGEX
import org.ice1000.julia.lang.module.projectSdk
import java.nio.file.Paths
import java.util.regex.Pattern


/**
 Stack trace example:
 [1] include_from_node1(::String) at ./loading.jl:576
...
 while loading /home/ice1000/git-repos/big-projects/cov/cov-plugin-test/src/a.jl, in expression starting on line 8
 * Console Linkenizing
 * @author: ice1000
 */
class JuliaConsoleFilter(private val project: Project) : Filter {
	private val sdkHomeCache = project.projectSdk?.homePath

	private companion object PatternHolder {
		private val STACK_FRAME_LOCATION = Pattern.compile(JULIA_STACK_FRAME_LOCATION_REGEX)
		private val ERROR_FILE_LOCATION = Pattern.compile(JULIA_ERROR_FILE_LOCATION_REGEX)
	}

	private fun default(startPoint: Int, entireLength: Int) = Filter.Result(startPoint, entireLength, null)
	override fun applyFilter(line: String, entireLength: Int): Filter.Result? {
		val startPoint = entireLength - line.length
		if (line.startsWith(" [")) {
			val matcher = STACK_FRAME_LOCATION.matcher(line)
			if (matcher.find()) {
				val (path, lineNumber) = matcher.group().drop(3).split(':') // "at ".length
				val sdkHome = sdkHomeCache ?: return null
				// TODO confirm
				val resultPath = Paths.get(sdkHome, "share", "julia", "base", path.trim('.', '/')).toAbsolutePath().toString()
				val resultFile = project.baseDir.fileSystem.findFileByPath(resultPath)
					?: return default(startPoint, entireLength)
				return Filter.Result(
					startPoint + matcher.start() + 3,
					startPoint + matcher.end(),
					OpenFileHyperlinkInfo(project, resultFile, lineNumber.toInt().let { if (it > 0) it - 1 else it }))
			}
		} else {
			val matcher = ERROR_FILE_LOCATION.matcher(line)
			if (matcher.find()) {
				val resultFile = project.baseDir.fileSystem.findFileByPath(matcher.group().dropLast(1))
					?: return default(startPoint, entireLength)
				val lineNumber = line.split(' ').lastOrNull()?.trim()?.toIntOrNull()
					?: return default(startPoint, entireLength)
				return Filter.Result(
					startPoint + matcher.start(),
					startPoint + matcher.end() - 1,
					OpenFileHyperlinkInfo(project, resultFile, lineNumber.let { if (it > 0) it - 1 else it }))
			}
		}
		return default(startPoint, entireLength)
	}
}

class JuliaConsoleFilterProvider : ConsoleFilterProviderEx {
	override fun getDefaultFilters(project: Project, scope: GlobalSearchScope) = getDefaultFilters(project)
	override fun getDefaultFilters(project: Project) = arrayOf(JuliaConsoleFilter(project))
}


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
