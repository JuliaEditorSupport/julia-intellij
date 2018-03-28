package org.ice1000.julia.lang.docfmt

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lexer.FlexAdapter
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.*
import org.ice1000.julia.lang.JuliaLanguage
import org.ice1000.julia.lang.docfmt.psi.DocfmtTypes

class DocfmtLexerAdapter : FlexAdapter(DocfmtLexer())

class DocfmtParserDefinition : ParserDefinition {
	private companion object {
		private val FILE = IFileElementType(DocfmtLanguage.INSTANCE)
	}

	override fun spaceExistanceTypeBetweenTokens(left: ASTNode?, right: ASTNode?) = ParserDefinition.SpaceRequirements.MAY
	override fun createParser(project: Project?) = DocfmtParser()
	override fun createFile(viewProvider: FileViewProvider) = DocfmtFile(viewProvider)
	override fun createElement(node: ASTNode?): PsiElement = DocfmtTypes.Factory.createElement(node)
	override fun getCommentTokens() = DocfmtTokenType.COMMENTS
	override fun createLexer(project: Project?) = DocfmtLexerAdapter()
	override fun getWhitespaceTokens() = TokenSet.WHITE_SPACE
	override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY
	override fun getFileNodeType() = FILE
}

class DocfmtTokenType(debugName: String) : IElementType(debugName, JuliaLanguage.INSTANCE) {
	companion object TokenHolder {
		@JvmField val COMMENTS = TokenSet.create(DocfmtTypes.LINE_COMMENT)
	}
}

class DocfmtElementType(debugName: String) : IElementType(debugName, DocfmtLanguage.INSTANCE)
