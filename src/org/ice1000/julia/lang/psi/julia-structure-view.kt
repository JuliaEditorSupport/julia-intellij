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
						when (element) {
							is JuliaStatements -> children.addAll(JuliaStructureViewElement(element).children)
							is JuliaTypeDeclaration -> {
								children.add(JuliaStructureViewElement(element))
								element.exprList.filter { it is JuliaSymbol }.forEach { children.add(JuliaStructureViewElement(it)) }
							}
							else -> children.add(JuliaStructureViewElement(element))
						}
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
