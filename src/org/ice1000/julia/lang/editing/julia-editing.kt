package org.ice1000.julia.lang.editing

import com.intellij.codeInsight.CodeInsightSettings
import com.intellij.codeInsight.editorActions.*
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate
import com.intellij.ide.IconProvider
import com.intellij.lang.*
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.lexer.Lexer
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.highlighter.HighlighterIterator
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidatorEx
import com.intellij.openapi.util.Ref
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.*
import com.intellij.psi.impl.cache.impl.*
import com.intellij.psi.impl.cache.impl.id.LexerBasedIdIndexer
import com.intellij.psi.impl.cache.impl.todo.LexerBasedTodoIndexer
import com.intellij.psi.impl.search.IndexPatternBuilder
import com.intellij.psi.search.UsageSearchContext
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.psi.*
import org.ice1000.julia.lang.psi.JuliaBlock
import org.ice1000.julia.lang.psi.impl.IJuliaFunctionDeclaration
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
			is JuliaMacro -> JuliaIcons.JULIA_MACRO_ICON
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
			BracePair(JuliaTypes.VERSION_START, JuliaTypes.VERSION_END, false),
			BracePair(JuliaTypes.BYTE_ARRAY_START, JuliaTypes.BYTE_ARRAY_END, false),
			BracePair(JuliaTypes.RAW_STR_START, JuliaTypes.RAW_STR_END, false),
			BracePair(JuliaTypes.CMD_QUOTE_START, JuliaTypes.CMD_QUOTE_END, false),
			BracePair(JuliaTypes.TRIPLE_QUOTE_START, JuliaTypes.TRIPLE_QUOTE_END, false),
			BracePair(JuliaTypes.LEFT_B_BRACKET, JuliaTypes.RIGHT_B_BRACKET, false),
			BracePair(JuliaTypes.LEFT_M_BRACKET, JuliaTypes.RIGHT_M_BRACKET, false)
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

class JuliaTodoIndexer : LexerBasedTodoIndexer() {
	override fun createLexer(consumer: OccurrenceConsumer): Lexer = JuliaIdIndexer.createIndexingLexer(consumer)
}

class JuliaIdIndexer : LexerBasedIdIndexer() {
	override fun createLexer(consumer: OccurrenceConsumer): Lexer {
		return createIndexingLexer(consumer)
	}

	companion object {
		fun createIndexingLexer(consumer: OccurrenceConsumer): Lexer {
			return JuliaFilterLexer(JuliaLexerAdapter(), consumer)
		}
	}
}

class JuliaFilterLexer(originalLexer: Lexer, table: OccurrenceConsumer) : BaseFilterLexer(originalLexer, table) {
	override fun advance() {
		scanWordsInToken(UsageSearchContext.IN_COMMENTS.toInt(), false, false)
		advanceTodoItemCountsInToken()
		myDelegate.advance()
	}
}

class JuliaTodoIndexPatternBuilder : IndexPatternBuilder {
	override fun getIndexingLexer(file: PsiFile): Lexer? = if (file is JuliaFile) JuliaLexerAdapter() else null
	override fun getCommentTokenSet(file: PsiFile): TokenSet? = if (file is JuliaFile) JuliaTokenType.COMMENTS else null
	override fun getCommentStartDelta(tokenType: IElementType?) = 0
	override fun getCommentEndDelta(tokenType: IElementType?) = 0
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
//		this is JuliaUnion ||
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
		is JuliaModuleDeclaration -> element.name.orEmpty()
		is IJuliaFunctionDeclaration -> element.toText
		is JuliaAbstractTypeDeclaration -> element.exprList.firstOrNull()?.text.orEmpty()
		is JuliaPrimitiveTypeDeclaration -> element.exprList.firstOrNull()?.text.orEmpty()
		is JuliaMacro -> "@${element.symbol?.text}()"
		is JuliaIfExpr -> "if ${element.expr?.text.orEmpty()}"
		is JuliaTryCatch -> "try"
		is JuliaFinallyClause -> "finally"
		is JuliaCatchClause -> "catch ${element.symbol}"
		is JuliaBlock,
		is JuliaColonBlock -> "block"
		is JuliaElseClause -> "else"
		is JuliaElseIfClause -> "elseif"
//		is JuliaUnion -> "union"
		is JuliaLet -> "let"
		is JuliaLambda -> "λ"
		is JuliaImportExpr -> "import ${element.children.firstOrNull { it is JuliaMemberAccess }?.text}"
		is JuliaImportAllExpr -> "importall ${element.memberAccess?.text ?: ""}"
		is JuliaUsing -> "using ${element.children.firstOrNull { it is JuliaMemberAccess }?.text}"
		is JuliaCompoundQuoteOp,
		is JuliaQuoteOp -> "quote"
		is JuliaSymbol -> element.text
		is JuliaForExpr -> "for ${element.children.mapNotNull {
			(it as? JuliaSingleIndexer)?.children?.firstOrNull()
				?: (it as? JuliaMultiIndexer)?.children?.firstOrNull()
		}.joinToString()}"
		is JuliaForComprehension -> "[ for |"
		is JuliaWhileExpr -> "while ${element.expr}"
		else -> "??"
	}, TEXT_MAX)

	override fun acceptElement(element: PsiElement) = element.acceptable()
}

