package org.ice1000.julia.lang.module

import org.ice1000.julia.lang.executeJulia
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @date: 2018/1/28
 *
 * zsh command `where`
 *
 * $ where julia
 * /Applications/Julia-0.6.app/Contents/Resources/julia/bin/julia
 * @author: zxj5470
 */

class JuliaSdkFindTest {
	@Test
	fun testFindPathMac() {
		println(findPathMac())
	}

	@Test
	fun testFindPathWindows() {
		println(findPathWindows())
	}

	@Test
	fun testFindImport() {
		val (stdout, _) = executeJulia(defaultExePath, null, 1000L, "--print", "Pkg.dir()")
		println(stdout)
		println(stdout.first())
		println(Files.exists(Paths.get(stdout.first())))
	}
}