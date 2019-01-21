package org.ice1000.julia.lang.psi

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.ex.VirtualFileManagerEx
import com.intellij.psi.*
import org.ice1000.julia.lang.psi.impl.*
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

		val elementType = sourceElement.node?.elementType
		if (elementType == JuliaTypes.REGULAR_STRING_PART_LITERAL) {
			val func = sourceElement.parent?.parent?.parent
			if (func is JuliaApplyFunctionOp) {
				val name = func.exprList.firstOrNull()?.text ?: return null
				when (name) {
					"joinpath" -> {
						val currentFileDir = sourceElement.containingFile.containingDirectory
						val text = func.exprList.asSequence().filterIsInstance<JuliaString>().joinToString(File.separator) { it.text.trim('\"') }
						return arrayOfPsiElements(currentFileDir, text)
					}
					"include" -> {
						val currentFileDir = sourceElement.containingFile.containingDirectory
						return arrayOfPsiElements(currentFileDir, sourceElement.text)
					}
				}
			}
		} else if (elementType == JuliaTypes.SYM) {
			val juliaSymbol = sourceElement.parent as? JuliaSymbol ?: return null
			return when (juliaSymbol.symbolKind) {
				JuliaSymbolKind.ApplyFunctionName -> {
					JuliaTypeDeclarationIndex.findElementsByName(project, juliaSymbol.text).toTypedArray()
				}
				JuliaSymbolKind.ModuleName -> {
					if (juliaSymbol.isInUsingExpr) {
						// a stupid cast if I use `if`
						JuliaModuleDeclarationIndex.findElementsByName(project, juliaSymbol.text).toTypedArray() as Array<PsiElement>
					} else {
						//not in usingExpr means in `module xxx`, its navigator should be in findUsage
						null
					}
				}
				// should be in findUsage but not here
				JuliaSymbolKind.TypeName -> null
				else -> null
			}
		}
		return null
	}

	override fun getActionText(context: DataContext): String? = null
}