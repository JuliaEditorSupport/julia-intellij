package org.ice1000.julia.lang.docfmt

import com.intellij.lang.Commenter

class DocfmtCommenter : Commenter {
	override fun getCommentedBlockCommentPrefix() = blockCommentPrefix
	override fun getCommentedBlockCommentSuffix() = blockCommentSuffix
	override fun getBlockCommentPrefix(): String? = null
	override fun getBlockCommentSuffix(): String? = null
	override fun getLineCommentPrefix() = "# "
}


