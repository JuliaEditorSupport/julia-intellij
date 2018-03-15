package org.ice1000.julia.lang.parsing

import com.intellij.lang.*
import com.intellij.psi.tree.IElementType

/**
 * !!!In progress!!!
 * @author ice1000
 */
class JuliaParser_Experimental : PsiParser, LightPsiParser {
	override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
		parseLight(root, builder)
		return builder.treeBuilt
	}

	override fun parseLight(root: IElementType?, builder: PsiBuilder) {
		// TODO
	}
}

