package org.ice1000.julia.lang.execution

import org.ice1000.julia.lang.executeCommandUntilResult
import org.ice1000.julia.lang.module.JuliaPackageManagerUtil
import org.ice1000.julia.lang.module.juliaPath
import org.intellij.lang.annotations.Language
import org.junit.Test

class JuliaPackageTest {
	@Test
	fun testListPackages() {
		val list = JuliaPackageManagerUtil.packagesList()
		list.forEach(::println)
	}

	@Test
	fun testListPackageVersions() {
		val versionList = JuliaPackageManagerUtil.versionsList()
		println(versionList["URIParser"]?:"")
		println(versionList["TextWrap"]?:"")
	}
}