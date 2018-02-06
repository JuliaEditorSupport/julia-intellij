package org.ice1000.julia.lang.docfmt

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.Commenter
import com.intellij.patterns.PlatformPatterns.psiElement
import org.ice1000.julia.lang.docfmt.psi.DocfmtTypes
import org.ice1000.julia.lang.editing.JuliaBasicCompletionContributor

class DocfmtCommenter : Commenter {
	override fun getCommentedBlockCommentPrefix() = blockCommentPrefix
	override fun getCommentedBlockCommentSuffix() = blockCommentSuffix
	override fun getBlockCommentPrefix(): String? = null
	override fun getBlockCommentSuffix(): String? = null
	override fun getLineCommentPrefix() = "# "
}

class DocfmtCompletionContributor : CompletionContributor() {
	private companion object CompletionHolder {
		private val KEYS = listOf(
			"TabWidth",
			"UseTab",
			"IndentWidth",
			"AlignAfterOpenBracket"
		).map(LookupElementBuilder::create)
		private val VALUES = listOf(
			"Align",
			"true",
			"false"
		).map(LookupElementBuilder::create)
	}

	init {
		extend(
			CompletionType.BASIC,
			psiElement()
				.afterLeaf("\n")
				.beforeLeaf(psiElement(DocfmtTypes.EQ_SYM)),
			JuliaBasicCompletionContributor.JuliaCompletionProvider(KEYS))
		extend(
			CompletionType.BASIC,
			psiElement()
				.afterLeaf("=")
				.beforeLeaf(psiElement(DocfmtTypes.EOL)),
			JuliaBasicCompletionContributor.JuliaCompletionProvider(VALUES))
	}
}
