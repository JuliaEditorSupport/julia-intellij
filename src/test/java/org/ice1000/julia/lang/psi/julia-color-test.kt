package org.ice1000.julia.lang.psi

import org.ice1000.julia.lang.shouldBe
import org.junit.Test
import java.awt.Color

class JuliaColorTest {
	@Test
	fun testColor() {
		val c = JuliaFileElementColorProvider
		val aqua = c.parseColor("aqua")
		aqua shouldBe Color(0, 255, 255)

		val red = c.parseColor("#FF0000")
		red shouldBe Color(255, 0, 0)

		val red2 = c.parseColor("#F00")
		red2 shouldBe Color(255, 0, 0)

		// should start with `#`
		val nullValue = c.parseColor("233")
		nullValue shouldBe null
	}
}