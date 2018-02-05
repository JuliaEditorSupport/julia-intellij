package org.ice1000.julia.lang.execution

import com.google.common.io.Files
import com.intellij.openapi.util.io.FileUtilRt
import org.ice1000.julia.lang.shouldBe
import org.junit.Test
import java.util.concurrent.TimeUnit

class JuliaExecutionTest {
	@Test
	fun test() {
		if (System.getProperty("user.name") != "ice1000") return
		val process = Runtime.getRuntime().exec("/home/ice1000/SDK/julia-6.2/bin/julia -q").also {
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
}

class JuliaConfig {
	@Test
	fun testFile() {
		println(FileUtilRt.getNameWithoutExtension("/home/zh/IdeaProjects/JuliaTest/src/lk.jl"))
		println(Files.getNameWithoutExtension("/home/zh/IdeaProjects/JuliaTest/src/lk.jl"))
	}
}

/**
 * @author: zxj5470
 * @date: 2018/1/31
 */
class JuliaConsoleTest {
	@Test
	fun testShouldFolding() {
		val command = "/Applications/Julia-0.6.app/Contents/Resources/julia/bin/julia --check-bounds=no --history-file=no --inline=no --color=no --math-mode=ieee --handle-signals=no --startup-file=no --optimize=0 --compile=yes -q /Users/paul/IdeaProjects/julia-project-test2/src/Jul.jl 23 f jh"
		JuliaConsoleFolding().shouldFoldLine(command) shouldBe true
	}
}