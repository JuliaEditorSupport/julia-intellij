package org.ice1000.julia.lang.module

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.openapi.util.SystemInfo
import org.ice1000.julia.lang.executeJulia
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths

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
		if (SystemInfo.isMac) println(findPathMac())
	}

	@Test
	fun testFindPathWindows() {
		@Suppress("DEPRECATION")
		if (SystemInfo.isWindows) println(findPathWindows())
	}

	@Test
	fun testFindPathLinux() {
		@Suppress("DEPRECATION")
		if (SystemInfo.isLinux) println(findPathLinux())
	}

	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			val (stdout, _) = executeJulia(defaultExePath, null, 1000L, "--print", "Pkg.dir()")
			println(stdout)
			println(stdout.first())
			println(Files.exists(Paths.get(stdout.first())))
		}
	}

	@Test
	fun testYourFiles() {
		if(SystemInfo.isWindows)
		println(PathEnvironmentVariableUtil.findInPath("julia.exe"))
		//return true
		println(Files.isExecutable(Paths.get("")))
	}
}