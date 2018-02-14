package org.ice1000.julia.lang.psi

import com.intellij.ide.structureView.*
import com.intellij.ide.structureView.impl.StructureViewComposite
import com.intellij.ide.structureView.impl.TemplateLanguageStructureViewBuilder
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.pom.Navigatable
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.PsiIconUtil
import icons.JuliaIcons
import org.ice1000.julia.lang.JuliaFile
import org.ice1000.julia.lang.JuliaLanguage
import org.ice1000.julia.lang.editing.cutText
import org.ice1000.julia.lang.psi.impl.IJuliaFunctionDeclaration
import javax.swing.Icon


class JuliaStructureViewFactory : PsiStructureViewFactory {
	override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder? {
		return object : TemplateLanguageStructureViewBuilder(psiFile) {
			override fun createMainView(
				fileEditor: FileEditor?,
				mainFile: PsiFile?): StructureViewComposite.StructureViewDescriptor? {
				if (!psiFile.isValid) {
					return null
				}
				val builder = object : TreeBasedStructureViewBuilder() {
					override fun createStructureViewModel(editor: Editor?) = JuliaStructureViewModel(psiFile)
				}
				val view = builder.createStructureView(fileEditor, psiFile.project)
				return StructureViewComposite.StructureViewDescriptor(JuliaLanguage.INSTANCE.displayName, view, JuliaIcons.JULIA_ICON)
			}
		}
	}

	class JuliaStructureViewModel(psiFile: PsiFile) : StructureViewModelBase(psiFile, JuliaStructureViewElement(psiFile)),
		StructureViewModel.ElementInfoProvider {
		override fun shouldEnterElement(o: Any?) = true
		override fun isAlwaysShowsPlus(element: StructureViewTreeElement) = false
		override fun isAlwaysLeaf(element: StructureViewTreeElement) = when (element) {
			is JuliaFunction -> true
			else -> false
		}
	}

	class JuliaStructureViewElement(private val psiElement: PsiElement) :
		PsiTreeElementBase<PsiElement>(psiElement),
		SortableTreeElement, Navigatable by (psiElement as NavigatablePsiElement) {

		override fun getChildrenBase() = getGrandsonOfYourMother()

		private fun getGrandsonOfYourMother(): List<StructureViewTreeElement> {
			val children = ArrayList<StructureViewTreeElement>()
			psiElement.children
				.filter { it !is LeafPsiElement }//filter EOL
				.forEach { element ->
					if (element.isBlock) {
						if (element is JuliaStatements) {
							children.addAll(JuliaStructureViewElement(element).children)
						} else
							children.add(JuliaStructureViewElement(element))
					}
				}
			return children.toList()
		}

		override fun getLocationString() = ""
		override fun getIcon(open: Boolean) = psiElement.presentIcon()
		override fun getPresentableText() = cutText(psiElement.presentText(), 50)

		override fun navigate(requestFocus: Boolean) {
			(psiElement as? NavigationItem)?.navigate(requestFocus)
		}

		override fun getValue() = psiElement
		override fun canNavigate() = psiElement is NavigationItem && (psiElement as NavigationItem).canNavigate()
		override fun canNavigateToSource() = psiElement is NavigationItem && (psiElement as NavigationItem).canNavigateToSource()
		override fun getAlphaSortKey() = (psiElement as? PsiNamedElement)?.name.orEmpty()
	}
}

val PsiElement.isBlock
	get() = this is JuliaFile ||
		this is JuliaStatements ||
		this is JuliaModuleDeclaration ||
		this is IJuliaFunctionDeclaration ||
		this is JuliaTypeDeclaration ||
		this is JuliaIfExpr ||
		this is JuliaElseIfClause ||
		this is JuliaElseClause ||
		this is JuliaWhileExpr ||
		this is JuliaAssignOp

val PsiElement.canBeNamed
	get() = this is JuliaFile ||
		this is IJuliaFunctionDeclaration ||
		this is JuliaTypeDeclaration ||
		this is JuliaAssignOp ||
		this is JuliaSymbol

val JuliaAssignOp.varOrConstIcon
	get() = if (exprList.firstOrNull()?.let { it.firstChild.node.elementType == JuliaTypes.CONST_KEYWORD } == true)
		JuliaIcons.JULIA_CONST_ICON
	else
		JuliaIcons.JULIA_VARIABLE_ICON

val JuliaIfExpr.compareText
	get() = "if " + statements.exprList.first().text

val JuliaElseIfClause.compareText
	get() = "elseif " + statements.exprList.first().text

val JuliaWhileExpr.compareText
	get() = expr?.text ?: "while"

val JuliaAssignOp.varOrConstName: String
	get() = this.exprList.first().let { if (it is JuliaSymbolLhs) it.symbolList.last().text else it.text }

fun PsiElement.presentText() = this.let {
	when (it) {
		is JuliaFile -> it.originalFile.name
		is JuliaIfExpr -> it.compareText
		is JuliaElseClause -> "else"
		is JuliaElseIfClause -> it.compareText
		is JuliaAssignOp -> it.varOrConstName
		is JuliaWhileExpr -> it.compareText
		is JuliaTypeDeclaration -> it.exprList.first().text
		is JuliaModuleDeclaration -> it.symbol.text
		is IJuliaFunctionDeclaration -> it.exprList.first().text
		else -> it.text
	}
}

fun PsiElement.presentIcon(): Icon? {
	return when (this) {
		is JuliaFile -> PsiIconUtil.getProvidersIcon(this, 0)
		is IJuliaFunctionDeclaration -> JuliaIcons.JULIA_FUNCTION_ICON
		is JuliaModuleDeclaration -> JuliaIcons.JULIA_MODULE_ICON
		is JuliaTypeDeclaration -> JuliaIcons.JULIA_TYPE_ICON
		is JuliaWhileExpr -> JuliaIcons.JULIA_WHILE_ICON
		is JuliaAssignOp -> this.varOrConstIcon
		is JuliaIfExpr -> JuliaIcons.JULIA_IF_ICON
		else -> JuliaIcons.JULIA_BIG_ICON
	}
}