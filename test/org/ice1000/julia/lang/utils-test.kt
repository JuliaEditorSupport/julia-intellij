package org.ice1000.julia.lang

import org.ice1000.julia.lang.module.versionOf
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths

private const val SDK_HOME = "/home/ice1000/SDK/julia-6.2"

class UtilsKtTest {
	@Test
	fun executeJuliaTest() {
		val (stdout, stderr) = executeJulia(SDK_HOME, null, 1000L, "--print",
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
		println(versionOf(SDK_HOME))
	}

	@Test
	fun juliaTest() = Runtime
		.getRuntime()
		.exec("${Paths.get(SDK_HOME, "bin", "julia").toAbsolutePath()} --version")
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

	@Test
	fun runMeWhileYourNotRunningAnythingElse() {
		while (true) {
			val (stdout, stderr) = executeCommand("git pull", null, 1000)
			stdout.forEach(::println)
			stderr.forEach(::println)
		}
	}
}
