package org.ice1000.julia.lang.editing.hint

import com.intellij.codeInsight.hints.InlayInfo
import org.ice1000.julia.lang.executeJulia
import org.ice1000.julia.lang.module.juliaSettings
import org.ice1000.julia.lang.psi.JuliaExpr

fun providePropertyTypeHint(elem: JuliaExpr): List<InlayInfo> {
	val juliaExe = elem.project.juliaSettings.settings.exePath // 这不就是工程的Julia路径吗
	val output = buildString {
		val (stdout, stderr) = executeJulia(juliaExe, elem.text, 1000)
		if (stdout.isNotEmpty()) {
			append(" => ")
			stdout.forEach { append(it) }
		}
		if (stderr.isNotEmpty()) stderr.forEach { appendln(it) }
	}
	val text = if ("print" in elem.text) output.substringBefore("nothing") else output
	return listOf(InlayInfo(text, elem.textOffset + elem.text.length))
}

