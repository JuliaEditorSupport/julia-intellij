package org.ice1000.julia.lang.execution

import org.ice1000.julia.lang.getFileName
import org.junit.Test

class JuliaConfig{
	@Test
	fun testFile(){
		println("/home/zh/IdeaProjects/JuliaTest/src/lk.jl".getFileName())
	}
}