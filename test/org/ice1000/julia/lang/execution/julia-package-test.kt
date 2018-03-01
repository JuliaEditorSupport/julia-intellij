package org.ice1000.julia.lang.execution

import org.ice1000.julia.lang.executeCommand
import org.ice1000.julia.lang.module.*
import org.intellij.lang.annotations.Language
import org.junit.Test
import java.io.File
import java.util.concurrent.TimeUnit

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
	fun testListVersionFaster(){
		packageNamesList("C:\\Users\\zh\\.julia\\v0.6").forEach{
			val process=Runtime.getRuntime().exec("git describe --abbrev=0 --tags", emptyArray(),"C:\\Users\\zh\\.julia\\v0.6\\$it".toFile())
			println(process.inputStream.reader().readText().removePrefix("v").trim())
		}
	}

	@Test
	fun testListPackageVersions() {
		if (!System.getenv("CI").isNullOrBlank()) return
		versionsList(JuliaSettings()).forEach(::println)
	}
}

private fun String.toFile(): File=File(this)
