package org.ice1000.julia.lang

import com.google.common.util.concurrent.SimpleTimeLimiter
import java.io.InputStream
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
fun executeJulia(
		homePath: String,
		code: String?,
		timeLimit: Long,
		vararg params: String): Pair<List<String>, List<String>> {
	var processRef: Process? = null
	var output: List<String> = emptyList()
	var outputErr: List<String> = emptyList()
	try {
		val path = Paths.get(homePath, "bin", "julia").toAbsolutePath().toString()
		SimpleTimeLimiter().callWithTimeout({
			val process: Process = Runtime.getRuntime().exec("$path${params.map { " $it" }}")
			processRef = process
			if (code != null) process.outputStream.use {
				it.write("$code\nquit()".toByteArray())
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
