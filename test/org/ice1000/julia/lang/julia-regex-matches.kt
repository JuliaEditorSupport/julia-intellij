package org.ice1000.julia.lang

import org.junit.Test

class JuliaRegexTest {
	@Test
	fun testCharRegex() {
		val unicodeCharX = """\x23\x23\x23"""
		val unicodeSingleChar = """\x22"""
		val unicodeCharU = """\u2233"""
		println(Regex(JULIA_CHAR_TRIPLE_UNICODE_X_REGEX).matches(unicodeCharX))
		println(Regex(JULIA_CHAR_SINGLE_UNICODE_X_REGEX).matches(unicodeSingleChar))
		println(Regex(JULIA_CHAR_SINGLE_UNICODE_U_REGEX).matches(unicodeCharU))
	}

	@Test
	fun testStringRegexEscapeSingleX() {
		var str = """"\x23发\x2233鬼地方\xqa""""
		str = str.trimQuotePair()
		str.indicesOf("\\x").forEach {
			if (it + 4 < str.length) {
				val s = str.subSequence(it, it + 4)
				if (s.matches(Regex(JULIA_CHAR_SINGLE_UNICODE_X_REGEX))) {
					println(s)
				} else {
					println("error in:$s")
				}
			} else {
				println("error in:${str.substring(it)}")
			}
		}
	}

	@Test
	fun testStringRegexEscapeU() {
		var str = """"sfdd\u2312\u12asadsf\213\uadss""""
		str = str.trimQuotePair()
		val size = str.indicesOf("\\u").size
		println(size)
		str.indicesOf("\\u").forEach {
			if (it + 6 < str.length) {
				val s = str.subSequence(it, it + 6)
				if (s.matches(Regex(JULIA_CHAR_SINGLE_UNICODE_U_REGEX))) {
					println(s)
				} else {
					println("error in:$s")
				}
			} else {
				println("error in:${str.substring(it)}")
			}
		}
	}

