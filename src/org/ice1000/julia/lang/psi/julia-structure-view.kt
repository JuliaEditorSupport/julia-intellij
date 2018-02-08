package org.ice1000.julia.lang.psi

import com.intellij.ide.structureView.*
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.julia.lang.JuliaLanguage
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


	override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean {
		return false
	}

	override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean {
		println(element.value)
		return true
	}
}

class JuliaStructureViewElement(private val element: PsiElement) : StructureViewTreeElement, SortableTreeElement {

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
	 */
	override fun getChildren(): Array<out TreeElement> {
		val ret: Array<out TreeElement>
		if (element.language == JuliaLanguage.INSTANCE) {
			val properties = PsiTreeUtil.getChildrenOfType(element, JuliaFunctionImpl::class.java)
			val treeElements = ArrayList<TreeElement>(properties!!.size)
			properties.mapTo(treeElements) { JuliaStructureViewElement(it) }
			ret = treeElements.toTypedArray()
		} else {
			ret = StructureViewTreeElement.EMPTY_ARRAY
		}
//		print result: `FILE` ?
//		println(ret)
		return ret
	}
}