package org.ice1000.julia.lang.execution

import com.intellij.util.SystemProperties
import org.ice1000.julia.lang.executeJulia
import org.ice1000.julia.lang.module.*
import org.ice1000.julia.lang.printJulia
import org.junit.Test
import java.io.File

class JuliaPackageTest {
	/**
	 * tears of the history.
	 * @author zxj5470
	 */

	@Test
	fun testListPackages() {
		if (!System.getenv("CI").isNullOrBlank()) return
		println(juliaPath)
		packageNamesList().forEach(::println)
	}

	@Test
	fun testCheckPackageInstallation() {
		if (!System.getenv("CI").isNullOrBlank()) return
		printJulia(juliaPath, expr = "Pkg.installed(\"DocumentFormat\")")
			.let(::println)
	}

	@Test
	fun testListVersionFaster() {
		if (!validateJuliaExe(juliaPath)) return
		when (SystemProperties.getUserName()) {
			"zh" -> packageNamesList(importPathOf(juliaPath)).forEach {
				val process = Runtime.getRuntime().exec("git describe --abbrev=0 --tags", emptyArray(), "C:\\Users\\zh\\.julia\\v0.6\\$it".let(::File))
				println(process.inputStream.reader().readText().removePrefix("v").trim())
			}
			"ice1000" -> packageNamesList(importPathOf(juliaPath)).forEach {
				println(it)
			}
		}
	}

	@Test
	fun testListPackageVersions() {
		if (!System.getenv("CI").isNullOrBlank()) return
		versionsList(JuliaSettings()).forEach(::println)
	}
}