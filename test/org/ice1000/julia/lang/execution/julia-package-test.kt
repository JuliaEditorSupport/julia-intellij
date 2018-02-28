package org.ice1000.julia.lang.execution

import org.ice1000.julia.lang.executeCommand
import org.ice1000.julia.lang.executeCommandUntilResult
import org.ice1000.julia.lang.executeJulia
import org.ice1000.julia.lang.module.defaultExePath
import org.ice1000.julia.lang.module.juliaPath
import org.intellij.lang.annotations.Language
import org.junit.Test
import java.io.File

class JuliaPackageTest {
	@Test
	fun testListPackages() {
		@Language("Julia")
		val code = """Pkg.dir()"""
		val (stdout, stderr) = executeJulia(juliaPath, code, 10000)
		var path = stdout.firstOrNull()
		if (path != null) {
			path = path.trim('"')
			File(path).list().filter { !it.startsWith(".") }.forEach(::println)
		}
	}

	@Test
	fun testListPackageVersions() {
		@Language("Julia")
		val code = """Pkg.installed()"""
//		val code = """Pkg.status()"""
		val ret = executeCommandUntilResult(juliaPath, code, "v\"")
		ret.forEach { println(it) }
	}
}