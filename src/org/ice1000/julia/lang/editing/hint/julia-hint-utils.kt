package org.ice1000.julia.lang.editing.hint

import com.intellij.codeInsight.hints.InlayInfo
import com.intellij.psi.PsiElement
import org.ice1000.julia.lang.executeCommand
import org.ice1000.julia.lang.module.defaultExePath
import org.ice1000.julia.lang.psi.JuliaExpr
import java.nio.file.Paths

fun providePropertyTypeHint(elem: PsiElement): List<InlayInfo> {
	return provideTypeHint(elem, elem.textOffset + elem.text.length)
}

fun executeJuliaE(exePath: String, code: String?, timeLimit: Long, vararg params: String) =
	executeCommand(
		"${Paths.get(exePath).toAbsolutePath()} -E \"$code\"",
		null,
		timeLimit
	)

fun provideTypeHint(element: PsiElement, offset: Int): List<InlayInfo> {
	val text = if (element is JuliaExpr) {
		try {
			val juliaExe = defaultExePath
			val output = buildString {
				val (stdout, stderr) = executeJuliaE(juliaExe, element.text, 1000)
				if (stdout.isNotEmpty()) {
					append(" => ")
					stdout.forEach { append(it) }
				}
				if (stderr.isNotEmpty()) {
					stderr.forEach { appendln(it) }
				}
			}
			if(element.text.contains("print") && output.endsWith("nothing")) output.substringBefore("nothing") else output
		} catch (e: Exception) {
			element.text
		}
	} else element.text

	return listOf(InlayInfo(text, offset))
}