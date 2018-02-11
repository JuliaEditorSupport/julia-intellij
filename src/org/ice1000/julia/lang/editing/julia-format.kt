package org.ice1000.julia.lang.editing

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.formatter.common.AbstractBlock
import org.ice1000.julia.lang.JuliaLanguage
import org.ice1000.julia.lang.psi.JuliaTypes
import java.util.*


class JuliaBlock(
	node: ASTNode,
	wrap: Wrap?,
	alignment: Alignment?,
	private var spacingBuilder: SpacingBuilder) : AbstractBlock(node, wrap, alignment) {
	override fun isLeaf() = myNode.firstChildNode == null
	override fun getSpacing(child1: Block?, child2: Block) = spacingBuilder.getSpacing(this, child1, child2)

	override fun buildChildren(): MutableList<Block> {
		val blocks = ArrayList<Block>()
		var child: ASTNode? = myNode.firstChildNode
		while (child != null) {
			if (child.elementType !== TokenType.WHITE_SPACE) {
				val block = JuliaBlock(child, Wrap.createWrap(WrapType.NONE, false), Alignment.createAlignment(),
					spacingBuilder)
				blocks.add(block)
			}
			child = child.treeNext
		}
		return blocks
	}
}

class JuliaFormattingModelBuilder : FormattingModelBuilder {
	override fun createModel(element: PsiElement, settings: CodeStyleSettings): FormattingModel {
		return FormattingModelProvider
			.createFormattingModelForPsiFile(element.containingFile,
				JuliaBlock(element.node,
					Wrap.createWrap(WrapType.NONE, false),
					Alignment.createAlignment(),
					createSpaceBuilder(settings)),
				settings)
	}

	private fun createSpaceBuilder(settings: CodeStyleSettings): SpacingBuilder {
//		val codeStyleSettings = settings.getCustomSettings(JuliaCodeStyleSettings::class.java)
//		val commonSettings = settings.getCommonSettings(JsonLanguage.INSTANCE)
//		val spacesAroundAssign = if (commonSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS) 1 else 0
		return SpacingBuilder(settings, JuliaLanguage.INSTANCE)
			.around(JuliaTypes.ASSIGN_LEVEL_OPERATOR).spaceIf(settings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
			.around(JuliaTypes.COMPARISON_LEVEL_OPERATOR).spaces(1)
			.around(JuliaTypes.ARROW_SYM).spaces(1)
			.around(JuliaTypes.EQ_SYM).spaces(1)
			.afterInside(JuliaTypes.COMMA_SYM, JuliaTypes.EXPRESSION_LIST).spaces(1)
			.afterInside(JuliaTypes.SEMICOLON_SYM, JuliaTypes.EXPRESSION_LIST).spaces(1)
			.afterInside(JuliaTypes.COMMA_SYM, JuliaTypes.ARRAY).spaces(1)
			.afterInside(JuliaTypes.SEMICOLON_SYM, JuliaTypes.ARRAY).spaces(1)
	}

	override fun getRangeAffectingIndent(file: PsiFile, offset: Int, elementAtOffset: ASTNode): TextRange? {
		return null
	}
}
