package org.ice1000.julia.lang.editing.hint

import com.intellij.codeInsight.hints.InlayInfo
import com.intellij.psi.PsiElement
import org.ice1000.julia.lang.executeJulia
import org.ice1000.julia.lang.module.juliaSettings
import org.ice1000.julia.lang.psi.JuliaExpr

fun providePropertyTypeHint(elem: PsiElement): List<InlayInfo> {
	val text = if (elem is JuliaExpr) {
		try {
			val juliaExe = elem.project.juliaSettings.settings.exePath      //用工程的Julia路径会不会好点来着。。
			val output = buildString {
				val (stdout, stderr) = executeJulia(juliaExe, elem.text, 1000)
				if (stdout.isNotEmpty()) {
					append(" => ")
					stdout.forEach { append(it) }
				}
				if (stderr.isNotEmpty()) stderr.forEach { appendln(it) }
			}
			if (elem.text.contains("print") && output.endsWith("nothing")) output.substringBefore("nothing") else output
		} catch (e: Exception) {
			elem.text
		}
	} else elem.text
	return listOf(InlayInfo(text, elem.textOffset + elem.text.length))
}

