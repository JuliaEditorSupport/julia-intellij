package org.ice1000.julia.lang.editing

import com.intellij.codeInsight.template.postfix.templates.*
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.Condition
import com.intellij.openapi.util.Conditions
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.containers.ContainerUtil
import org.ice1000.julia.lang.editing.JuliaPostfixTemplateProvider.Companion.selectorTopmost
import org.ice1000.julia.lang.psi.*


class JuliaPostfixTemplateProvider : PostfixTemplateProvider {
	private val myTemplates: Set<PostfixTemplate>

	companion object {
		fun selectorTopmost(): PostfixTemplateExpressionSelector {
			return selectorTopmost(Conditions.alwaysTrue())
		}

		fun selectorTopmost(additionalFilter: Condition<PsiElement>): PostfixTemplateExpressionSelector {
			return object : PostfixTemplateExpressionSelectorBase(additionalFilter) {
				override fun getNonFilteredExpressions(psiElement: PsiElement, document: Document, i: Int): List<PsiElement> {
					val stat = PsiTreeUtil.getNonStrictParentOfType(psiElement, JuliaApplyFunctionOp::class.java, JuliaExpr::class.java)
					return ContainerUtil.createMaybeSingletonList(stat)
				}
			}
		}
	}

	init {
		myTemplates = ContainerUtil.newHashSet(
			JuliaPrintPostfixTemplate(this),
			JuliaSoutPostfixTemplate(this),
			JuliaLengthPostfixTemplate(this),
			JuliaSizePostfixTemplate(this)
		)
	}

	override fun getTemplates(): Set<PostfixTemplate> = myTemplates
	override fun isTerminalSymbol(currentChar: Char) = currentChar == '.'
	override fun afterExpand(file: PsiFile, editor: Editor) = Unit
	override fun preCheck(copyFile: PsiFile, realEditor: Editor, currentOffset: Int): PsiFile = copyFile
	override fun preExpand(file: PsiFile, editor: Editor) = Unit
}

class JuliaPrintPostfixTemplate(provider: PostfixTemplateProvider) :
	StringBasedPostfixTemplate("print", "print(expr)", selectorTopmost(), provider) {
	override fun getTemplateString(psiElement: PsiElement): String? = "print(\$expr\$)"
	override fun getElementToRemove(expr: PsiElement): PsiElement = expr
}

class JuliaSoutPostfixTemplate(provider: PostfixTemplateProvider) :
	StringBasedPostfixTemplate("sout", "println(expr)", selectorTopmost(), provider) {
	override fun getTemplateString(psiElement: PsiElement): String? = "println(\$expr\$)"
	override fun getElementToRemove(expr: PsiElement): PsiElement = expr
}

class JuliaLengthPostfixTemplate(provider: PostfixTemplateProvider) :
	StringBasedPostfixTemplate("length", "length(expr)", selectorTopmost(), provider) {
	override fun getTemplateString(psiElement: PsiElement): String? = "length(\$expr\$)"
	override fun getElementToRemove(expr: PsiElement): PsiElement = expr
}

class JuliaSizePostfixTemplate(provider: PostfixTemplateProvider) :
	StringBasedPostfixTemplate("size", "size(expr)", selectorTopmost(), provider) {
	override fun getTemplateString(psiElement: PsiElement): String? = "size(\$expr\$)"
	override fun getElementToRemove(expr: PsiElement): PsiElement = expr
}