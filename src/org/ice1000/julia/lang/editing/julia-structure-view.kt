package org.ice1000.julia.lang.editing

import com.intellij.ide.structureView.*
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.lang.ASTNode
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.lang.folding.*
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.pom.Navigatable
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.PsiIconUtil
import icons.JuliaIcons
import org.ice1000.julia.lang.JuliaFile
import org.ice1000.julia.lang.orFalse
import org.ice1000.julia.lang.psi.*
import org.ice1000.julia.lang.psi.impl.IJuliaFunctionDeclaration

class JuliaStructureViewModel(root: PsiFile, editor: Editor?) :
	StructureViewModelBase(root, editor, JuliaStructureViewElement(root)),
	StructureViewModel.ElementInfoProvider {
	init {
		withSuitableClasses(JuliaFile::class.java,
			JuliaModuleDeclaration::class.java,
			IJuliaFunctionDeclaration::class.java,
			JuliaTypeDeclaration::class.java,
			JuliaIfExpr::class.java,
			JuliaElseIfClause::class.java,
			JuliaElseClause::class.java,
			JuliaWhileExpr::class.java,
			JuliaAssignOp::class.java,
			JuliaTypeOp::class.java,
			JuliaSymbol::class.java)
	}

	override fun shouldEnterElement(o: Any?) = true
	override fun isAlwaysShowsPlus(element: StructureViewTreeElement) = false
	override fun isAlwaysLeaf(element: StructureViewTreeElement) = element is JuliaFunction
}

class JuliaStructureViewElement(private val root: NavigatablePsiElement) :
	StructureViewTreeElement, ItemPresentation, SortableTreeElement, Navigatable by root {
	override fun getLocationString() = ""
	override fun getIcon(open: Boolean) = when (root) {
		is JuliaFile -> PsiIconUtil.getProvidersIcon(root, 0)
		is IJuliaFunctionDeclaration -> JuliaIcons.JULIA_FUNCTION_ICON
		is JuliaModuleDeclaration -> JuliaIcons.JULIA_MODULE_ICON
		is JuliaTypeDeclaration -> JuliaIcons.JULIA_TYPE_ICON
		is JuliaWhileExpr -> JuliaIcons.JULIA_WHILE_ICON
		is JuliaTypeOp -> JuliaIcons.JULIA_VARIABLE_ICON
		is JuliaIfExpr -> JuliaIcons.JULIA_IF_ICON
		is JuliaAssignOp ->
			if (root.exprList.firstOrNull()?.let { it.firstChild.node.elementType == JuliaTypes.CONST_KEYWORD }.orFalse())
				JuliaIcons.JULIA_CONST_ICON
			else JuliaIcons.JULIA_VARIABLE_ICON
		is JuliaSymbol -> if (root.isFieldInTypeDeclaration) JuliaIcons.JULIA_VARIABLE_ICON else JuliaIcons.JULIA_BIG_ICON
		else -> JuliaIcons.JULIA_BIG_ICON
	}

	override fun getPresentableText() = cutText(root.presentText(), 60)
	override fun getPresentation() = this
	override fun getValue() = root
	override fun getAlphaSortKey() = (root as? PsiNamedElement)?.name.orEmpty()
	override fun getChildren() = root
		.children
		.flatMap { (it as? JuliaStatements)?.children?.toList() ?: listOf(it) }
		.filter {
			if (it is JuliaSymbol || it is JuliaTypeOp) it.isFieldInTypeDeclaration
			else it.treeViewTokens
		}
		.map { JuliaStructureViewElement(it as NavigatablePsiElement) }
		.toTypedArray()
}

class JuliaStructureViewFactory : PsiStructureViewFactory {
	override fun getStructureViewBuilder(psiFile: PsiFile) = object : TreeBasedStructureViewBuilder() {
		override fun isRootNodeShown() = true
		override fun createStructureViewModel(editor: Editor?) = JuliaStructureViewModel(psiFile, editor)
	}
}

/**
 * Inspired by Grammar-Kit plugin and JavaX-Var-Hint plugin
 */
class JuliaCustomFoldingBuilder : CustomFoldingBuilder() {
	override fun isRegionCollapsedByDefault(node: ASTNode) = false
	override fun buildLanguageFoldRegions(descriptors: MutableList<FoldingDescriptor>, root: PsiElement, document: Document, quick: Boolean) {
		if (root !is JuliaFile || root.hasError) return

		PsiTreeUtil.findChildrenOfType(root, JuliaStatements::class.java).flatMap {
			PsiTreeUtil.findChildrenOfType(it, JuliaFunction::class.java).mapNotNull {
				getFold(it, it.toText)
			}
		}.let(descriptors::addAll)
	}

	private fun getFold(elem: PsiElement, placeHolder: String?) =
		NamedFoldingDescriptor(elem.node, elem.textRange, null, placeHolder ?: "...")

	/**
	 * The return String will be overrode by [NamedFoldingDescriptor]'s `placeHolder`.
	 */
	override fun getLanguagePlaceholderText(node: ASTNode, range: TextRange): String = "..."

	private val PsiElement.hasError get() = PsiTreeUtil.hasErrorElements(this)

}