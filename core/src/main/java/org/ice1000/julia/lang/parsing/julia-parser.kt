package org.ice1000.julia.lang.parsing

import com.intellij.lang.*
import com.intellij.openapi.project.Project
import com.intellij.psi.tree.IElementType
import org.ice1000.julia.lang.JuliaParserDefinition
import org.ice1000.julia.lang.psi.JuliaTypes

/**
 * !!!In progress!!!
 * @author ice1000
 * @see [org.ice1000.julia.lang.JuliaParser]
 */
class JuliaParserExperimental : PsiParser, LightPsiParser {
	override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
		parseLight(root, builder)
		return builder.treeBuilt
	}

	override fun parseLight(root: IElementType, builder: PsiBuilder) {
		val mark = builder.mark()
		builder.parse(root)
		mark.done(root)
	}

	companion object ParserImplementation {
		fun PsiBuilder.markAndAdvance() = mark().also { advanceLexer() }

		fun PsiBuilder.parse(root: IElementType) {
			while (!eof()) {
				println(tokenType)
				when (tokenType) {
					JuliaTypes.SYM -> {
						val mark = markAndAdvance()
						mark.done(JuliaTypes.SYMBOL)
					}
					else -> advanceLexer()
				}
				// TODO 太难了，太难了
				// TODO 没有金刚钻，别揽瓷器活。。。
			}
		}
	}
}

/**
 * !!!In progress!!!
 * @author ice1000
 * @see [JuliaParserDefinition]
 */
class JuliaParserDefinitionExperimental : JuliaParserDefinition() {
	override fun createParser(project: Project?) = JuliaParserExperimental()
}
