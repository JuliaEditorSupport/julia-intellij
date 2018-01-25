package org.ice1000.julia.lang

import com.intellij.lang.*
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.*
import org.ice1000.julia.lang.psi.JuliaTypes

class JuliaParserDefinition : ParserDefinition {
	private companion object {
		private val FILE = IFileElementType(Language.findInstance(JuliaLanguage::class.java))
	}

	override fun createParser(project: Project?) = JuliaParser()
	override fun createFile(viewProvider: FileViewProvider) = JuliaFile(viewProvider)
	override fun spaceExistanceTypeBetweenTokens(left: ASTNode?, right: ASTNode?) = ParserDefinition.SpaceRequirements.MAY
	override fun getStringLiteralElements() = JuliaTokenType.STRINGS
	override fun getFileNodeType() = FILE
	override fun createLexer(project: Project?) = JuliaLexerAdapter()
	override fun createElement(node: ASTNode?): PsiElement = JuliaTypes.Factory.createElement(node)
	override fun getCommentTokens() = JuliaTokenType.COMMENTS
	override fun getWhitespaceTokens(): TokenSet = TokenSet.WHITE_SPACE
}

class JuliaTokenType(debugName: String) : IElementType(debugName, JuliaLanguage) {
	companion object {
		@JvmField val COMMENTS = TokenSet.create(JuliaTypes.COMMENT)
		@JvmField val STRINGS = TokenSet.create(JuliaTypes.STR)
	}
}

class JuliaElementType(debugName: String) : IElementType(debugName, JuliaLanguage)
