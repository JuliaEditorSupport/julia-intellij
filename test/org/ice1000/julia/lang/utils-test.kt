package org.ice1000.julia.lang

import org.ice1000.julia.lang.module.defaultExePath
import org.ice1000.julia.lang.module.versionOf
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths

class UtilsKtTest {
	@Test
	fun executeJuliaTest() {
		val (stdout, stderr) = executeJulia(defaultExePath, null, 1000L, "--print",
			"""1+1
				|2+2
			""".trimMargin())
		println("stdout:")
		stdout.forEach(::println)
		println("stderr:")
		stderr.forEach(::println)
	}

	@Test
	fun versionTest() {
		println(versionOf(defaultExePath))
	}

	@Test
	fun juliaTest() = Runtime
		.getRuntime()
		.exec("$defaultExePath --version")
		.inputStream
		.bufferedReader()
		.readLine()
		.let(::println)

	@Test
	fun whereIsJulia() = executeCommand("whereis julia", null, 1000)
		.first
		.forEach(::println)

	@Test
	fun whereExactlyIsJulia() = System.getenv("PATH")
		.split(":")
		.also { it.forEach(::println) }
		.firstOrNull { Files.isExecutable(Paths.get(it, "julia")) }
		?.let { Paths.get(it).parent.toAbsolutePath().toString() }
		.let(::println)
}
