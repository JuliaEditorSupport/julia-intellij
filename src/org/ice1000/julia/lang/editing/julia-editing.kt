package org.ice1000.julia.lang.editing

import com.intellij.lang.*
import com.intellij.psi.*
import com.intellij.psi.tree.IElementType
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy
import com.intellij.spellchecker.tokenizer.Tokenizer
import org.ice1000.julia.lang.JULIA_BLOCK_COMMENT_BEGIN
import org.ice1000.julia.lang.JULIA_BLOCK_COMMENT_END
import org.ice1000.julia.lang.psi.*

class JuliaBraceMatcher : PairedBraceMatcher {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(JuliaTypes.LEFT_BRACKET, JuliaTypes.RIGHT_BRACKET, false),
			BracePair(JuliaTypes.STRING_INTERPOLATE_START, JuliaTypes.RIGHT_BRACKET, false),
			BracePair(JuliaTypes.LEFT_B_BRACKET, JuliaTypes.RIGHT_B_BRACKET, false),
			BracePair(JuliaTypes.LEFT_M_BRACKET, JuliaTypes.RIGHT_M_BRACKET, false),
			BracePair(JuliaTypes.TRY_KEYWORD, JuliaTypes.END_KEYWORD, false),
			BracePair(JuliaTypes.MODULE_KEYWORD, JuliaTypes.END_KEYWORD, false),
			BracePair(JuliaTypes.FUNCTION_KEYWORD, JuliaTypes.END_KEYWORD, false),
			BracePair(JuliaTypes.IF_KEYWORD, JuliaTypes.END_KEYWORD, false),
			BracePair(JuliaTypes.WHILE_KEYWORD, JuliaTypes.END_KEYWORD, false),
			BracePair(JuliaTypes.FOR_KEYWORD, JuliaTypes.END_KEYWORD, false),
			BracePair(JuliaTypes.BEGIN_KEYWORD, JuliaTypes.END_KEYWORD, false),
			BracePair(JuliaTypes.LET_KEYWORD, JuliaTypes.END_KEYWORD, false),
			BracePair(JuliaTypes.MACRO_KEYWORD, JuliaTypes.END_KEYWORD, false),
			BracePair(JuliaTypes.TYPE_KEYWORD, JuliaTypes.END_KEYWORD, false),
			BracePair(JuliaTypes.QUOTE_KEYWORD, JuliaTypes.END_KEYWORD, false),
			BracePair(JuliaTypes.STRUCT_KEYWORD, JuliaTypes.END_KEYWORD, false)
		)
	}

	override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int) = openingBraceOffset
	override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?) = true
	override fun getPairs() = PAIRS
}

class JuliaCommenter : Commenter {
	override fun getCommentedBlockCommentPrefix() = blockCommentPrefix
	override fun getCommentedBlockCommentSuffix() = blockCommentSuffix
	override fun getBlockCommentPrefix() = JULIA_BLOCK_COMMENT_BEGIN
	override fun getBlockCommentSuffix() = JULIA_BLOCK_COMMENT_END
	override fun getLineCommentPrefix() = "# "
}

class JuliaSpellCheckingStrategy : SpellcheckingStrategy() {
	override fun getTokenizer(element: PsiElement): Tokenizer<PsiElement> = when (element) {
		is PsiComment,
		is JuliaFunctionName,
		is JuliaTypeName,
		is JuliaSymbol -> TEXT_TOKENIZER
		is JuliaString -> super.getTokenizer(element).takeIf { it != EMPTY_TOKENIZER } ?: TEXT_TOKENIZER
		else -> EMPTY_TOKENIZER
	}
}
