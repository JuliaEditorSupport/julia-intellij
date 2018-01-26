package org.ice1000.julia.lang

import org.junit.Test

class JuliaRegexTest{
	@Test
	fun testRegex() {
		val unicodeCharX="""\x23\x23\x23"""
		val unicodeSingleChar="""\x22"""
		val unicodeCharU="""\u2233"""
		println(Regex(JULIA_CHAR_TRIPLE_UNICODE_X_REGEX).matches(unicodeCharX))
		println(Regex(JULIA_CHAR_SINGLE_UNICODE_X_REGEX).matches(unicodeSingleChar))
		println(Regex(JULIA_CHAR_SINGLE_UNICODE_U_REGEX).matches(unicodeCharU))
	}
}