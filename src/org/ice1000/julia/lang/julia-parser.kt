package org.ice1000.julia.lang

import com.intellij.lang.*
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.*
import org.ice1000.julia.lang.psi.JuliaTypes

class JuliaParserDefinition : ParserDefinition {
	private companion object {
		private val FILE = IFileElementType(JuliaLanguage.INSTANCE)
	}

	override fun createParser(project: Project?) = JuliaParser()
	override fun createFile(viewProvider: FileViewProvider) = JuliaFile(viewProvider)
	override fun spaceExistanceTypeBetweenTokens(left: ASTNode?, right: ASTNode?) = ParserDefinition.SpaceRequirements.MAY
	override fun getStringLiteralElements() = JuliaTokenType.STRINGS
	override fun getFileNodeType() = FILE
	override fun createLexer(project: Project?) = JuliaLexerAdapter()
	override fun createElement(node: ASTNode?): PsiElement = JuliaTypes.Factory.createElement(node)
	override fun getCommentTokens() = JuliaTokenType.COMMENTS
	override fun getWhitespaceTokens() = JuliaTokenType.WHITE_SPACES
}

class JuliaTokenType(debugName: String) : IElementType(debugName, JuliaLanguage.INSTANCE) {
	companion object {
		@JvmField val COMMENTS = TokenSet.create(JuliaTypes.BLOCK_COMMENT, JuliaTypes.LINE_COMMENT)
		@JvmField val STRINGS = TokenSet.create(JuliaTypes.STR, JuliaTypes.RAW_STR, JuliaTypes.STRING)
		@JvmField val WHITE_SPACES: TokenSet = TokenSet.WHITE_SPACE
	}
}

class JuliaElementType(debugName: String) : IElementType(debugName, JuliaLanguage.INSTANCE)