object JuliaNameValidator : InputValidatorEx {
	override fun canClose(inputString: String?) = checkInput(inputString)
	override fun getErrorText(inputString: String?) = JuliaBundle.message("julia.actions.new-file.invalid", inputString.orEmpty())
	override fun checkInput(inputString: String?) = inputString?.run {
		all { it.isLetterOrDigit() || it in "_!" || it in '\u0100'..'\uFFFF' } && firstOrNull() != '!'
	}.orFalse()
}

class JuliaFindUsagesProvider : FindUsagesProvider {
	override fun getWordsScanner() = DefaultWordsScanner(JuliaLexerAdapter(),
		TokenSet.create(JuliaTypes.SYMBOL),
		JuliaTokenType.COMMENTS,
		JuliaTokenType.STRINGS)

	override fun getNodeText(element: PsiElement, useFullName: Boolean) =
		if (element.canBeNamed) element.presentText() else ""

	override fun getHelpId(psiElement: PsiElement): String? = null
	override fun getDescriptiveName(element: PsiElement) = if (element.canBeNamed) element.presentText() else ""
	override fun getType(element: PsiElement) = if (element.canBeNamed) element.text else ""
	override fun canFindUsagesFor(element: PsiElement) =
		element.let { it as? PsiNameIdentifierOwner }?.run { nameIdentifier as? JuliaSymbol }?.let { it.symbolKind.isDeclaration }.orFalse()
}

class JuliaRefactoringSupportProvider : RefactoringSupportProvider() {
	override fun isMemberInplaceRenameAvailable(element: PsiElement, context: PsiElement?) = true
}

/**
 * [JuliaBraceMatcher.isPairedBracesAllowedBeforeType] doesn't work!
 */
class JuliaTypedHandlerDelegate : TypedHandlerDelegate() {
	override fun beforeCharTyped(c: Char, project: Project, editor: Editor, file: PsiFile, fileType: FileType): Result {
		if (fileType != JuliaFileType) return Result.CONTINUE
		val offset = editor.caretModel.offset
		if (file.isWritable && c in "\"'`") {
			editor.document.insertString(offset, c.toString())
		}
		return Result.CONTINUE
	}
}

/**
 * Thanks to Kotlin Plugin.
 */
class JuliaPairBackspaceHandler : BackspaceHandlerDelegate() {
	/**
	 * [JuliaTypes.TRANSPOSE_SYM] is `'`, because char has no `_START` and `_END` Token.
	 * TODO: work as [JavaBackspaceHandler] to fix some bugs.
	 */
	override fun beforeCharDeleted(c: Char, file: PsiFile, editor: Editor) {
		if (c !in "\"`'(" || file !is JuliaFile || !CodeInsightSettings.getInstance().AUTOINSERT_PAIR_BRACKET) return
		val offset = editor.caretModel.offset
		val highlighter = (editor as EditorEx).highlighter
		val iterator = highlighter.createIterator(offset)
		if (iterator.tokenType != JuliaTypes.STRING_INTERPOLATE_END
			&& iterator.tokenType != JuliaTypes.QUOTE_END
			&& iterator.tokenType != JuliaTypes.TRANSPOSE_SYM
			&& iterator.tokenType != JuliaTypes.CMD_QUOTE_END) return
		iterator.retreat()
		if (iterator.tokenType != JuliaTypes.STRING_INTERPOLATE_START
			&& iterator.tokenType != JuliaTypes.QUOTE_START
			&& iterator.tokenType != JuliaTypes.TRANSPOSE_SYM
			&& iterator.tokenType != JuliaTypes.CMD_QUOTE_START) return

		if (offset + 1 > file.textLength) {
			editor.document.deleteString(offset, offset)
		} else
			editor.document.deleteString(offset, offset + 1)
	}

	override fun charDeleted(c: Char, file: PsiFile, editor: Editor): Boolean {
		return false
	}
}

/**
 * TODO: delete blanks and make s(t)m(u)a(p)r(i)t(d) indent. ( `|` is the cursor)
 * ```
 * if x>0
 *     dosth()
 *     |end
 * ```
 * become
 * ```
 * if x>0
 *     dosth()
 * end
 * ```
 */
class JuliaIndentBackspaceHandler : BackspaceHandlerDelegate() {
	override fun beforeCharDeleted(c: Char, file: PsiFile, editor: Editor) {
		if (c !in " \t" || file !is JuliaFile) return
		// TODO
		return
	}

	override fun charDeleted(c: Char, file: PsiFile, editor: Editor): Boolean {
		// TODO
		return false
	}
}

