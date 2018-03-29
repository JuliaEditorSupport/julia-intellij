package org.ice1000.julia.lang.execution

import com.google.common.io.Files
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.io.FileUtilRt
import org.ice1000.julia.lang.executeCommand
import org.ice1000.julia.lang.module.juliaPath
import org.ice1000.julia.lang.module.versionOf
import org.ice1000.julia.lang.shouldBe
import org.junit.Test
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis
import kotlin.test.assertTrue

private fun main(args: Array<String>) {
	val process = Runtime.getRuntime().exec("$juliaPath -q").also {
		//language=Julia
		it.outputStream.let {
			it.write("using DocumentFormat: format\n".toByteArray())
			it.flush()
		}
	}
	println("0")
	//language=Julia
	process.outputStream.let {
		it.write("println(\"Bye, world!\")\n".toByteArray())
		it.flush()
	}
	process.waitFor(5, TimeUnit.SECONDS)
	println("1")
	val reader = process.inputStream.bufferedReader()
	reader.readLine().let(::println)
	println("2")
	//language=Julia
	process.outputStream.let {
		it.write("println(\"Bye, world again!\")\n".toByteArray())
		it.flush()
	}
	process.waitFor(5, TimeUnit.SECONDS)
	println("1")
	reader.readText().let(::println)
	println("2")
}

class ExecutionTest {
	@Test
	fun testTerminate() {
		println(executeCommand("git status", timeLimit = 10000000L))
	}

	@Test
	fun testVersion() {
		if (System.getenv("CI").isNullOrBlank())
			println(versionOf(juliaPath))
	}

	/**
	 * Must be longer than 500ms, shorter than 5000ms
	 * Or test will fail
	 */
	@Test(timeout = 5000L)
	fun testTimeout() {
		measureTimeMillis {
			// just test if it will throw exceptions
			executeCommand("git clone https://github.com/jetbrains/intellij-community", timeLimit = 500L)
		}.let {
			println(it)
		}
	}
}

class JuliaExecutionTest {
	@Test
	fun testDocker() {
		//Windows
		if (!System.getenv("CI").isNullOrBlank()) return
		if (!SystemInfo.isWindows) return
		val winCD = "%CD%"
		val unixPWD = "\$PWD"
		val currentDir = if (SystemInfo.isWindows) winCD else unixPWD
		//windows command need not `-it`
		//currentDir in @Test is Project Root
		val juliaScriptFile = "ParseFunctions.jl"
		val params = ""
		val cmd = "cmd /c docker run --rm -v \"$currentDir/testData\":/usr/myapp -w /usr/myapp julia julia $juliaScriptFile $params"
		val process = Runtime.getRuntime().exec(cmd)
		println(process.inputStream.reader().readText())
		println("----error----")
		println(process.errorStream.reader().readText())
	}

	@Test
	fun testUnixDocker() {
		val pwd = File(".").absolutePath
		val juliaFile = "ParseFunctions.jl"
		val containerName = "julia"
		val exeName = "julia"
		val params = ""
		val cmd = "docker run --rm -v $pwd/testData:$pwd/testData -w $pwd/testData $containerName $exeName $juliaFile $params"
		try {
			val process = Runtime.getRuntime().exec(cmd)
			println(process.inputStream.reader().readText())
			System.err.println(process.errorStream.reader().readText())
		} catch (e: IOException) {
			println("Test system doesn't have docker, skip")
		}
	}

}

/**
 * Linux Deepin
 * @author zxj5470
 */
class JuliaConfig {
	@Test
	fun testFile() {
		FileUtilRt.getNameWithoutExtension("/home/zh/IdeaProjects/JuliaTest/src/lk.jl") shouldBe
			"/home/zh/IdeaProjects/JuliaTest/src/lk"
		Files.getNameWithoutExtension("/home/zh/IdeaProjects/JuliaTest/src/lk.jl") shouldBe "lk"
	}
}

/**
 * Mac OS X
 * @author zxj5470
 * @date 2018/1/31
 */
class JuliaConsoleTest {
	@Test
	fun testShouldFolding() {
		if (!System.getenv("CI").isNullOrBlank()) return
		if (!SystemInfo.isMac) return
		val command = "/Applications/Julia-0.6.app/Contents/Resources/julia/bin/julia --check-bounds=no --history-file=no --inline=no --color=no --math-mode=ieee --handle-signals=no --startup-file=no --optimize=0 --compile=yes -q /Users/paul/IdeaProjects/julia-project-test2/src/Jul.jl 23 f jh"
		JuliaConsoleFolding().shouldFoldLine(command) shouldBe true
	}
}
