package org.ice1000.julia.lang.execution

import org.ice1000.julia.lang.executeCommand
import org.ice1000.julia.lang.module.*
import org.intellij.lang.annotations.Language
import org.junit.Test

class JuliaPackageTest {
	/**
	 * tears of the history.
	 * @author zxj5470
	 */
	private fun packagesList(): List<String> {
		@Language("Julia")
		val code = "Pkg.dir()"
		val (stdout) = executeCommand(juliaPath, code)
		return stdout
			.firstOrNull()
			?.trim('"')
			?.toFile()
			?.listFiles()
			?.filter { it.isDirectory && !it.name.startsWith(".") && it.name != "METADATA" }
			?.map { it.name }
			?: emptyList()
	}

	@Test
	fun testListPackages() {
		if (!System.getenv("CI").isNullOrBlank()) return
		packagesList().forEach(::println)
	}

	@Test
	fun testListPackageVersions() {
		if (!System.getenv("CI").isNullOrBlank()) return
		JuliaPackageManagerUtil.versionsList()
			.forEach(::println)
	}
}