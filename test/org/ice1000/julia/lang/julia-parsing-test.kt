package org.ice1000.julia.lang

import com.intellij.testFramework.ParsingTestCase
import org.ice1000.julia.lang.docfmt.DocfmtParserDefinition
import org.junit.Test

class JuliaParsingTest : ParsingTestCase("", JULIA_EXTENSION, JuliaParserDefinition()) {
	override fun getTestDataPath() = "testData"
	override fun skipSpaces() = true
	fun testParsing0() {
		println(name)
		doTest(true)
	}

	fun testParsing1() {
		println(name)
		doTest(true)
	}

	fun testParseFunctions() {
		println(name)
		doTest(true)
	}

	fun testParseJuliac() {
		println(name)
		doTest(false)
	}

	fun testRegex() {
		println(name)
		doTest(true)
	}

	fun testParseFor() {
		println(name)
		doTest(true)
	}

	fun testParseImport() {
		println(name)
		doTest(true)
	}
	fun testParseIssue135(){
		println(name)
		doTest(true)
	}
}

class JuliaLexerTest {
	@Test
	fun test0() {
		JuliaLexer().let {
			// Star platinum the world!
		}
	}
}

class DocfmtParsingTest : ParsingTestCase("", DOCFMT_EXTENSION, DocfmtParserDefinition()) {
	override fun getTestDataPath() = "testData"
	override fun skipSpaces() = true
	fun test() {
		doTest(true)
	}
}