class JuliaEnterAfterUnmatchedEndHandler : EnterHandlerDelegate {
	override fun postProcessEnter(file: PsiFile, editor: Editor, dataContext: DataContext): EnterHandlerDelegate.Result {
		if (file !is JuliaFile) return EnterHandlerDelegate.Result.Continue
		val indentSize = 4

		val document = editor.document
		val caretModel = editor.caretModel
		val pos = caretModel.logicalPosition
		val offset = caretModel.offset
		val lineStartOffset = document.getLineStartOffset(pos.line)
		val lineEndOffset = document.getLineEndOffset(pos.line)
		val lineStringAfter = document.charsSequence.substring(offset, lineEndOffset)
		// `previous` is `current` before ENTER pressed.
		val previousLineStartOffset = document.getLineStartOffset(pos.line - 1)
		val previousLineEndOffset = document.getLineEndOffset(pos.line - 1)
		val previousLine = document.charsSequence.substring(previousLineStartOffset, previousLineEndOffset)

		if (lineStringAfter.afterIsDeletable()) { // end, else, elseif...
			val lineStart = document.charsSequence.substring(lineStartOffset, lineEndOffset + 4)
			return when {
				previousLine.trim().beforeIsIndentable() -> {
					EnterHandlerDelegate.Result.Continue
				}
				lineStart.isBlank() -> {
					document.deleteString(lineStartOffset, lineStartOffset + indentSize)
					EnterHandlerDelegate.Result.Stop
				}
				lineStart.startsWith('\t') -> {
					document.deleteString(lineStartOffset, lineStartOffset + 1)
					EnterHandlerDelegate.Result.Stop
				}
				else -> EnterHandlerDelegate.Result.Continue
			}
		}

		// insert indent when previousLine beforeIsIndentable to first line of indentStatements
		// note: `previousLine` is the `currentLine` before ENTER pressed.
		if (previousLine.trimStart().beforeIsIndentable()) {
			if (pos.line == 0) return EnterHandlerDelegate.Result.Continue
			val previousLineStart4Chars = document.charsSequence.substring(previousLineStartOffset, previousLineStartOffset + 4)
			// depends on previous' indent
			return when {
				previousLineStart4Chars.isBlank() -> {
					document.insertString(lineStartOffset, StringUtil.repeat(" ", indentSize))
					EnterHandlerDelegate.Result.Stop
				}
				previousLineStart4Chars.startsWith('\t') -> {
					document.insertString(lineStartOffset, "\t")
					EnterHandlerDelegate.Result.Stop
				}
				else -> {
					document.insertString(lineStartOffset, StringUtil.repeat(" ", indentSize))
					EnterHandlerDelegate.Result.Stop
				}
			}
		} else {
			return EnterHandlerDelegate.Result.Continue
		}
	}

	override fun preprocessEnter(file: PsiFile, editor: Editor, caretOffset: Ref<Int>, caretAdvance: Ref<Int>, dataContext: DataContext, originalHandler: EditorActionHandler?): EnterHandlerDelegate.Result {
		return EnterHandlerDelegate.Result.Continue
	}
}

private fun String.afterIsDeletable(): Boolean = this.trim() == "end"
	|| this.startsWith("elseif ") || this.startsWith("elseif(")
	|| this.trim() == ("else")

private fun String.beforeIsIndentable(): Boolean =
	this.startsWith("module ")
		|| this.startsWith("function ") || this.startsWith("primitive ")
		|| this.startsWith("type ") || this.startsWith("abstract ")
		|| this.startsWith("immutable ") || this.startsWith("mutable ")
		|| this.startsWith("if ") || this.startsWith("if(")
		|| this.startsWith("else")
		|| this.startsWith("elseif ") || this.startsWith("elseif(")
		|| this.startsWith("while ") || this.startsWith("while(")

/**
 * this class seems never used.
 */
class JuliaQuoteHandler : QuoteHandler {
	override fun isOpeningQuote(iterator: HighlighterIterator, offset: Int): Boolean {
		val tokenType = iterator.tokenType
		if (tokenType == JuliaTypes.STRING || tokenType == JuliaTypes.CHAR_LITERAL) {
			val start = iterator.start
			return offset == start
		}
		return false
	}

	override fun hasNonClosedLiteral(editor: Editor?, iterator: HighlighterIterator?, offset: Int): Boolean {
		return true
	}

	override fun isClosingQuote(iterator: HighlighterIterator, offset: Int): Boolean {
		val tokenType = iterator.tokenType

		if (tokenType == JuliaTypes.CHAR_LITERAL) {
			val start = iterator.start
			val end = iterator.end
			return end - start >= 1 && offset == end - 1
		} else if (tokenType == JuliaTypes.QUOTE_END || tokenType == JuliaTypes.CMD_QUOTE_END) {
			return true
		}
		return false
	}

	override fun isInsideLiteral(iterator: HighlighterIterator): Boolean {
		val tokenType = iterator.tokenType
		return tokenType == JuliaTypes.REGULAR_STRING_PART_LITERAL ||
			tokenType == JuliaTypes.STRING ||
			tokenType == JuliaTypes.STRING_INTERPOLATE_START ||
			tokenType == JuliaTypes.STRING_INTERPOLATE_END ||
			tokenType == JuliaTypes.STRING_UNICODE ||
			tokenType == JuliaTypes.STRING_ESCAPE ||
			tokenType == JuliaTypes.STRING_CONTENT
	}
}