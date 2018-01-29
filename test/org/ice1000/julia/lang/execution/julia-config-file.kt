package org.ice1000.julia.lang.execution

import com.intellij.openapi.util.io.FileUtilRt
import org.junit.Test

class JuliaConfig {
	@Test
	fun testFile() {
		println(FileUtilRt.getNameWithoutExtension("/home/zh/IdeaProjects/JuliaTest/src/lk.jl"))
	}
}