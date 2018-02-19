package org.ice1000.julia.lang.editing

import com.intellij.codeInsight.editorActions.*
import com.intellij.ide.IconProvider
import com.intellij.lang.*
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.findUsages.EmptyFindUsagesProvider
import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.openapi.editor.*
import com.intellij.openapi.fileTypes.*
import com.intellij.openapi.project.*
import com.intellij.openapi.ui.InputValidatorEx
import com.intellij.psi.*
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy
import com.intellij.spellchecker.tokenizer.Tokenizer
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.action.*
import org.ice1000.julia.lang.psi.*
import org.ice1000.julia.lang.psi.JuliaBlock
import javax.swing.Icon

class JuliaIconProvider : IconProvider() {
	override fun getIcon(element: PsiElement, flags: Int): Icon? {
		val file = element as? JuliaFile ?: return null
		val statements = file.children
			.firstOrNull { it is JuliaStatements }
			?.let { it as? JuliaStatements } ?: return JuliaIcons.JULIA_ICON
		val validChildren = statements.children.filterNot {
			it is JuliaString ||
				it is JuliaInteger ||
				it is JuliaUsing ||
				it is JuliaImportAllExpr ||
				it is JuliaImportExpr
		}
		if (validChildren.size != 1) return JuliaIcons.JULIA_ICON
		return when (validChildren.first()) {
			is JuliaModuleDeclaration -> JuliaIcons.JULIA_MODULE_ICON
			is JuliaPrimitiveTypeDeclaration,
			is JuliaAbstractTypeDeclaration,
			is JuliaTypeDeclaration -> JuliaIcons.JULIA_TYPE_ICON
			is JuliaFunction -> JuliaIcons.JULIA_FUNCTION_ICON
			else -> JuliaIcons.JULIA_ICON
		}
	}
}

class JuliaBraceMatcher : PairedBraceMatcher {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(JuliaTypes.LEFT_BRACKET, JuliaTypes.RIGHT_BRACKET, false),
			BracePair(JuliaTypes.COLON_BEGIN_SYM, JuliaTypes.RIGHT_BRACKET, false),
			BracePair(JuliaTypes.STRING_INTERPOLATE_START, JuliaTypes.STRING_INTERPOLATE_END, false),
			BracePair(JuliaTypes.BLOCK_COMMENT_START, JuliaTypes.BLOCK_COMMENT_END, false),
			BracePair(JuliaTypes.QUOTE_START, JuliaTypes.QUOTE_END, false),
			BracePair(JuliaTypes.REGEX_START, JuliaTypes.REGEX_END, false),
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
		is JuliaFunction -> element.toText
		is JuliaCompactFunction -> "${element.exprList.firstOrNull()?.text}()"
		is JuliaAbstractTypeDeclaration -> element.exprList.firstOrNull()?.text.orEmpty()
		is JuliaPrimitiveTypeDeclaration -> element.exprList.firstOrNull()?.text.orEmpty()
		is JuliaMacro -> "@${element.symbol?.text}()"
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

object JuliaNameValidator : InputValidatorEx {
	override fun canClose(inputString: String?) = checkInput(inputString)
	override fun getErrorText(inputString: String?) = JuliaBundle.message("julia.actions.new-file.invalid", inputString.orEmpty())
	override fun checkInput(inputString: String?) = inputString?.run {
		all { it.isLetterOrDigit() || it in "_!" || it in '\u0100'..'\u9999' } && firstOrNull() != '!'
	} == true
}

class JuliaFindUsagesProvider : EmptyFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(JuliaLexerAdapter(),
		TokenSet.create(JuliaTypes.SYMBOL),
		JuliaTokenType.COMMENTS,
		JuliaTokenType.STRINGS)

	override fun getNodeText(element: PsiElement, useFullName: Boolean) =
		if (element.canBeNamed) element.presentText() else ""

	override fun getDescriptiveName(element: PsiElement) = if (element.canBeNamed) element.presentText() else ""
	override fun getType(element: PsiElement) = if (element.canBeNamed) element.text else ""
	override fun canFindUsagesFor(psiElement: PsiElement) = psiElement is PsiNamedElement
}

class JuliaRefactoringSupportProvider : RefactoringSupportProvider() {
	override fun isMemberInplaceRenameAvailable(element: PsiElement, context: PsiElement?) = true
}

@Deprecated("This class is deprecated because we use action instead of TypedHandler",ReplaceWith("org.ice1000.julia.lang.action.JuliaUnicodeInputAction"))
class JuliaTypedHandlerDelegate : TypedHandlerDelegate() {
	override fun beforeCharTyped(c: Char, project: Project, editor: Editor, file: PsiFile, fileType: FileType): Result {
		if (fileType != JuliaFileType)
			return Result.CONTINUE
		else if (c == '\\') {
			val offset = editor.caretModel.offset
			val psiElement = file.findElementAt(offset - 1)
			val type = psiElement?.node?.elementType
			val popupTokensArray = arrayOf(
				JuliaTypes.EOL,
				TokenType.WHITE_SPACE,
				JuliaTypes.SYM
			)
			if (type in popupTokensArray) {
				JuliaUnicodeInputAction.actionInvoke(editor, project)
				return Result.STOP
			}
		}
		return Result.CONTINUE
	}
}