package org.ice1000.julia.lang

import com.google.common.util.concurrent.SimpleTimeLimiter
import com.intellij.openapi.util.TextRange
import com.intellij.util.containers.ArrayListSet
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

inline fun forceRun(lambda: () -> Any) {
	try {
		lambda()
	} catch (e: Throwable) {
	}
}

/**
 * @param homePath the home path of the Julia SDK currently used
 * @param code doesn't need to `quit()`, because this function will automatically add one if code != null
 * @param timeLimit the time limit. Will wait for this long and kill process after 100 ms
 * @param params additional parameters to the Julia compiler
 * @return (stdout, stderr)
 */
fun executeJulia(homePath: String, code: String?, timeLimit: Long, vararg params: String) =
	executeCommand(
		"${Paths.get(homePath, "bin", "julia").toAbsolutePath()} ${params.joinToString(" ")}",
		code?.let { "$it\nquit()" },
		timeLimit
	)

fun executeCommandToFindPath(command:String)= executeCommand(command, null, 500L)
	.first
	.firstOrNull()
	?.split(' ')
	?.firstOrNull { Files.isExecutable(Paths.get(it)) }
	?.let { Paths.get(it).parent.parent.toAbsolutePath().toString() }
	?: System.getenv("PATH")
		.split(":")
		.firstOrNull { Files.isExecutable(Paths.get(it, "julia")) }
		?.let { Paths.get(it).parent.toAbsolutePath().toString() }

fun executeCommand(
	command: String,
	input: String?,
	timeLimit: Long): Pair<List<String>, List<String>> {
	var processRef: Process? = null
	var output: List<String> = emptyList()
	var outputErr: List<String> = emptyList()
	try {
		SimpleTimeLimiter().callWithTimeout({
			val process: Process = Runtime.getRuntime().exec(command)
			processRef = process
			if (input != null) process.outputStream.use {
				it.write(input.toByteArray())
				it.flush()
			}
			process.waitFor(timeLimit, TimeUnit.MILLISECONDS)
			output = process.inputStream.use(::collectLines)
			outputErr = process.errorStream.use(::collectLines)
			forceRun(process::destroy)
		}, timeLimit + 100, TimeUnit.MILLISECONDS, true)
	} catch (e: Throwable) {
		processRef?.destroy()
	}
	return output to outputErr
}

private fun collectLines(it: InputStream): List<String> {
	val reader = it.bufferedReader()
	val ret = reader.lines().collect(Collectors.toList())
	forceRun(reader::close)
	return ret
}

fun TextRange.narrow(fromStart: Int, toEnd: Int) = TextRange(startOffset + fromStart, endOffset - toEnd)
fun TextRange.subRangeBeginOffsetAndLength(
	beginOffset: Int,
	textLength: Int) = TextRange(startOffset + beginOffset, startOffset + beginOffset + textLength)

fun String.trimQuotePair() = trim('\'', '\"')

/**
 * TODO
 * its effect needs to profit.
 * it is stupid to map each char and compare indices whether in ListSet
 * @param someStr String:
 */
fun String.indicesOf(someStr: String) = indices
	.map { indexOf(someStr, it) }
	.filterTo(ArrayListSet()) { it > -1 }.toIntArray()

fun String.splitsOf(someStr: String, expandSize: Int): Array<String> {
	val ret = ArrayList<String>()
	indices.forEach { i ->
		if (substring(i).startsWith(someStr))
			ret.add(substring(i, i + expandSize))
	}
	return ret.toTypedArray()
}

fun Boolean.toYesNo() = if (this) "yes" else "no"
