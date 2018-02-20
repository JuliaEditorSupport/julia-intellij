package org.ice1000.julia.lang.editing

import com.intellij.ide.structureView.*
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.Editor
import com.intellij.pom.Navigatable
import com.intellij.psi.*
import org.ice1000.julia.lang.JuliaFile
import org.ice1000.julia.lang.psi.*
import org.ice1000.julia.lang.psi.impl.IJuliaFunctionDeclaration

class JuliaStructureViewModel(psiFile: PsiFile, editor: Editor?) :
	StructureViewModelBase(psiFile, editor, JuliaStructureViewElement(psiFile)),
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
	override fun getChildren() = childrenOf(root)
	override fun getLocationString() = ""
	override fun getIcon(open: Boolean) = root.presentIcon()
	override fun getPresentableText() = cutText(root.presentText(), 50)
	override fun getPresentation() = this
	override fun navigate(requestFocus: Boolean) = root.navigate(requestFocus)
	override fun getValue() = root
	override fun canNavigate() = root.canNavigate()
	override fun canNavigateToSource() = root.canNavigateToSource()
	override fun getAlphaSortKey() = (root as? PsiNamedElement)?.name.orEmpty()
}

class JuliaStructureViewFactory : PsiStructureViewFactory {
	override fun getStructureViewBuilder(psiFile: PsiFile) = object : TreeBasedStructureViewBuilder() {
		override fun isRootNodeShown() = true
		override fun createStructureViewModel(editor: Editor?) = JuliaStructureViewModel(psiFile, editor)
	}
}
