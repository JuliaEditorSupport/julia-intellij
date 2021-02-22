@file:Suppress("UNCHECKED_CAST")

package org.ice1000.julia.lang.psi

import com.google.gson.JsonParser
import com.intellij.codeInsight.daemon.*
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
import org.ice1000.julia.lang.psi.impl.*
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import com.intellij.util.FunctionUtil
import javax.swing.Icon
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.util.PsiUtilBase
import com.intellij.openapi.editor.ElementColorProvider
import com.intellij.ui.ColorChooser
import com.intellij.util.ui.ColorIcon
import com.intellij.util.ui.ColorsIcon
import org.ice1000.julia.lang.module.JULIA_COLOR_CONSTANTS
import java.awt.Color


/**
 * Goto JuliaFile in a string by Ctrl/Meta + Click
 */
class JuliaGotoDeclarationHandler : GotoDeclarationHandler {
	override fun getGotoDeclarationTargets(sourceElement: PsiElement?, offset: Int, editor: Editor?): Array<PsiElement>? {
		sourceElement ?: return null
		val project = sourceElement.project

		fun arrayOfPsiElements(dir: PsiDirectory, text: String): Array<PsiElement>? {
			val url = dir.virtualFile.url + File.separator + text
			val vf = VirtualFileManagerEx.getInstance().findFileByUrl(url) ?: return null
			val f = PsiManager.getInstance(project).findFile(vf) ?: return null
			return arrayOf(f)
		}

		val elementType = sourceElement.node?.elementType
		// goto file when click string content
		if (elementType == JuliaTypes.REGULAR_STRING_PART_LITERAL) {

			val file = sourceElement.containingFile ?: return null
			val func = sourceElement.parent?.parent?.parent

			if (func is JuliaApplyFunctionOp) {
				val name = func.exprList.firstOrNull()?.text ?: return null
				val currentFileDir = file.containingDirectory ?: return null
				when (name) {
					"joinpath" -> {
						val text = func.exprList.asSequence().filterIsInstance<JuliaString>().joinToString(File.separator) { it.text.trim('\"') }
						return arrayOfPsiElements(currentFileDir, text)
					}
					"include" -> {
						return arrayOfPsiElements(currentFileDir, sourceElement.text)
					}
				}
			}
		} else if (elementType == JuliaTypes.SYM) {
			val juliaSymbol = sourceElement.parent as? JuliaSymbol ?: return null
			return when (juliaSymbol.symbolKind) {
				JuliaSymbolKind.ApplyFunctionName -> {
					if (juliaSymbol.text in IGNORED) return null
					val executor = Executors.newCachedThreadPool()
					val result: MutableCollection<PsiElement> = arrayListOf()
					val future = executor.submit {
						try {
							ReadAction.compute<Unit, Throwable> {
								project.languageServer.searchFunctionsByName(juliaSymbol.text)?.let { ret ->
									if (ret.startsWith("__INTELLIJ__")) return@let null
									val unescaped = StringEscapeUtils.unescapeJava(ret.trim('"'))
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
										}.toTypedArray().also { result.addAll(it) }
									} catch (e: Exception) {
										e.printStackTrace()
									}
								}
							}
						} catch (e: Exception) {
							e.printStackTrace()
						}
					}
					return try {
						future.get(5000, TimeUnit.MILLISECONDS)
						result.addAll(JuliaTypeDeclarationIndex.findElementsByName(project, juliaSymbol.text))
						result.toTypedArray()
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
				JuliaSymbolKind.TypeName -> {
					(JuliaTypeDeclarationIndex.findElementsByName(project, juliaSymbol.text) + JuliaAbstractTypeDeclarationIndex.findElementsByName(project, juliaSymbol.text))
						.toTypedArray()
				}
				else -> null
			}
		}
		return null
	}

	companion object {
		val jsonParser = JsonParser()
		val IGNORED = arrayOf("ccall", "throw")
	}

	override fun getActionText(context: DataContext): String? = null
}

/**
 * It can't depends on language server but StubIndex.
 */
class JuliaLineMarkerProvider : LineMarkerProvider {
	companion object {
		val overridenTypeIcon = AllIcons.Gutter.OverridenMethod // ↓
		val overridingIcon = AllIcons.Gutter.OverridingMethod // ♂
		private val mapfileElementColorProvider = JuliaFileElementColorProvider()

	}

