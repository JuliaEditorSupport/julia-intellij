package org.ice1000.julia.lang.editing

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.lang.ASTNode
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.FileContentUtil
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.JuliaTokenType
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls

abstract class JuliaIntentionAction(@Nls private val info: String? = null) : BaseIntentionAction() {
	override fun getText() = info ?: super.getText()
	override fun isAvailable(project: Project, editor: Editor?, psiFile: PsiFile?) = true
	override fun getFamilyName() = JuliaBundle.message("julia.name")
}

class JuliaRemoveElementIntention(
	private val element: PsiElement,
	intentionText: String) : JuliaIntentionAction(intentionText) {
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		ApplicationManager.getApplication().runWriteAction(element::delete)
	}
}

class JuliaRemoveElementChildIntention(
	private val element: PsiElement,
	private val child: ASTNode,
	intentionText: String) : JuliaIntentionAction(intentionText) {
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		ApplicationManager.getApplication().runWriteAction { element.node.removeChild(child) }
	}
}

class JuliaReplaceWithTextIntention(
	private val element: PsiElement,
	@NonNls private val new: String,
	info: String) : JuliaIntentionAction(info) {
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		ApplicationManager.getApplication().runWriteAction {
			JuliaTokenType.fromText(new, project).let(element::replace)
		}
	}
}

class JuliaInsertTextBeforeIntention(
	private val element: PsiElement,
	@NonNls private val new: String,
	info: String,
	private val reparse: Boolean = false) : JuliaIntentionAction(info) {
	override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
		ApplicationManager.getApplication().runWriteAction {
			element.parent.addBefore(JuliaTokenType.fromText(new, project), element)
			if (reparse) element.containingFile.virtualFile?.let { FileContentUtil.reparseFiles(it) }
		}
	}
}

class JuliaReplaceNodeWithTextIntention(
	private val element: ASTNode,
	@NonNls private val new: String,
	info: String) : JuliaIntentionAction(info) {
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		ApplicationManager.getApplication().runWriteAction {
			element.treeParent.replaceChild(element, JuliaTokenType.fromText(new, project).node)
		}
	}
}
