package org.ice1000.julia.lang

import com.intellij.testFramework.ParsingTestCase
import org.ice1000.julia.lang.docfmt.DocfmtParserDefinition
import org.junit.Test

//class JuliaExperimentalParsingTest : ParsingTestCase(
//	"", JULIA_EXTENSION, JuliaParserDefinitionExperimental()) {
//	override fun getTestDataPath() = "testData/experimental"
//	override fun skipSpaces() = true
//	fun testComment() {
//		doTest(true)
//	}
//
//	fun testBlockComment() {
//		doTest(true)
//	}
//}

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

	fun testParseEnd() {
		println(name)
		doTest(true)
	}

	fun testParseJuliac() {
		println(name)
		doTest(true)
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

	fun testParseIssue135() {
		println(name)
		doTest(true)
	}

	fun testComment() {
		println("我永远喜欢结城明日奈")
		doTest(true)
	}

	fun testVersionAndRaw() {
		println("我永远喜欢时崎狂三")
		doTest(true)
	}

	fun testlearn_julia_in_Y_minutes() {
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
