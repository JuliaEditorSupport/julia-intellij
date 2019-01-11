package org.ice1000.julia.lang

import com.intellij.lang.*
import com.intellij.lexer.FlexAdapter
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.stubs.PsiFileStub
import com.intellij.psi.tree.*
import org.ice1000.julia.lang.psi.JuliaTypes
import org.ice1000.julia.lang.psi.impl.JuliaLazyParseableBlockImpl

class JuliaLexerAdapter : FlexAdapter(JuliaLexer())

open class JuliaParserDefinition : ParserDefinition {
	private companion object {
		private val FILE = IFileElementType(JuliaLanguage.INSTANCE)
	}

	override fun createParser(project: Project?): PsiParser = JuliaParser()
	final override fun createFile(viewProvider: FileViewProvider) = JuliaFile(viewProvider)
	final override fun spaceExistanceTypeBetweenTokens(
		left: ASTNode?,
		right: ASTNode?) = ParserDefinition.SpaceRequirements.MAY

	final override fun getStringLiteralElements() = JuliaTokenType.STRINGS
	final override fun getFileNodeType() = FILE
	final override fun createLexer(project: Project?) = JuliaLexerAdapter()
	final override fun createElement(node: ASTNode?): PsiElement = JuliaTypes.Factory.createElement(node)
	final override fun getCommentTokens() = JuliaTokenType.COMMENTS
	final override fun getWhitespaceTokens() = JuliaTokenType.WHITE_SPACE
}

class JuliaTokenType(debugName: String) : IElementType(debugName, JuliaLanguage.INSTANCE) {
	companion object TokenHolder {
		@JvmField val LAZY_PARSEABLE_BLOCK: IElementType = JuliaLazyParseableBlockElementType()

		@JvmField val COMMENTS = TokenSet.create(
			JuliaTypes.BLOCK_COMMENT_BODY,
			JuliaTypes.BLOCK_COMMENT_START,
			JuliaTypes.BLOCK_COMMENT_END,
			JuliaTypes.LINE_COMMENT
		)

		@JvmField val STRINGS = TokenSet.create(
			JuliaTypes.REGULAR_STRING_PART_LITERAL,
			JuliaTypes.STRING_CONTENT
		)

		@JvmField val BINARY_OPERATORS = TokenSet.create(
			JuliaTypes.SPECIAL_ARROW_SYM,
			JuliaTypes.ARROW_SYM,
			JuliaTypes.DOT_SYM,
			JuliaTypes.DOUBLE_COLON,
			JuliaTypes.COLON_SYM,
			JuliaTypes.TRANSPOSE_SYM,
			JuliaTypes.FACTORISE_SYM,
			JuliaTypes.EXPONENT_SYM,
			JuliaTypes.EQUALS_SYM,
			JuliaTypes.NOT_SYM,
			JuliaTypes.BITWISE_NOT_SYM,
			JuliaTypes.BITWISE_AND_SYM,
			JuliaTypes.BITWISE_OR_SYM,
			JuliaTypes.BITWISE_XOR_SYM,
			JuliaTypes.REMAINDER_SYM,
			JuliaTypes.SUBTYPE_SYM,
			JuliaTypes.INTERPOLATE_SYM,
			JuliaTypes.INVERSE_DIV_SYM,
			JuliaTypes.IS_SYM,
			JuliaTypes.ISNT_SYM,
			JuliaTypes.LAMBDA_ABSTRACTION,
			JuliaTypes.SLICE_SYM,
			JuliaTypes.LESS_THAN_SYM,
			JuliaTypes.LESS_THAN_OR_EQUAL_SYM,
			JuliaTypes.USHR_SYM,
			JuliaTypes.AND_SYM,
			JuliaTypes.OR_SYM,
			JuliaTypes.INVERSE_PIPE_SYM,
			JuliaTypes.PIPE_SYM,
			JuliaTypes.SHL_SYM,
			JuliaTypes.SHR_SYM,
			JuliaTypes.PLUS_SYM,
			JuliaTypes.MINUS_SYM,
			JuliaTypes.MULTIPLY_SYM,
			JuliaTypes.UNEQUAL_SYM,
			JuliaTypes.IN_SYM,
			JuliaTypes.FRACTION_SYM,
			JuliaTypes.GREATER_THAN_SYM,
			JuliaTypes.GREATER_THAN_OR_EQUAL_SYM,
			JuliaTypes.DIVIDE_SYM
		)

		@JvmField val ASSIGN_OPERATORS = TokenSet.create(
			JuliaTypes.EQ_SYM,
			JuliaTypes.INVERSE_DIV_ASSIGN_SYM,
			JuliaTypes.USHR_ASSIGN_SYM,
			JuliaTypes.SHL_ASSIGN_SYM,
			JuliaTypes.SHR_ASSIGN_SYM,
			JuliaTypes.PLUS_ASSIGN_SYM,
			JuliaTypes.MINUS_ASSIGN_SYM,
			JuliaTypes.MULTIPLY_ASSIGN_SYM,
			JuliaTypes.DIVIDE_ASSIGN_SYM,
			JuliaTypes.FRACTION_ASSIGN_SYM,
			JuliaTypes.FACTORISE_ASSIGN_SYM,
			JuliaTypes.EXPONENT_ASSIGN_SYM,
			JuliaTypes.BITWISE_AND_ASSIGN_SYM,
			JuliaTypes.BITWISE_OR_ASSIGN_SYM,
			JuliaTypes.BITWISE_XOR_ASSIGN_SYM,
			JuliaTypes.REMAINDER_ASSIGN_SYM
		)

		@JvmField val CONCATENATABLE_TOKENS = TokenSet.orSet(COMMENTS, STRINGS)
		// Cannot use TokenSet.WHITE_SPACE since PhpStorm doesn't have such field
		@JvmField val WHITE_SPACE = TokenSet.create(TokenType.WHITE_SPACE)
		fun fromText(code: String, project: Project): PsiElement = PsiFileFactory
			.getInstance(project)
			.createFileFromText(JuliaLanguage.INSTANCE, code)
			.firstChild
	}

	class JuliaLazyParseableBlockElementType : IReparseableElementType("JuliaStatementsImpl(STATEMENTS)", JuliaLanguage.INSTANCE) {
		override fun createNode(text: CharSequence?): ASTNode? = JuliaLazyParseableBlockImpl(this, text)

		override fun parseContents(lazyParseableBlock: ASTNode): ASTNode? {
			val builder = PsiBuilderFactory.getInstance().createBuilder(lazyParseableBlock.treeParent.psi.project,
				lazyParseableBlock,
				JuliaLexerAdapter(),
				language,
				lazyParseableBlock.chars)
			JuliaParser().parseLight(JuliaTypes.STATEMENTS, builder)
			return builder.treeBuilt.firstChildNode
		}
	}
}

class JuliaElementType(debugName: String) : IElementType(debugName, JuliaLanguage.INSTANCE)
