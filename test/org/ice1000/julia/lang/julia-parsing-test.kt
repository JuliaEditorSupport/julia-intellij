package org.ice1000.julia.lang

import com.intellij.testFramework.ParsingTestCase
import org.junit.Test

class JuliaParsingTest : ParsingTestCase("", JULIA_EXTENSION, JuliaParserDefinition()) {
	override fun getTestDataPath() = "testData"
	override fun skipSpaces() = true
	fun testParsing0() {
		println(name)
		doTest(true)
	}

	fun testParseFunctions() {
		println(name)
		doTest(true)
	}
}

class JuliaLexerTest {
	@Test
	fun test0() {

	}
}
