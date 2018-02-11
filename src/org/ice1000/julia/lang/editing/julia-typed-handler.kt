package org.ice1000.julia.lang.editing

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import org.ice1000.julia.lang.JuliaFileType
import org.ice1000.julia.lang.action.JuliaUnicodeInputAction
import org.ice1000.julia.lang.psi.JuliaTypes

/**
 * a temp file...
 * It can be mixed into julia-editing.kt
 */
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
			} else {
				println("do not popup")
			}
		}
		return Result.CONTINUE
	}
}