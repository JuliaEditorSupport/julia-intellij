package org.ice1000.julia.lang.editing

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.lang.ASTNode
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.JuliaTokenType
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls

abstract class JuliaIntentionAction : BaseIntentionAction() {
	override fun isAvailable(project: Project, editor: Editor?, psiFile: PsiFile?) = true
	override fun getFamilyName() = JuliaBundle.message("julia.name")
}

class JuliaRemoveElementIntention(
	private val element: PsiElement,
	@Nls private val intentionText: String) : JuliaIntentionAction() {
	override fun getText() = intentionText
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		ApplicationManager.getApplication().runWriteAction(element::delete)
	}
}

class JuliaRemoveElementChildIntention(
	private val element: PsiElement,
	private val child: ASTNode,
	@Nls private val intentionText: String) : JuliaIntentionAction() {
	override fun getText() = intentionText
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		ApplicationManager.getApplication().runWriteAction { element.node.removeChild(child) }
	}
}

class JuliaReplaceWithTextIntention(
	private val element: PsiElement,
	@NonNls private val new: String,
	@Nls private val info: String) : JuliaIntentionAction() {
	override fun getText() = info
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		ApplicationManager.getApplication().runWriteAction {
			JuliaTokenType.fromText(new, project).let(element::replace)
		}
	}
}

class JuliaReplaceNodeWithTextIntention(
	private val element: ASTNode,
	@NonNls private val new: String,
	@Nls private val info: String) : JuliaIntentionAction() {
	override fun getText() = info
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		ApplicationManager.getApplication().runWriteAction {
			JuliaTokenType.fromText(new, project).let { element.treeParent.replaceChild(element, it.node) }
		}
	}
}
