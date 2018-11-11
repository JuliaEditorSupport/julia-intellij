package org.ice1000.julia.lang.psi

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.ex.VirtualFileManagerEx
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import java.io.File

/**
 * Goto JuliaFile in a string by Ctrl/Meta + Click
 */
class JuliaGotoDeclarationHandler : GotoDeclarationHandler {
	override fun getGotoDeclarationTargets(sourceElement: PsiElement?, offset: Int, editor: Editor?): Array<PsiElement>? {

		sourceElement ?: return emptyArray()

		if (sourceElement.node?.elementType == JuliaTypes.REGULAR_STRING_PART_LITERAL) {
			val dir = sourceElement.containingFile.containingDirectory
			val url = dir.virtualFile.url + File.separator + sourceElement.text
			val vf = VirtualFileManagerEx.getInstance().findFileByUrl(url) ?: return emptyArray()
			val f = PsiManager.getInstance(sourceElement.project).findFile(vf) ?: return emptyArray()
			return arrayOf(f)
		}
		return emptyArray()
	}

	override fun getActionText(context: DataContext?): String? = null
}