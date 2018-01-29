package org.ice1000.julia.lang.module

import org.junit.Test

/**
 * @date: 2018/1/28
 *
 * zsh command `where`
 *
 * $ where julia
 * /Applications/Julia-0.6.app/Contents/Resources/julia/bin/julia
 * @author: zxj5470
 */

class JuliaSdkFindTest {
	@Test
	fun testFindPathMac() {
		println(findPathMac())
	}
}