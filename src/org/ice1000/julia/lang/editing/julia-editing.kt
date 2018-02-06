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
		this is JuliaCatchClause ||
		this is JuliaFinallyClause ||
		this is JuliaBlock ||
		this is JuliaElseClause ||
		this is JuliaElseIfClause ||
		this is JuliaUnion ||
		this is JuliaLet ||
		this is JuliaLambda ||
		this is JuliaImportExpr ||
		this is JuliaImportAllExpr ||
		this is JuliaUsing ||
		this is JuliaCompoundQuoteOp ||
		this is JuliaQuoteOp ||
		this is JuliaColonBlock ||
		this is JuliaSymbol ||
		this is JuliaForExpr ||
		this is JuliaForComprehension ||
		this is JuliaWhileExpr

	override fun getLanguages() = arrayOf(JuliaLanguage.INSTANCE)
	override fun getElementInfo(element: PsiElement) = cutText(when (element) {
		is JuliaTypeDeclaration -> element.exprList.firstOrNull()?.text.orEmpty()
		is JuliaModuleDeclaration -> element.symbol.text
		is JuliaFunction -> "${element.exprList.firstOrNull()?.text}()"
		is JuliaCompactFunction -> "${element.exprList.firstOrNull()?.text}()"
		is JuliaAbstractTypeDeclaration -> element.exprList.firstOrNull()?.text.orEmpty()
		is JuliaPrimitiveTypeDeclaration -> element.exprList.firstOrNull()?.text.orEmpty()
		is JuliaMacro -> "@${element.symbolList.firstOrNull()?.text}()"
		is JuliaIfExpr -> "if ${element.statements.exprList.firstOrNull()?.text}"
		is JuliaTryCatch -> "try"
		is JuliaFinallyClause -> "finally"
		is JuliaCatchClause -> "catch ${element.symbol}"
		is JuliaBlock,
		is JuliaColonBlock -> "block"
		is JuliaElseClause -> "else"
		is JuliaElseIfClause -> "elseif"
		is JuliaUnion -> "union"
		is JuliaLet -> "let"
		is JuliaLambda -> "λ"
		is JuliaImportExpr -> "import ${element.exprList.firstOrNull()?.text}"
		is JuliaImportAllExpr -> "importall ${element.expr.text}"
		is JuliaUsing -> "using ${element.exprList.firstOrNull()?.text}"
		is JuliaCompoundQuoteOp -> "quote"
		is JuliaQuoteOp -> "quote"
		is JuliaSymbol -> element.text
		is JuliaForExpr -> "for ${(element.multiIndexer ?: element.singleIndexer)?.text}"
		is JuliaForComprehension -> "[ ${element.exprList.firstOrNull()?.text} for |"
		is JuliaWhileExpr -> "while ${element.expr}"
		else -> "??"
	}, TEXT_MAX)

	override fun acceptElement(element: PsiElement) = element.acceptable()
}
