package org.ice1000.julia.lang.editing

import com.intellij.lang.*
import com.intellij.psi.*
import com.intellij.psi.tree.IElementType
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy
import com.intellij.spellchecker.tokenizer.Tokenizer
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.psi.*
import org.ice1000.julia.lang.psi.JuliaBlock

class JuliaBraceMatcher : PairedBraceMatcher {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(JuliaTypes.LEFT_BRACKET, JuliaTypes.RIGHT_BRACKET, false),
			BracePair(JuliaTypes.COLON_BEGIN_SYM, JuliaTypes.RIGHT_BRACKET, false),
			BracePair(JuliaTypes.STRING_INTERPOLATE_START, JuliaTypes.STRING_INTERPOLATE_END, false),
			BracePair(JuliaTypes.BLOCK_COMMENT_START, JuliaTypes.BLOCK_COMMENT_END, false),
			BracePair(JuliaTypes.QUOTE_START, JuliaTypes.QUOTE_END, false),
			BracePair(JuliaTypes.CMD_QUOTE_START, JuliaTypes.CMD_QUOTE_END, false),
			BracePair(JuliaTypes.TRIPLE_QUOTE_START, JuliaTypes.TRIPLE_QUOTE_END, false),
			BracePair(JuliaTypes.LEFT_B_BRACKET, JuliaTypes.RIGHT_B_BRACKET, false),
			BracePair(JuliaTypes.LEFT_M_BRACKET, JuliaTypes.RIGHT_M_BRACKET, false),
			BracePair(JuliaTypes.TRY_KEYWORD, JuliaTypes.END_KEYWORD, false),
			BracePair(JuliaTypes.MODULE_KEYWORD, JuliaTypes.END_KEYWORD, false),
			BracePair(JuliaTypes.FUNCTION_KEYWORD, JuliaTypes.END_KEYWORD, false),
			BracePair(JuliaTypes.IF_KEYWORD, JuliaTypes.END_KEYWORD, false),
			BracePair(JuliaTypes.WHILE_KEYWORD, JuliaTypes.END_KEYWORD, false),
			BracePair(JuliaTypes.FOR_KEYWORD, JuliaTypes.END_KEYWORD, false),
			BracePair(JuliaTypes.DO_KEYWORD, JuliaTypes.END_KEYWORD, false),
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
		is JuliaSymbol -> TEXT_TOKENIZER
		is JuliaString -> super.getTokenizer(element).takeIf { it != EMPTY_TOKENIZER } ?: TEXT_TOKENIZER
		else -> EMPTY_TOKENIZER
	}
}

const val TEXT_MAX = 16
const val LONG_TEXT_MAX = 24
fun cutText(it: String, textMax: Int) = if (it.length <= textMax) it else "${it.take(textMax)}…"

class JuliaBreadCrumbsProvider : BreadcrumbsProvider {
	private fun PsiElement.acceptable() = this is JuliaTypeDeclaration ||
		this is JuliaModuleDeclaration ||
		this is JuliaFunction ||
		this is JuliaCompactFunction ||
		this is JuliaAbstractTypeDeclaration ||
		this is JuliaPrimitiveTypeDeclaration ||
		this is JuliaMacro ||
		this is JuliaIfExpr ||
		this is JuliaTryCatch ||
		this is JuliaFinallyClause ||
		this is JuliaApplyFunctionOp ||
		this is JuliaApplyMacroOp ||
		this is JuliaApplyIndexOp ||
		this is JuliaApplyWhereOp ||
		this is JuliaBlock ||
		this is JuliaElseClause ||
		this is JuliaElseIfClause ||
		this is JuliaUnion ||
		this is JuliaBreakExpr ||
		this is JuliaContinueExpr ||
		this is JuliaReturnExpr ||
		this is JuliaLet ||
		this is JuliaLambda ||
		this is JuliaImportExpr ||
		this is JuliaImportAllExpr ||
		this is JuliaUsing ||
		this is JuliaCompoundQuoteOp ||
		this is JuliaColonBlock ||
		this is JuliaSymbol ||
		this is JuliaForExpr ||
		this is JuliaForComprehension ||
		this is JuliaWhileExpr

	override fun getLanguages() = arrayOf(JuliaLanguage.INSTANCE)
	override fun getElementInfo(element: PsiElement) = cutText(when (element) {
		is JuliaTypeDeclaration -> "type ${element.exprList.firstOrNull()}"
		is JuliaModuleDeclaration -> ""
		is JuliaFunction -> ""
		is JuliaCompactFunction -> ""
		is JuliaAbstractTypeDeclaration -> ""
		is JuliaPrimitiveTypeDeclaration -> ""
		is JuliaMacro -> ""
		is JuliaIfExpr -> ""
		is JuliaTryCatch -> ""
		is JuliaFinallyClause -> ""
		is JuliaApplyFunctionOp -> ""
		is JuliaApplyMacroOp -> ""
		is JuliaApplyIndexOp -> ""
		is JuliaApplyWhereOp -> ""
		is JuliaBlock -> ""
		is JuliaElseClause -> ""
		is JuliaElseIfClause -> ""
		is JuliaUnion -> ""
		is JuliaBreakExpr -> ""
		is JuliaContinueExpr -> ""
		is JuliaReturnExpr -> ""
		is JuliaLet -> ""
		is JuliaLambda -> ""
		is JuliaImportExpr -> ""
		is JuliaImportAllExpr -> ""
		is JuliaUsing -> ""
		is JuliaCompoundQuoteOp -> ""
		is JuliaColonBlock -> ""
		is JuliaSymbol -> ""
		is JuliaForExpr -> ""
		is JuliaForComprehension -> ""
		is JuliaWhileExpr -> ""
		else -> ""
	}, TEXT_MAX)

	override fun acceptElement(element: PsiElement) = element.acceptable()
}
