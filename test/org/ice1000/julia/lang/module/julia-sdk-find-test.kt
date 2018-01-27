package org.ice1000.julia.lang.module

import org.ice1000.julia.lang.MAC_APPLICATIONS
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

/**
 * @date: 2018/1/28
 *
 * zsh command `where`
 *
 * $ where julia
 * /Applications/Julia-0.6.app/Contents/Resources/julia/bin/julia
 * @author: zxj5470
 */

class JuliaSdkFindTest{
	@Test
	fun testFindPathMac(){
		val JULIA_HOME=findPathMac()+"/Contents/Resources/julia/"
		println(JULIA_HOME)
	}

	private fun findPathMac(): String {
		val appPath = Paths.get(MAC_APPLICATIONS)
		val result = Files.list(appPath).collect(Collectors.toList()).firstOrNull { application ->
			application.toString().contains("julia", true)
		} ?: appPath
		return result.toAbsolutePath().toString()
	}
}