	@Test
	fun juliaComparisonSymbolRegexGenerator() {
		println(("> < >= ≥ <= ≤ == === ≡ != ≠ !== ≢ ∈ ∉ ∋ ∌ ⊆ ⊈ ⊂ ⊄ ⊊ ∝ ∊ ∍ ∥ " +
				"∦ ∷ ∺ ∻ ∽ ∾ ≁ ≃ ≄ ≅ ≆ ≇ ≈ ≉ ≊ ≋ ≌ ≍ ≎ ≐ ≑ ≒ ≓ ≔ ≕ ≖ ≗ ≘ ≙ ≚ ≛ ≜ ≝" +
				" ≞ ≟ ≣ ≦ ≧ ≨ ≩ ≪ ≫ ≬ ≭ ≮ ≯ ≰ ≱ ≲ ≳ ≴ ≵ ≶ ≷ ≸ ≹ ≺ ≻ ≼ ≽ ≾ ≿ ⊀ ⊁ ⊃ ⊅ ⊇" +
				" ⊉ ⊋ ⊏ ⊐ ⊑ ⊒ ⊜ ⊩ ⊬ ⊮ ⊰ ⊱ ⊲ ⊳ ⊴ ⊵ ⊶ ⊷ ⋍ ⋐ ⋑ ⋕ ⋖ ⋗ ⋘ ⋙ ⋚ ⋛ ⋜ ⋝" +
				" ⋞ ⋟ ⋠ ⋡ ⋢ ⋣ ⋤ ⋥ ⋦ ⋧ ⋨ ⋩ ⋪ ⋫ ⋬ ⋭ ⋲ ⋳ ⋴ ⋵ ⋶ ⋷ ⋸ ⋹ ⋺ ⋻ ⋼ ⋽ ⋾ ⋿ ⟈ ⟉" +
				" ⟒ ⦷ ⧀ ⧁ ⧡ ⧣ ⧤ ⧥ ⩦ ⩧ ⩪ ⩫ ⩬ ⩭ ⩮ ⩯ ⩰ ⩱ ⩲ ⩳ ⩴ ⩵ ⩶ ⩷ ⩸ ⩹ ⩺ ⩻ ⩼" +
				" ⩽ ⩾ ⩿ ⪀ ⪁ ⪂ ⪃ ⪄ ⪅ ⪆ ⪇ ⪈ ⪉ ⪊ ⪋ ⪌ ⪍ ⪎ ⪏ ⪐ ⪑ ⪒ ⪓ ⪔ ⪕ ⪖ ⪗ ⪘ ⪙ ⪚" +
				" ⪛ ⪜ ⪝ ⪞ ⪟ ⪠ ⪡ ⪢ ⪣ ⪤ ⪥ ⪦ ⪧ ⪨ ⪩ ⪪ ⪫ ⪬ ⪭ ⪮ ⪯ ⪰ ⪱ ⪲ ⪳ ⪴ ⪵ ⪶ ⪷ ⪸" +
				" ⪹ ⪺ ⪻ ⪼ ⪽ ⪾ ⪿ ⫀ ⫁ ⫂ ⫃ ⫄ ⫅ ⫆ ⫇ ⫈ ⫉ ⫊ ⫋ ⫌ ⫍ ⫎ ⫏ ⫐ ⫑ ⫒ ⫓ ⫔ ⫕ ⫖ ⫗ ⫘ ⫙ ⫷ ⫸ ⫹ ⫺ ⊢ ⊣ ⟂"
				).replace(" ",  "|"))
		println(("∈|∉|∋|∌|⊆|⊈|⊂|⊄|⊊|∝|∊|∍|∥|∦|∷|∺|∻|∽|∾|≁|≃|≄|≅|≆|≇|≈|≉|≊|≋|≌|≍|≎|≐|≑" +
				"|≒|≓|≔|≕|≖|≗|≘|≙|≚|≛|≜|≝|≞|≟|≣|≦|≧|≨|≩|≪|≫|≬|≭|≮|≯|≰|≱|≲|≳|≴|≵|≶|≷|≸|≹" +
				"|≺|≻|≼|≽|≾|≿|⊀|⊁|⊃|⊅|⊇|⊉|⊋|⊏|⊐|⊑|⊒|⊜|⊩|⊬|⊮|⊰|⊱|⊲|⊳|⊴|⊵|⊶|⊷|⋍|⋐|⋑|⋕" +
				"|⋖|⋗|⋘|⋙|⋚|⋛|⋜|⋝|⋞|⋟|⋠|⋡|⋢|⋣|⋤|⋥|⋦|⋧|⋨|⋩|⋪|⋫|⋬|⋭|⋲|⋳|⋴|⋵|⋶|⋷|⋸" +
				"|⋹|⋺|⋻|⋼|⋽|⋾|⋿|⟈|⟉|⟒|⦷|⧀|⧁|⧡|⧣|⧤|⧥|⩦|⩧|⩪|⩫|⩬|⩭|⩮|⩯|⩰|⩱|⩲|⩳|⩴|⩵" +
				"|⩶|⩷|⩸|⩹|⩺|⩻|⩼|⩽|⩾|⩿|⪀|⪁|⪂|⪃|⪄|⪅|⪆|⪇|⪈|⪉|⪊|⪋|⪌|⪍|⪎|⪏|⪐|⪑|⪒|⪓|⪔" +
				"|⪕|⪖|⪗|⪘|⪙|⪚|⪛|⪜|⪝|⪞|⪟|⪠|⪡|⪢|⪣|⪤|⪥|⪦|⪧|⪨|⪩|⪪|⪫|⪬|⪭|⪮|⪯|⪰|⪱|⪲|" +
				"⪳|⪴|⪵|⪶|⪷|⪸|⪹|⪺|⪻|⪼|⪽|⪾|⪿|⫀|⫁|⫂|⫃|⫄|⫅|⫆|⫇|⫈|⫉|⫊|⫋|⫌|⫍|⫎|⫏|⫐|⫑|⫒|⫓|⫔|⫕|⫖|⫗|⫘|⫙|⫷|⫸|⫹|⫺|⊢|⊣|⟂"
				).replace("|", ""))
	}
}