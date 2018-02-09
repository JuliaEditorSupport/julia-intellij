package org.ice1000.julia.lang.docfmt

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.lang.Commenter
import com.intellij.openapi.fileTypes.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider
import com.intellij.psi.tree.IElementType
import icons.JuliaIcons
import org.ice1000.julia.lang.*
import org.ice1000.julia.lang.docfmt.psi.DocfmtTypes

object DocfmtFileType : LanguageFileType(DocfmtLanguage.INSTANCE) {
	override fun getDefaultExtension() = DOCFMT_EXTENSION
	override fun getName() = JuliaBundle.message("docfmt.name")
	override fun getIcon() = JuliaIcons.DOCFMT_ICON
	override fun getDescription() = JuliaBundle.message("docfmt.name.description")
}

class DocfmtFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, DocfmtLanguage.INSTANCE) {
	val existing = BooleanArray(10)
	override fun getFileType() = DocfmtFileType
	override fun subtreeChanged() {
		for (i in existing.indices) existing[i] = false
		super.subtreeChanged()
	}
}

object DocfmtHighlighter : SyntaxHighlighter {
	override fun getHighlightingLexer() = DocfmtLexerAdapter()
	override fun getTokenHighlights(tokenType: IElementType?) = when (tokenType) {
		DocfmtTypes.EQ_SYM -> JuliaHighlighter.ASSIGNMENT_OPERATOR_KEY
		DocfmtTypes.LINE_COMMENT -> JuliaHighlighter.COMMENT_KEY
		DocfmtTypes.INT -> JuliaHighlighter.NUMBER_KEY
		else -> emptyArray()
	}
}

class DocfmtHighlighterFactory : SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = DocfmtHighlighter
}

class DocfmtCommenter : Commenter {
	override fun getCommentedBlockCommentPrefix() = blockCommentPrefix
	override fun getCommentedBlockCommentSuffix() = blockCommentSuffix
	override fun getBlockCommentPrefix(): String? = null
	override fun getBlockCommentSuffix(): String? = null
	override fun getLineCommentPrefix() = "# "
}
