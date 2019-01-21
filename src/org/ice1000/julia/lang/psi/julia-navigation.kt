package org.ice1000.julia.lang.psi

import com.google.gson.JsonParser
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.ex.VirtualFileManagerEx
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.julia.lang.module.languageServer
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
					val ret = project.languageServer.searchByName(juliaSymbol.text) ?: return null
					return try {
						val unescaped = StringUtil.unescapeStringCharacters(ret.trim('"'))
						val json = JsonParser().parse(unescaped)
						json.asJsonArray.mapNotNull {
							val each = it.asJsonArray
							val file = each[0].asString.let(::File)
							val line = each[1].asInt
							val vf = LocalFileSystem.getInstance().findFileByIoFile(file) ?: return@mapNotNull null
							val document = FileDocumentManager.getInstance().getDocument(vf) ?: return@mapNotNull null
							val psiOffset = document.getLineStartOffset(line - 1)
							val psiFile = PsiManager.getInstance(project).findFile(vf) ?: return@mapNotNull null
							val elem = psiFile.findElementAt(psiOffset + 1) ?: return@mapNotNull null
							PsiTreeUtil.getNonStrictParentOfType(elem, JuliaCompactFunction::class.java, JuliaFunction::class.java)
						}.toTypedArray()
					} catch (e: Exception) {
						e.printStackTrace()
						null
					}
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