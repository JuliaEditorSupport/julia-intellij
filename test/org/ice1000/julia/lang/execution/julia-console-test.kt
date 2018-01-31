package org.ice1000.julia.lang.execution

import org.ice1000.julia.lang.shouldBe
import org.junit.Test

/**
 * @author: zxj5470
 * @date: 2018/1/31
 */
class JuliaConsoleTest{
	@Test
	fun testShouldFolding(){
		val command = "/Applications/Julia-0.6.app/Contents/Resources/julia/bin/julia --check-bounds=no --history-file=no --inline=no --color=no --math-mode=ieee --handle-signals=no --startup-file=no --optimize=0 --compile=yes -q /Users/paul/IdeaProjects/julia-project-test2/src/Jul.jl 23 f jh"
		JuliaConsoleFolding().shouldFoldLine(command) shouldBe true
	}
}