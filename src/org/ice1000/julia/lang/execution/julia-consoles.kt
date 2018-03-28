package org.ice1000.julia.lang.execution

import com.intellij.execution.ConsoleFolding
import com.intellij.execution.filters.*
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import org.ice1000.julia.lang.JULIA_ERROR_FILE_LOCATION_REGEX
import org.ice1000.julia.lang.JULIA_STACK_FRAME_LOCATION_REGEX
import org.ice1000.julia.lang.module.juliaSettings
import java.nio.file.Paths
import java.util.regex.Pattern


/**
Stack trace example:
[1] include_from_node1(::String) at ./loading.jl:576
...
while loading /home/ice1000/git-repos/big-projects/cov/cov-plugin-test/src/a.jl, in expression starting on line 8
 * Console Linkenizing
 * @author ice1000
 */
class JuliaConsoleFilter(private val project: Project) : Filter {
	private val sdkHomeCache = project.juliaSettings.settings.basePath

	private companion object PatternHolder {
		private val STACK_FRAME_LOCATION = Pattern.compile(JULIA_STACK_FRAME_LOCATION_REGEX)
		private val ERROR_FILE_LOCATION = Pattern.compile(JULIA_ERROR_FILE_LOCATION_REGEX)
	}

	// Filter.Result(startPoint, entireLength, null)
	override fun applyFilter(line: String, entireLength: Int): Filter.Result? {
		if (project.isDisposed) return null
		val startPoint = entireLength - line.length
		val fileSystem = project.baseDir.fileSystem
		val matcher1 = STACK_FRAME_LOCATION.matcher(line)
		if (matcher1.find()) {
			val (path, lineNumber) = matcher1.group().drop(3).split(':') // "at ".length
			val resultFile = fileSystem.findFileByPath(path)
				?: fileSystem.findFileByPath(Paths.get(sdkHomeCache, path.trim('.', '/')).toString())
				?: return null
			return Filter.Result(
				startPoint + matcher1.start() + 3,
				startPoint + matcher1.end(),
				OpenFileHyperlinkInfo(project, resultFile, lineNumber.toInt()))
		}
		val matcher2 = ERROR_FILE_LOCATION.matcher(line)
		if (matcher2.find()) {
			val resultFile = fileSystem.findFileByPath(matcher2.group().dropLast(1))
				?: return null
			val lineNumber = line.split(' ').lastOrNull()?.trim()?.toIntOrNull()
				?: return null
			return Filter.Result(
				startPoint + matcher2.start(),
				startPoint + matcher2.end() - 1,
				OpenFileHyperlinkInfo(project, resultFile, lineNumber - 1))
		}
		return null
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
 * `/PATH-TO-JULIA_HOME/bin/julia --COMMAND_PARAMS /PATH-TO-SOURCE/sourceCode.jl`
 * @author zxj5470
 * @date 2018/01/29
 *
 * @update 2018/02/11
 * fold Julia interpreter Stacktrace which is useless.
 */
class JuliaConsoleFolding : ConsoleFolding() {
	override fun getPlaceholderText(lines: MutableList<String>): String {
		lines.forEach {
			when {
				it.matchExecCommand() ->
					return "julia ${it.substring(it.lastIndexOf("/") + 1)}"
				it.matchErrorStackTrace() ->
					return " <${lines.size} stace frames>"
			}
		}
		return ""
	}

	override fun shouldFoldLine(output: String) =
		output.matchExecCommand() or output.matchErrorStackTrace()

	private fun String.matchExecCommand() = "julia" in this &&
		".jl" in this &&
		"--check-bounds" in this

	private fun String.matchErrorStackTrace() = "loading.jl:" in this ||
		"sysimg.jl:" in this ||
		"client.jl:" in this
}
