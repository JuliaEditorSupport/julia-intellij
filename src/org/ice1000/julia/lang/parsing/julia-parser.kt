package org.ice1000.julia.lang.parsing

import com.intellij.lang.*
import com.intellij.openapi.project.Project
import com.intellij.psi.tree.IElementType
import org.ice1000.julia.lang.JuliaParserDefinition

/**
 * !!!In progress!!!
 * @author ice1000
 */
class JuliaParserExperimental : PsiParser, LightPsiParser {
	override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
		parseLight(root, builder)
		return builder.treeBuilt
	}

	override fun parseLight(root: IElementType, builder: PsiBuilder) {
		val mark = builder.mark()
		// TODO 太难了，太难了
		// TODO 没有金刚钻，别揽瓷器活。。。
		mark.done(root)
	}
}

class JuliaParserDefinitionExperimental : JuliaParserDefinition() {
	override fun createParser(project: Project?) = JuliaParserExperimental()
}
