package org.ice1000.julia.lang.execution

import org.ice1000.julia.lang.executeJulia
import org.ice1000.julia.lang.module.juliaPath
import org.intellij.lang.annotations.Language
import org.junit.Test
import java.io.File

class JuliaPackageTest {
	@Test
	fun testListPackages() {
		@Language("Julia")
		val code = """Pkg.dir()"""
		val (stdout, stderr) = executeJulia(juliaPath, code, 5000)
		var path = stdout.firstOrNull()
		if (path != null) {
			path = path.substring(1, path.lastIndex)
			println(path)
			File(path).list().filter { !it.startsWith(".") }.forEach(::println)
		}
	}
}