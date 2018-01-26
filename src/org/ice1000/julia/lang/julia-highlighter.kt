package org.ice1000.julia.lang

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.options.colors.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import org.ice1000.julia.lang.psi.JuliaTypes
import org.intellij.lang.annotations.Language

object JuliaHighlighter : SyntaxHighlighter {
	@JvmField val KEYWORD = TextAttributesKey.createTextAttributesKey("JULIA_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
	@JvmField val NUMBER = TextAttributesKey.createTextAttributesKey("JULIA_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
	@JvmField val STRING = TextAttributesKey.createTextAttributesKey("JULIA_STRING", DefaultLanguageHighlighterColors.STRING)
	@JvmField val STRING_ESCAPE = TextAttributesKey.createTextAttributesKey("JULIA_STRING_ESCAPE", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE)
	@JvmField val STRING_ESCAPE_INVALID = TextAttributesKey.createTextAttributesKey("JULIA_STRING_ESCAPE_INVALID", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE)
	@JvmField val CHAR = TextAttributesKey.createTextAttributesKey("JULIA_CHAR", DefaultLanguageHighlighterColors.STRING)
	@JvmField val CHAR_ESCAPE = TextAttributesKey.createTextAttributesKey("JULIA_CHAR_ESCAPE", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE)
	@JvmField val CHAR_ESCAPE_INVALID = TextAttributesKey.createTextAttributesKey("JULIA_CHAR_ESCAPE_INVALID", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE)
	@JvmField val OPERATOR = TextAttributesKey.createTextAttributesKey("JULIA_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
	@JvmField val BRACKET = TextAttributesKey.createTextAttributesKey("JULIA_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
	@JvmField val B_BRACKET = TextAttributesKey.createTextAttributesKey("JULIA_BRACES", DefaultLanguageHighlighterColors.BRACES)
	@JvmField val M_BRACKET = TextAttributesKey.createTextAttributesKey("JULIA_BRACKET", DefaultLanguageHighlighterColors.BRACKETS)
	@JvmField val COMMENT = TextAttributesKey.createTextAttributesKey("JULIA_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
	@JvmField val BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey("JULIA_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
	@JvmField val TYPE_NAME = TextAttributesKey.createTextAttributesKey("JULIA_TYPE_NAME", DefaultLanguageHighlighterColors.CLASS_NAME)
	@JvmField val ABSTRACT_TYPE_NAME = TextAttributesKey.createTextAttributesKey("JULIA_ABSTRACT_TYPE_NAME", DefaultLanguageHighlighterColors.INTERFACE_NAME)
	@JvmField val MODULE_NAME = TextAttributesKey.createTextAttributesKey("JULIA_MODULE_NAME", DefaultLanguageHighlighterColors.CLASS_NAME)
	@JvmField val FUNCTION_NAME = TextAttributesKey.createTextAttributesKey("JULIA_FUNCTION_NAME", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)

	private val KEYWORD_KEY = arrayOf(KEYWORD)
	private val STRING_KEY = arrayOf(STRING)
	private val CHAR_KEY = arrayOf(CHAR)
	private val NUMBER_KEY = arrayOf(NUMBER)
	private val OPERATOR_KEY = arrayOf(OPERATOR)
	private val BRACKETS_KEY = arrayOf(BRACKET)
	private val B_BRACKETS_KEY = arrayOf(B_BRACKET)
	private val M_BRACKETS_KEY = arrayOf(M_BRACKET)
	private val COMMENT_KEY = arrayOf(COMMENT)
	private val BLOCK_COMMENT_KEY = arrayOf(BLOCK_COMMENT)

	private val OPERATOR_LIST = listOf(
			JuliaTypes.LEFT_BRACKET,
			JuliaTypes.RIGHT_BRACKET,
			JuliaTypes.LEFT_B_BRACKET,
			JuliaTypes.RIGHT_B_BRACKET,
			JuliaTypes.DOT_SYM,
			JuliaTypes.DOUBLE_COLON,
			JuliaTypes.COLON_SYM,
			JuliaTypes.SEMICOLON_SYM,
			JuliaTypes.COMMA_SYM,
			JuliaTypes.EQ_SYM,
			JuliaTypes.AT_SYM,
			JuliaTypes.ELEMENT_FRACTION_ASSIGN_SYM,
			JuliaTypes.ELEMENT_MULTIPLY_ASSIGN_SYM,
			JuliaTypes.ELEMENT_REMAINDER_ASSIGN_SYM,
			JuliaTypes.ELEMENT_REMAINDER_SYM,
			JuliaTypes.ELEMENT_EXPONENT_SYM,
			JuliaTypes.ELEMENT_MINUS_SYM,
			JuliaTypes.ELEMENT_MINUS_ASSIGN_SYM,
			JuliaTypes.ELEMENT_MULTIPLY_SYM,
			JuliaTypes.ELEMENT_PLUS_SYM,
			JuliaTypes.ELEMENT_PLUS_ASSIGN_SYM,
			JuliaTypes.ELEMENT_EQUALS_SYM,
			JuliaTypes.ELEMENT_UNEQUAL_SYM,
			JuliaTypes.ELEMENT_GREATER_THAN_SYM,
			JuliaTypes.ELEMENT_LESS_THAN_SYM,
			JuliaTypes.ELEMENT_GREATER_THAN_OR_EQUAL_SYM,
			JuliaTypes.ELEMENT_LESS_THAN_OR_EQUAL_SYM,
			JuliaTypes.ELEMENT_TRANSPOSE_SYM,
			JuliaTypes.TRANSPOSE_SYM,
			JuliaTypes.FACTORISE_ASSIGN_SYM,
			JuliaTypes.FACTORISE_SYM,
			JuliaTypes.EXPONENT_ASSIGN_SYM,
			JuliaTypes.EXPONENT_SYM,
			JuliaTypes.EQUALS_SYM,
			JuliaTypes.NOT_SYM,
			JuliaTypes.BITWISE_NOT_SYM,
			JuliaTypes.BITWISE_AND_SYM,
			JuliaTypes.BITWISE_AND_ASSIGN_SYM,
			JuliaTypes.BITWISE_OR_SYM,
			JuliaTypes.BITWISE_OR_ASSIGN_SYM,
			JuliaTypes.BITWISE_XOR_ASSIGN_SYM,
			JuliaTypes.REMAINDER_ASSIGN_SYM,
			JuliaTypes.REMAINDER_SYM,
			JuliaTypes.SUBTYPE_SYM,
			JuliaTypes.INTERPOLATE_SYM,
			JuliaTypes.INVERSE_DIV_ASSIGN_SYM,
			JuliaTypes.INVERSE_DIV_SYM,
			JuliaTypes.IS_SYM,
			JuliaTypes.ISNT_SYM,
			JuliaTypes.LAMBDA_ABSTRACTION,
			JuliaTypes.SLICE_SYM,
			JuliaTypes.LESS_THAN_SYM,
			JuliaTypes.LESS_THAN_OR_EQUAL_SYM,
			JuliaTypes.USHR_ASSIGN_SYM,
			JuliaTypes.USHR_SYM,
			JuliaTypes.AND_SYM,
			JuliaTypes.OR_SYM,
			JuliaTypes.PIPE_SYM,
			JuliaTypes.SHL_SYM,
			JuliaTypes.SHL_ASSIGN_SYM,
			JuliaTypes.SHR_SYM,
			JuliaTypes.SHR_ASSIGN_SYM,
			JuliaTypes.PLUS_SYM,
			JuliaTypes.PLUS_ASSIGN_SYM,
			JuliaTypes.MINUS_SYM,
			JuliaTypes.MINUS_ASSIGN_SYM,
			JuliaTypes.MULTIPLY_SYM,
			JuliaTypes.MULTIPLY_ASSIGN_SYM,
			JuliaTypes.UNEQUAL_SYM,
			JuliaTypes.FRACTION_ASSIGN_SYM,
			JuliaTypes.FRACTION_SYM,
			JuliaTypes.GREATER_THAN_SYM,
			JuliaTypes.GREATER_THAN_OR_EQUAL_SYM,
			JuliaTypes.DIVIDE_ASSIGN_SYM,
			JuliaTypes.DIVIDE_ASSIGN_SYM,
			JuliaTypes.DIVIDE_SYM,
			JuliaTypes.ELEMENT_SHL_SYM,
			JuliaTypes.ELEMENT_SHR_SYM,
			JuliaTypes.ELEMENT_USHR_SYM,
			JuliaTypes.ELEMENT_FRACTION_SYM,
			JuliaTypes.ELEMENT_DIVIDE_SYM,
			JuliaTypes.ELEMENT_DIV_ASSIGN_SYM,
			JuliaTypes.ELEMENT_EXPONENT_ASSIGN_SYM
	)

	private val KEYWORDS_LIST = listOf(
			JuliaTypes.END_KEYWORD,
			JuliaTypes.MODULE_KEYWORD,
			JuliaTypes.BAREMODULE_KEYWORD,
			JuliaTypes.BREAK_KEYWORD,
			JuliaTypes.CONTINUE_KEYWORD,
			JuliaTypes.INCLUDE_KEYWORD,
			JuliaTypes.EXPORT_KEYWORD,
			JuliaTypes.IMPORT_KEYWORD,
			JuliaTypes.USING_KEYWORD,
			JuliaTypes.IF_KEYWORD,
			JuliaTypes.ELSEIF_KEYWORD,
			JuliaTypes.ELSE_KEYWORD,
			JuliaTypes.FOR_KEYWORD,
			JuliaTypes.WHILE_KEYWORD,
			JuliaTypes.IN_KEYWORD,
			JuliaTypes.RETURN_KEYWORD,
			JuliaTypes.TRY_KEYWORD,
			JuliaTypes.CATCH_KEYWORD,
			JuliaTypes.FINALLY_KEYWORD,
			JuliaTypes.FUNCTION_KEYWORD,
			JuliaTypes.TYPE_KEYWORD,
			JuliaTypes.ABSTRACT_KEYWORD,
			JuliaTypes.TYPEALIAS_KEYWORD,
			JuliaTypes.IMMUTABLE_KEYWORD,
			JuliaTypes.TRUE_KEYWORD,
			JuliaTypes.FALSE_KEYWORD,
			JuliaTypes.QUOTE_KEYWORD,
			JuliaTypes.UNION_KEYWORD
	)

	/** parentheses */
	private val BRACKETS = listOf(JuliaTypes.LEFT_BRACKET, JuliaTypes.RIGHT_BRACKET)
	/** braces */
	private val B_BRACKETS = listOf(JuliaTypes.LEFT_B_BRACKET, JuliaTypes.RIGHT_B_BRACKET)
	/** brackets */
	private val M_BRACKETS: List<IElementType> = listOf()

	override fun getHighlightingLexer() = JuliaLexerAdapter()
	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		JuliaTypes.STR,
		JuliaTypes.RAW_STR -> STRING_KEY
		JuliaTypes.CHAR_LITERAL -> CHAR_KEY
		JuliaTypes.LINE_COMMENT -> COMMENT_KEY
		JuliaTypes.BLOCK_COMMENT -> BLOCK_COMMENT_KEY
		JuliaTypes.INT_LITERAL,
		JuliaTypes.FLOAT_LITERAL -> NUMBER_KEY
		in BRACKETS -> BRACKETS_KEY
		in M_BRACKETS -> M_BRACKETS_KEY
		in B_BRACKETS -> B_BRACKETS_KEY
		in KEYWORDS_LIST -> KEYWORD_KEY
		in OPERATOR_LIST -> OPERATOR_KEY
		else -> emptyArray()
	}
}

class JuliaHighlighterFactory : SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = JuliaHighlighter
}

class JuliaColorSettingsPage : ColorSettingsPage {
	private companion object DescriptorHolder {
		private val DESCRIPTORS = arrayOf(
				AttributesDescriptor(JuliaBundle.message("julia.highlighter.color-settings-pane.comment"), JuliaHighlighter.COMMENT),
				AttributesDescriptor(JuliaBundle.message("julia.highlighter.color-settings-pane.block-comment"), JuliaHighlighter.BLOCK_COMMENT),
				AttributesDescriptor(JuliaBundle.message("julia.highlighter.color-settings-pane.keyword"), JuliaHighlighter.KEYWORD),
				AttributesDescriptor(JuliaBundle.message("julia.highlighter.color-settings-pane.string"), JuliaHighlighter.STRING),
				AttributesDescriptor(JuliaBundle.message("julia.highlighter.color-settings-pane.module-name"), JuliaHighlighter.MODULE_NAME),
				AttributesDescriptor(JuliaBundle.message("julia.highlighter.color-settings-pane.abs-type-name"), JuliaHighlighter.ABSTRACT_TYPE_NAME),
				AttributesDescriptor(JuliaBundle.message("julia.highlighter.color-settings-pane.type-name"), JuliaHighlighter.TYPE_NAME)
		)

		private val ADDITIONAL_DESCRIPTORS = mapOf(
				"moduleName" to JuliaHighlighter.MODULE_NAME,
				"stringEscape" to JuliaHighlighter.STRING_ESCAPE,
				"stringEscapeInvalid" to JuliaHighlighter.STRING_ESCAPE_INVALID,
				"typeName" to JuliaHighlighter.TYPE_NAME,
				"abstractTypeName" to JuliaHighlighter.ABSTRACT_TYPE_NAME
		)
	}

	override fun getHighlighter(): SyntaxHighlighter = JuliaHighlighter
	override fun getAdditionalHighlightingTagToDescriptorMap() = ADDITIONAL_DESCRIPTORS
	override fun getIcon() = JULIA_BIG_ICON
	override fun getAttributeDescriptors() = DESCRIPTORS
	override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
	override fun getDisplayName() = JuliaFileType.name
	@Language("Julia")
	override fun getDemoText() = """
		#=
		 BLOCK COMMENT
		=#
		module <moduleName>ice1000</moduleName>
		3.2 # => 3.2 (Float64)
		1 + 1 # => 2
		div(5, 2) # => 2 # for a truncated result, use div
		<stringEscapeInvalid>'\xjs'</stringEscapeInvalid> # => escape char syntax error
		!true # => false
		@printf "%d is less than %f" 4.5 5.3 # 5 is less than 5.300000
		"1 + 2 = 3" == "1 + 2 = $(1+2)" # => true
		try
			println("Hello<stringEscape>\n</stringEscape>World\g" + '\n' + '\a')
			some_other_var # => ERROR: some_other_var not defined
		catch e
		   println(e)
		end
		abstract type <abstractTypeName>Cat</abstractTypeName> <: Animals
		end
		type <typeName>Dog</typeName> <: Animals
			age::Int64
		end
		for (k,v) in Dict("dog"=>"mammal","cat"=>"mammal","mouse"=>"mammal")
		println("${JULIA_STRING_DOLLAR}k is a ${JULIA_STRING_DOLLAR}v")
		end
		x = 0
		while x < 4
			println(x)
			x += 1
		end
	""".trimIndent()
}
