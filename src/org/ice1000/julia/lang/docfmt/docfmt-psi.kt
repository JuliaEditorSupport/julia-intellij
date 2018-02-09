package org.ice1000.julia.lang.docfmt

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.ice1000.julia.lang.docfmt.psi.DocfmtConfig

interface IDocfmtConfig : PsiElement {
	var type: Int
}

abstract class DocfmtConfigMixin(node: ASTNode) : DocfmtConfig, ASTWrapperPsiElement(node) {
	override var type = -1
	override fun subtreeChanged() {
		type = -1
		super.subtreeChanged()
	}
}
