package org.ice1000.julia.lang

import com.intellij.testFramework.ParsingTestCase

class JuliaParsingTest : ParsingTestCase("", JULIA_EXTENSION, JuliaParserDefinition()) {
	override fun getTestDataPath() = "/home/ice1000/git-repos/julia-intellij/testData"
	override fun skipSpaces() = true
	fun testParsing0() {
		println(name)
		doTest(true)
	}
}
