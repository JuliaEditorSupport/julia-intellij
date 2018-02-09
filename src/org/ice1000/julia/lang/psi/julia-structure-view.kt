package org.ice1000.julia.lang.psi

import com.intellij.ide.structureView.*
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.editor.Editor
import com.intellij.pom.Navigatable
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNamedElement
import org.ice1000.julia.lang.psi.impl.JuliaFunctionImpl
import java.util.*


class JuliaStructureViewFactory : PsiStructureViewFactory {
	override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder? {
		return object : TreeBasedStructureViewBuilder() {
			override fun createStructureViewModel(editor: Editor?): StructureViewModel {
				return JuliaStructureViewModel(psiFile)
			}
		}
	}
}

class JuliaStructureViewModel(psiFile: PsiFile) : StructureViewModelBase(psiFile, JuliaStructureViewElement(psiFile)), StructureViewModel.ElementInfoProvider {

	override fun getSorters(): Array<Sorter> {
		return arrayOf(Sorter.ALPHA_SORTER)
	}


	override fun isAlwaysShowsPlus(element: StructureViewTreeElement) = true

	override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean {
		return when (element) {
			is JuliaFunctionImpl -> true
			else -> false
		}
	}
}

class JuliaStructureViewElement(private val element: PsiElement) :
	StructureViewTreeElement, SortableTreeElement, Navigatable by (element as NavigatablePsiElement) {

	val array = ArrayList<PsiElement>()

	override fun getValue(): Any {
		return element
	}

	override fun navigate(requestFocus: Boolean) {
		if (element is NavigationItem) {
			(element as NavigationItem).navigate(requestFocus)
		}
	}

	override fun canNavigate(): Boolean {
		return element is NavigationItem && (element as NavigationItem).canNavigate()
	}

	override fun canNavigateToSource(): Boolean {
		return element is NavigationItem && (element as NavigationItem).canNavigateToSource()
	}

	override fun getAlphaSortKey(): String {
		return (element as? PsiNamedElement)?.name.orEmpty()
	}

	override fun getPresentation(): ItemPresentation {
		return (element as NavigationItem).presentation!!
	}

	/**
	 * FIXME
	 * now can find psi but cannot show on panel
	 */
	override fun getChildren(): Array<out TreeElement> {
		val treeElements = ArrayList<TreeElement>()
		array.clear()
		findNode(element)
		println(array.size)
		val nodes = array
		nodes.forEach {
			treeElements.add(JuliaStructureViewElement(it))
		}
		return treeElements.toTypedArray()
	}

	private fun findNode(psi: PsiElement?) {
		psi?.children?.forEach {
//			println(it)
			when (it) {
				is JuliaFunctionImpl -> array.add(it)
			}
			findNode(it)
		}
//		println("------psi out------")
	}
}