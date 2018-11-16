package org.ice1000.julia.lang

import com.intellij.lang.ParserDefinition
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

class JuliaParsingTest : ParsingTestCase("", JULIA_EXTENSION,
		// To make the IDE happy
		JuliaParserDefinition() as ParserDefinition) {
	override fun getTestDataPath() = "testData/parsing"
	override fun skipSpaces() = true
	fun testParsing0() = doTest(true)
	fun testParsing1() = doTest(true)
	fun testParseFunctions() = doTest(true)
	fun testParseRegexString() = doTest(true)
	fun testParseIssue135() = doTest(true)
	fun testParseIssue188() = doTest(true)
	fun testParseIssue195() = doTest(true)
	fun testParseIssue204() = doTest(true)
	fun testParseIssue206() = doTest(true)
	fun testParseIssue207() = doTest(true)
	fun testParseIssue208() = doTest(true)
	fun testParseIssue212() = doTest(true)
	fun testParseIssue213() = doTest(true)
	fun testParseIssue215() = doTest(true)
	fun testParseIssue220() = doTest(true)
	fun testParseIssue223() = doTest(true)
	fun testParseIssue225() = doTest(true)
	fun testParseIssue227() = doTest(true)
	fun testParseIssue228() = doTest(true)
	fun testParseIssue232() = doTest(true)
	fun testParseIssue240() = doTest(true)
	fun testParseIssue250() = doTest(true)
	fun testParseEnd() = doTest(true)
	fun testParseEolAfterComma() = doTest(true)
	fun testParseEolAfterWhere() = doTest(true)
	fun testParseJuliac() = doTest(true)
	fun testRegex() = doTest(true)
	fun testParseFor() = doTest(true)
	fun testParseLet() = doTest(true)
	fun testParseEscapeInsideRegEx() = doTest(true)
	fun testParseImport() = doTest(true)
	fun testParseGlobal() = doTest(true)
	fun testParseCharEscape() = doTest(true)

	fun testComment() {
		println("我永远喜欢结城明日奈")
		doTest(true)
	}

	fun testVersionRawByteArray() {
		println("我永远喜欢时崎狂三")
		doTest(true)
	}

	fun testlearn_julia_in_Y_minutes() {

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

class DocfmtParsingTest : ParsingTestCase("", DOCFMT_EXTENSION,
		DocfmtParserDefinition() as ParserDefinition) {
	override fun getTestDataPath() = "testData/parsing"
	override fun skipSpaces() = true
	fun test() {
		doTest(true)
	}
}
