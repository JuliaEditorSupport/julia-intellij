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

object JuliaHighlighter : SyntaxHighlighter {
	@JvmField val KEYWORD = TextAttributesKey.createTextAttributesKey("JULIA_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
	@JvmField val NUMBER = TextAttributesKey.createTextAttributesKey("JULIA_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
	@JvmField val STRING = TextAttributesKey.createTextAttributesKey("JULIA_STRING", DefaultLanguageHighlighterColors.STRING)
	@JvmField val OPERATOR = TextAttributesKey.createTextAttributesKey("JULIA_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
	@JvmField val COMMENT = TextAttributesKey.createTextAttributesKey("JULIA_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
	@JvmField val BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey("JULIA_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
	@JvmField val CLASS_TYPENAME = TextAttributesKey.createTextAttributesKey("JULIA_TYPENAME", DefaultLanguageHighlighterColors.CLASS_NAME)
	@JvmField val FUNCTION_NAME = TextAttributesKey.createTextAttributesKey("JULIA_FUNCTION_NAME", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)

	private val KEYWORD_KEY = arrayOf(KEYWORD)
	private val STRING_KEY = arrayOf(STRING)
	private val NUMBER_KEY = arrayOf(NUMBER)
	private val OPERATOR_KEY = arrayOf(OPERATOR)
	private val COMMENT_KEY = arrayOf(COMMENT)
	private val BLOCK_COMMENT_KEY = arrayOf(BLOCK_COMMENT)

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
			JuliaTypes.FALSE_KEYWORD
	)

	override fun getHighlightingLexer() = JuliaLexerAdapter()
	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		JuliaTypes.STR,
		JuliaTypes.RAW_STR -> STRING_KEY
		JuliaTypes.LINE_COMMENT -> COMMENT_KEY
		JuliaTypes.BLOCK_COMMENT -> BLOCK_COMMENT_KEY
		JuliaTypes.INT_LITERAL,
		JuliaTypes.FLOAT_LITERAL -> NUMBER_KEY
		in KEYWORDS_LIST -> KEYWORD_KEY
		else -> emptyArray()
	}
}

class JuliaHighlighterFactory : SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = JuliaHighlighter
}

class JuliaColorSettingsPage : ColorSettingsPage {
	private val descriptors = arrayOf(
			AttributesDescriptor(JuliaBundle.message("julia.highlighter.color-settings-pane.comment"), JuliaHighlighter.COMMENT),
			AttributesDescriptor(JuliaBundle.message("julia.highlighter.color-settings-pane.block-comment"), JuliaHighlighter.BLOCK_COMMENT),
			AttributesDescriptor(JuliaBundle.message("julia.highlighter.color-settings-pane.keyword"), JuliaHighlighter.KEYWORD),
			AttributesDescriptor(JuliaBundle.message("julia.highlighter.color-settings-pane.string"), JuliaHighlighter.STRING),
			AttributesDescriptor(JuliaBundle.message("julia.highlighter.color-settings-pane.type-name"), JuliaHighlighter.CLASS_TYPENAME)
	)

	override fun getHighlighter(): SyntaxHighlighter = JuliaHighlighter
	override fun getAdditionalHighlightingTagToDescriptorMap() = null
	override fun getIcon() = JuliaFileType.icon
	override fun getAttributeDescriptors() = descriptors
	override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
	override fun getDisplayName() = JuliaFileType.name
	override fun getDemoText() = """
		#=
		 BLOCK COMMENT
		=#
		module ice1000
		3.2 # => 3.2 (Float64)
		1 + 1 # => 2
		div(5, 2) # => 2 # for a truncated result, use div
		# Boolean operators
		!true # => false
		@printf "%d is less than %f" 4.5 5.3 # 5 is less than 5.300000
		"1 + 2 = 3" == "1 + 2 = $(1+2)" # => true
		try
		   some_other_var # => ERROR: some_other_var not defined
		catch e
		   println(e)
		end
		abstract type Cat <: Animals
			age::Int64
		end
		for (k,v) in Dict("dog"=>"mammal","cat"=>"mammal","mouse"=>"mammal")
		println("$JULIA_STRING_DOLLAR{'$'}k is a $JULIA_STRING_DOLLAR{'$'}v")
		end
		x = 0
		while x < 4
			println(x)
			x += 1
		end
	""".trimIndent()
}
