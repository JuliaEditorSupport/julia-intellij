package org.ice1000.julia.lang

import org.junit.Test
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * It aims to make a folder hint.
 */
class JuliaCharEscapeHintTest {
	@Test
	fun testSplitsOf(){
		println("""\xe\asa\s\s""".splitsOf("\\", 2).joinToString(","))
	}

	@Test
	fun testCharEscape() {
		testNull()
		testSucceed()
	}

	@Test
	fun testNull() {
		unicodeUToChar("") shouldBe null
	}

	@Test
	fun testSucceed() {
		val iceU = """\u51b0"""
		val iceX = """\xe5\x86\xb0"""
		val ice = '冰'
		unicodeUToChar(iceU) shouldBe ice
		unicodeXToChar(iceX) shouldBe ice
	}
}

fun unicodeUToChar(sourceStr: String): Char? {
	if (sourceStr.length != 6) return null
	return Integer.parseInt(sourceStr.substring(2), 16).toChar() // 16进制parse整形字符串。
}

//fun simpleEscapeChar(sourceStr: String): Char = sourceStr[1]

// TODO: effect needs to be enhanced
fun unicodeXToChar(sourceStr: String): Char? {
	println(sourceStr.length)
	val bytes = sourceStr.splitWithLength(4).mapTo(ArrayList()) { it.removePrefix("\\x").parseToInt().toByte() }.toByteArray()
	return String(bytes, StandardCharsets.UTF_8)[0]
}

// TODO: effect needs to be enhanced
fun String.splitWithLength(len: Int): Array<String> {
	val ret = mutableListOf<String>()
	var i = 0
	while (i + len < length) {
		ret.add(substring(i, i + len))
		i += len
	}
	ret.add(substring(i))
	return ret.toTypedArray()
}

fun String.parseToInt() = Integer.parseInt(this, 16)
infix fun Any?.shouldBe(other: Any?) = assert(this == other)
