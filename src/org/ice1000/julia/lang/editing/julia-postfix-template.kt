package org.ice1000.julia.lang.editing

import com.intellij.codeInsight.template.Template
import com.intellij.codeInsight.template.TemplateManager
import com.intellij.codeInsight.template.postfix.templates.*
import com.intellij.codeInsight.unwrap.ScopeHighlighter
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.*
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.IntroduceTargetChooser
import com.intellij.util.containers.ContainerUtil
import org.ice1000.julia.lang.editing.JuliaPostfixTemplateProvider.Companion.selectorTopmost
import org.ice1000.julia.lang.psi.JuliaApplyFunctionOp
import org.ice1000.julia.lang.psi.JuliaExpr


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
			JuliaPrintPostfixTemplate("print"),
			JuliaSoutPostfixTemplate("sout", "println"),
			JuliaLengthPostfixTemplate("length"),
			JuliaSizePostfixTemplate("size")
		)
	}

	override fun getTemplates(): Set<PostfixTemplate> = myTemplates
	override fun isTerminalSymbol(currentChar: Char) = currentChar == '.'
	override fun afterExpand(file: PsiFile, editor: Editor) = Unit
	override fun preCheck(copyFile: PsiFile, realEditor: Editor, currentOffset: Int): PsiFile = copyFile
	override fun preExpand(file: PsiFile, editor: Editor) = Unit
}

/**
 * These classes' name should be equal to `res/postfixTemplates/`
 */
class JuliaPrintPostfixTemplate(name: String, functionName: String = name) : JuliaPostfixTemplateBase(name, functionName)

class JuliaSoutPostfixTemplate(name: String, functionName: String = name) : JuliaPostfixTemplateBase(name, functionName)
class JuliaLengthPostfixTemplate(name: String, functionName: String = name) : JuliaPostfixTemplateBase(name, functionName)
class JuliaSizePostfixTemplate(name: String, functionName: String = name) : JuliaPostfixTemplateBase(name, functionName)

/**
 * PostfixTemplate(String,String) is deprecated but we have to compact legacy versions.
 */
@Suppress("DEPRECATION")
open class JuliaPostfixTemplateBase(private val name: String,
																		private val functionName: String = name,
																		private val selector: PostfixTemplateExpressionSelector = selectorTopmost()) :
	PostfixTemplate(name, "$functionName(expr)") {
	override fun isApplicable(context: PsiElement, copyDocument: Document, newOffset: Int): Boolean {
		return selector.hasExpression(context, copyDocument, newOffset)
	}

	override fun expand(context: PsiElement, editor: Editor) {
		val expressions = selector.getExpressions(context,
			editor.document,
			editor.caretModel.offset)

		if (expressions.isEmpty()) {
			PostfixTemplatesUtils.showErrorHint(context.project, editor)
			return
		}
		if (expressions.size == 1) {
			prepareAndExpandForChooseExpression(expressions[0], editor)
			return
		}
		if (ApplicationManager.getApplication().isUnitTestMode) {
			val item = ContainerUtil.getFirstItem<PsiElement>(expressions)!!
			prepareAndExpandForChooseExpression(item, editor)
			return
		} else IntroduceTargetChooser.showChooser(
			editor, expressions,
			object : Pass<PsiElement>() {
				override fun pass(e: PsiElement) {
					prepareAndExpandForChooseExpression(e, editor)
				}
			},
			selector.renderer,
			"Expressions", 0, ScopeHighlighter.NATURAL_RANGER
		)

	}

	protected fun prepareAndExpandForChooseExpression(expression: PsiElement, editor: Editor) {
		ApplicationManager.getApplication().runWriteAction {
			CommandProcessor.getInstance()
				.executeCommand(expression.project, { expandForChooseExpression(expression, editor) }, "Expand postfix template",
					PostfixLiveTemplate.POSTFIX_TEMPLATE_ID)
		}
	}

	private fun expandForChooseExpression(expr: PsiElement, editor: Editor) {
		val project = expr.project
		val document = editor.document
		val elementForRemoving = getElementToRemove(expr)
		document.deleteString(elementForRemoving.textRange.startOffset, elementForRemoving.textRange.endOffset)
		val manager = TemplateManager.getInstance(project)
		val templateString = getTemplateString(expr)
		val template = createTemplate(manager, templateString)
		manager.startTemplate(editor, template)
	}

	private fun createTemplate(manager: TemplateManager, templateString: String): Template {
		val template = manager.createTemplate("", "", templateString)
		template.isToReformat = true
		return template
	}

	private fun getTemplateString(expr: PsiElement): String = "$functionName(${expr.text})"
	private fun getElementToRemove(expr: PsiElement): PsiElement = expr
}