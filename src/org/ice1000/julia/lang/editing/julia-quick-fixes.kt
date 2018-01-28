package org.ice1000.julia.lang.editing

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.ice1000.julia.lang.JuliaBundle
import org.ice1000.julia.lang.JuliaTokenType
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls

class JuliaRemoveElementIntention(
	private val element: PsiElement,
	@Nls private val intentionText: String) : BaseIntentionAction() {
	override fun getText() = intentionText
	override fun isAvailable(project: Project, editor: Editor?, psiFile: PsiFile?) = true
	override fun getFamilyName() = JuliaBundle.message("julia.name")
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		element.delete()
	}
}

class JuliaReplaceWithTextIntention(
	private val element: PsiElement,
	@NonNls private val new: String,
	@Nls private val info: String) : BaseIntentionAction() {
	override fun getText() = info
	override fun isAvailable(project: Project, editor: Editor?, psiFile: PsiFile?) = true
	override fun getFamilyName() = JuliaBundle.message("julia.name")
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		JuliaTokenType.fromText(new, project).let(element::replace)
	}
}

class JuliaReplaceNodeWithTextIntention(
	private val element: ASTNode,
	@NonNls private val new: String,
	@Nls private val info: String) : BaseIntentionAction() {
	override fun getText() = info
	override fun isAvailable(project: Project, editor: Editor?, psiFile: PsiFile?) = true
	override fun getFamilyName() = JuliaBundle.message("julia.name")
	override operator fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
		JuliaTokenType.fromText(new, project).let { element.treeParent.replaceChild(element, it.node) }
	}
}
