package org.ice1000.julia.lang.execution

import com.intellij.util.SystemProperties
import org.ice1000.julia.lang.module.*
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
	fun testListVersionFaster() {
		when (SystemProperties.getUserName()) {
			"zh" -> packageNamesList("C:\\Users\\zh\\.julia\\v0.6").forEach {
				val process = Runtime.getRuntime().exec("git describe --abbrev=0 --tags", emptyArray(), "C:\\Users\\zh\\.julia\\v0.6\\$it".let(::File))
				println(process.inputStream.reader().readText().removePrefix("v").trim())
			}
			"ice1000" -> packageNamesList("/home/ice1000/.julia/v0.6").forEach {
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