	override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
		val project = element.project
		if (element is IJuliaSymbol) {
			when {
				element.parent is JuliaAbstractTypeDeclaration -> {
					return NavigationGutterIconBuilder
						.create(overridenTypeIcon)
						.setTooltipText("Please Click name to navigate to Subtypes")
						.setTarget(element)
						.createLineMarkerInfo(element)
				}
				// function Base.+
				element.parent is JuliaFunction
					&& element.prevSibling?.elementType == JuliaTypes.DOT_SYM
					&& element.prevSibling?.prevSibling?.elementType == JuliaTypes.SYM -> {
					val moduleName = element.prevSibling.prevSibling.text
					val overridingName = element.text
					val modules = JuliaModuleDeclarationIndex.findElementsByName(project, moduleName)
					val targets = modules.flatMap { module ->
						module.statements?.let { stmt ->
							stmt.children.filter { (it is IJuliaFunctionDeclaration) && it.nameIdentifier?.text == overridingName }
						} ?: emptyList()
					}
					return if (targets.isNotEmpty()) {
						NavigationGutterIconBuilder
							.create(overridingIcon)
							.setTooltipText("navigate to overrided function. (This feature is still working)")
							.setTargets(targets)
							.createLineMarkerInfo(element)
					} else null
				}
				element.isSuperTypeExpr || element.parent is JuliaType && element.parent.isSuperTypeExpr -> {
					val target = JuliaTypeDeclarationIndex.findElementsByName(project, element.text) + JuliaAbstractTypeDeclarationIndex.findElementsByName(project, element.text)
					return NavigationGutterIconBuilder
						.create(overridingIcon)
						.setTooltipText("navigate to overrided type.")
						.setTargets(target)
						.createLineMarkerInfo(element)
				}
				element.parent is JuliaImplicitMultiplyOp && element.nextRealSibling is JuliaString -> {
					if (element.text == "colorant") {
						val stringPart = (element.nextRealSibling as? JuliaString) ?: return null
						val color = mapfileElementColorProvider.getColorFrom(stringPart) ?: return null
						val info = MyColorInfo(element, color, mapfileElementColorProvider)
						NavigateAction.setNavigateAction(info, "Choose color", null)
						return info
					}
				}
			}
		}

		return null
	}

	override fun collectSlowLineMarkers(
		elements: MutableList<out PsiElement>,
		result: MutableCollection<in LineMarkerInfo<*>>
	) {
		super.collectSlowLineMarkers(elements, result)
	}

	private class MyColorInfo internal
	constructor(element: PsiElement, private val color: Color, colorProvider: ElementColorProvider)
		: MergeableLineMarkerInfo<PsiElement>(element, element.textRange, ColorIcon(12, color), FunctionUtil.nullConstant<Any, String>(),
		GutterIconNavigationHandler<PsiElement> { e, elt ->
			if (!elt.isWritable) return@GutterIconNavigationHandler

			val editor = PsiUtilBase.findEditor(element)!!
			val c = ColorChooser.chooseColor(editor.component, "Choose Color", color, false)
			if (c != null) {
				try {
					colorProvider.setColorTo(element, c)
				} catch (e: Exception) {
					e.printStackTrace()
				}
			}
		}, GutterIconRenderer.Alignment.LEFT) {

		override fun canMergeWith(info: MergeableLineMarkerInfo<*>): Boolean = info is MyColorInfo

		override fun getCommonIcon(infos: List<MergeableLineMarkerInfo<*>>): Icon {
			return if (infos.size == 2 && infos[0] is MyColorInfo && infos[1] is MyColorInfo) {
				ColorsIcon(12, (infos[0] as MyColorInfo).color, (infos[1] as MyColorInfo).color)
			} else AllIcons.Gutter.Colors
		}
	}
}

class JuliaFileElementColorProvider : ElementColorProvider {
	override fun setColorTo(element: PsiElement, color: Color) {
	}

	/**
	 *
	 * @param element [PsiElement] which is [JuliaString]
	 * @return Color?
	 */
	override fun getColorFrom(element: PsiElement): Color? {
		if (element !is JuliaString) return null
		val content = element.stringContentList.firstOrNull() ?: return null
		return parseColor(content.text)
	}

	companion object {
		@JvmStatic
		fun parseColor(content: String): Color? {
			return try {
				if (content.startsWith('#')) {
					when {
						content.length >= 9 -> Color.decode(content.substring(0, 9))
						content.length >= 7 -> Color.decode(content.substring(0, 7))
						content.length >= 4 -> Color(content[1].toColorInt(), content[2].toColorInt(), content[3].toColorInt())
						else -> null
					}
				} else {
					JULIA_COLOR_CONSTANTS[content]?.toColor()
				}
			} catch (e: Exception) {
				e.printStackTrace()
				null
			}
		}

	}
}

/**
 * @sample "#FFF" -> Color(255,255,255)
 * @sample "#EFE" -> Color(238,255,238)
 * @receiver Char
 * @return Int
 */
private fun Char.toColorInt() = when {
	this in '0'..'9' -> this - '0'
	this in 'a'..'f' -> this - 'a' + 10
	this in 'A'..'F' -> this - 'A' + 10
	else -> 0
} * 17

fun Int.toColor() = Color(
	this shr 16 and 0xFF,
	this shr 8 and 0xFF,
	this and 0xFF
)