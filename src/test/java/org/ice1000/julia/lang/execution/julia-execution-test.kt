/*
 *     Julia language support plugin for Intellij-based IDEs.
 *     Copyright (C) 2024 julia-intellij contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.ice1000.julia.lang.execution

import com.google.common.io.Files
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.io.FileUtilRt
import org.ice1000.julia.lang.executeCommand
import org.ice1000.julia.lang.module.juliaPath
import org.ice1000.julia.lang.module.versionOf
import org.ice1000.julia.lang.shouldBe
import org.junit.Assume.assumeFalse
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis


private fun assumeMac() = assumeTrue("Mac-only test", SystemInfo.isMac)
private fun assumeNonCI() = assumeTrue("Non CI test", System.getenv("CI").isNullOrBlank())
private fun assumeNotWindows() = assumeFalse("Non-windows-only test", SystemInfo.isWindows)
private fun assumeWindows() = assumeTrue("Windows-only test", SystemInfo.isWindows)

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
	fun testVersion2() {
		println(versionOf("julia", timeLimit = 23_333L))
	}

	@Test
	fun testTerminate() {
		println(executeCommand("git status", timeLimit = 10000000L))
	}

	@Test
	fun testVersion() {
		println(versionOf(juliaPath))
	}

	/**
	 * Must be longer than 500ms, shorter than 5000ms
	 * Or test will fail
	 */
	@Test(timeout = 5000L)
	fun testTimeout() {
		assumeNonCI()
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
		assumeNonCI()
		assumeWindows()
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
		assumeNonCI()
		assumeNotWindows()
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
		assumeNonCI()
		assumeMac()
		val command = "/Applications/Julia-0.6.app/Contents/Resources/julia/bin/julia --check-bounds=no --history-file=no --inline=no --color=no --math-mode=ieee --handle-signals=no --startup-file=no --optimize=0 --compile=yes -q /Users/paul/IdeaProjects/julia-project-test2/src/Jul.jl 23 f jh"
		JuliaConsoleFolding().shouldFoldLine(command) shouldBe true
	}
}

