package org.ice1000.julia.lang.psi

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.ex.VirtualFileManagerEx
import com.intellij.psi.*
import java.io.File

/**
 * Goto JuliaFile in a string by Ctrl/Meta + Click
 */
class JuliaGotoDeclarationHandler : GotoDeclarationHandler {
	override fun getGotoDeclarationTargets(sourceElement: PsiElement?, offset: Int, editor: Editor?): Array<PsiElement>? {

		val project = sourceElement?.project ?: return null

		fun arrayOfPsiElements(dir: PsiDirectory, text: String): Array<PsiElement>? {
			val url = dir.virtualFile.url + File.separator + text
			val vf = VirtualFileManagerEx.getInstance().findFileByUrl(url) ?: return null
			val f = PsiManager.getInstance(project).findFile(vf) ?: return null
			return arrayOf(f)
		}

		if (sourceElement.node?.elementType == JuliaTypes.REGULAR_STRING_PART_LITERAL) {
			val func = sourceElement.parent.parent.parent
			if (func is JuliaApplyFunctionOp) {
				val name = func.exprList.firstOrNull()?.text ?: return null
				when (name) {
					"joinpath" -> {
						val currentFileDir = sourceElement.containingFile.containingDirectory
						val text = func.exprList.asSequence().filter { it is JuliaString }.joinToString(File.separator) { it.text.trim('\"') }
						return arrayOfPsiElements(currentFileDir, text)
					}
					"include" -> {
						val currentFileDir = sourceElement.containingFile.containingDirectory
						return arrayOfPsiElements(currentFileDir, sourceElement.text)
					}
				}
			}
		} else if (sourceElement.node?.elementType == JuliaTypes.SYM) {
			val juliaSymbol = sourceElement.parent as? JuliaSymbol ?: return null
			if (juliaSymbol.isApplyFunctionName) {
				val ele: JuliaFunction = findElement(project, juliaSymbol.text)
					?: return null
				return arrayOf(ele)
			}
		}
		return null
	}

	override fun getActionText(context: DataContext?): String? = null
}