package org.ice1000.julia.lang.module

import org.ice1000.julia.lang.executeCommand
import org.junit.Test

import org.junit.Assert.*

class JuliaSdkTypeTest {
	@Test
	fun suggestHomePath() {
		if (!System.getenv("CI").isNullOrBlank()) return
		println(executeCommand("ls", null, 100L).first)
		println(executeCommand("whereis julia", null, 500L).first)
		println(defaultExePath)
		println("" in arrayOf(""))
	}
}
