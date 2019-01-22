package org.ice1000.julia.lang.psi

import com.google.gson.JsonParser
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.ex.VirtualFileManagerEx
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import org.apache.commons.lang.StringEscapeUtils
import org.ice1000.julia.lang.module.languageServer
import org.ice1000.julia.lang.psi.impl.IJuliaSymbol
import org.ice1000.julia.lang.psi.impl.isInUsingExpr
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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
					val executor = Executors.newCachedThreadPool()
					var result: Array<PsiElement>? = null
					val future = executor.submit {
						try {
							ReadAction.compute<Array<PsiElement>?, Throwable> {
								project.languageServer.searchFunctionsByName(juliaSymbol.text)?.let { ret ->
									if (ret.startsWith("__INTELLIJ__")) return@compute null
									val unescaped = StringEscapeUtils.unescapeJava(ret.trim('"'))
									println(unescaped)
									try {
										val json = jsonParser.parse(unescaped)
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
										}.toTypedArray().also { result = it }
									} catch (e: Exception) {
										e.printStackTrace()
										result
									}
								} ?: result
							}
						} catch (e: Exception) {
							e.printStackTrace()
						}
					}
					return try {
						future?.get(5000, TimeUnit.MILLISECONDS)
						result
					} catch (ignored: Throwable) {
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

	companion object {
		val jsonParser = JsonParser()
	}

	override fun getActionText(context: DataContext): String? = null
}

class JuliaLineMarkerProvider : LineMarkerProvider {
	override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
		val icon = AllIcons.Gutter.OverridenMethod // ↓
		if (element is IJuliaSymbol && element.parent is JuliaAbstractTypeDeclaration) {
			val builder = NavigationGutterIconBuilder
				.create(icon)
				.setTooltipText("Please Click name to navigate to Subtypes")
				.setTarget(element)
			return builder.createLineMarkerInfo(element)
		} else return null
	}

	override fun collectSlowLineMarkers(elements: MutableList<PsiElement>, result: MutableCollection<LineMarkerInfo<PsiElement>>) {
	}
}