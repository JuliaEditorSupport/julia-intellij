package org.ice1000.julia.lang.editing

import com.intellij.lang.Commenter
import org.ice1000.julia.lang.*

class JuliaCommenter : Commenter {
	override fun getCommentedBlockCommentPrefix() = blockCommentPrefix
	override fun getCommentedBlockCommentSuffix() = blockCommentSuffix
	override fun getBlockCommentPrefix() = JULIA_DOC_SURROUNDING
	override fun getBlockCommentSuffix() = JULIA_DOC_SURROUNDING
	override fun getLineCommentPrefix() = "# "
}