package org.ice1000.julia.lang

import com.intellij.openapi.util.TextRange
import org.ice1000.julia.lang.module.validateJuliaExe
import java.io.InputStream
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

inline fun forceRun(lambda: () -> Unit) {
	try {
		lambda()
	} catch (e: Throwable) {
	}
}

fun printJulia(
	exePath: String, timeLimit: Long = 2000L, expr: String) =
	executeJulia(exePath, null, timeLimit, "--print", expr)

/**
 * @param exePath the home path of the Julia SDK currently used
 * @param code doesn't need to `quit()`, because this function will automatically add one if code != null
 * @param timeLimit the time limit. Will wait for this long and kill process after 100 ms
 * @param params additional parameters to the Julia compiler
 * @return (stdout, stderr)
 */
fun executeJulia(
	exePath: String, code: String? = null, timeLimit: Long = 2000L, vararg params: String) =
	executeCommand(
		"$exePath ${params.joinToString(" ")}",
		code?.let { "$it\nquit()" },
		timeLimit)

fun executeCommandToFindPath(command: String) = executeCommand(command, null, 500L)
	.first
	.firstOrNull()
	?.split(' ')
	?.firstOrNull(::validateJuliaExe)
	?: System.getenv("PATH")
		.split(":")
		.firstOrNull(::validateJuliaExe)

fun executeCommand(
	command: String, input: String? = null, timeLimit: Long = 1200L): Pair<List<String>, List<String>> {
	var processRef: Process? = null
	var output: List<String> = emptyList()
	var outputErr: List<String> = emptyList()
	val executor = Executors.newCachedThreadPool()
	val future = executor.submit {
		val process: Process = Runtime.getRuntime().exec(command)
		processRef = process
		process.outputStream.use {
			if (input != null) it.write(input.toByteArray())
			it.flush()
		}
		process.waitFor()
		output = process.inputStream.use(::collectLines)
		outputErr = process.errorStream.use(::collectLines)
		forceRun(process::destroy)
	}
	try {
		future.get(timeLimit, TimeUnit.MILLISECONDS)
	} catch (ignored: Throwable) {
		// timeout? catch it and give up anyway
	} finally {
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

fun String.trimQuotePair() = trim('\'', '"', '`')

/**
 * its effect needs to profit.
 * it is stupid to map each char and compare indices whether in ListSet
 * @param someStr String
 */
fun String.indicesOf(someStr: String) = indices
	.map { indexOf(someStr, it) }
	.filter { it > -1 }

fun String.splitsOf(someStr: String, expandSize: Int) = indices
	.filter { substring(it).startsWith(someStr) }
	.map { substring(it, it + expandSize) }

fun Boolean.toYesNo() = if (this) "yes" else "no"
fun Boolean?.orFalse() = true == this
