package org.ice1000.julia.lang.editing

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.tree.TokenSet
import org.ice1000.julia.lang.JuliaLexerAdapter
import org.ice1000.julia.lang.psi.JuliaTypes



class JuliaFindUsagesProvider:FindUsagesProvider{
	override fun getWordsScanner(): WordsScanner?=DefaultWordsScanner(JuliaLexerAdapter(),
		TokenSet.create(JuliaTypes.SYMBOL),
		TokenSet.create(JuliaTypes.LINE_COMMENT),
		TokenSet.create(JuliaTypes.STRING))

	override fun getNodeText(element: PsiElement, useFullName: Boolean) =
		if (element.canBeNamed) element.presentText() else ""

	override fun getDescriptiveName(element: PsiElement) =
		if (element.canBeNamed) element.presentText() else ""

	override fun getType(element: PsiElement) =
		if(element.canBeNamed) element.text else ""

	override fun getHelpId(psiElement: PsiElement): String? = null

	override fun canFindUsagesFor(psiElement: PsiElement) = 	psiElement is PsiNamedElement

}