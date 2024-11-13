/*
 *     Julia language support plugin for Intellij-based IDEs.
 *     Copyright (C) 2024 julia-intellij contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.ice1000.julia.lang.editing

import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.julia.lang.JuliaFileType
import org.ice1000.julia.lang.psi.*
import org.ice1000.julia.lang.psi.JuliaTypes.EOL
import org.ice1000.julia.lang.psi.JuliaTypes.LINE_COMMENT

abstract class JuliaTemplateContextType private constructor(presentableName: String) :
	TemplateContextType(presentableName) {
	class Base : JuliaTemplateContextType("Julia") {
		override fun isInContext(element: PsiElement): Boolean = true
		override fun isCommentInContext(): Boolean = true
	}

	class Module : JuliaTemplateContextType("Module") {
		override fun isInContext(element: PsiElement) =
			PsiTreeUtil.getParentOfType(element, JuliaModuleDeclaration::class.java) != null
	}

	class Class : JuliaTemplateContextType("Class") {
		override fun isInContext(element: PsiElement): Boolean {
			return PsiTreeUtil.getParentOfType(element, JuliaTypeDeclaration::class.java) != null
		}
	}

	class Comment : JuliaTemplateContextType("Comment") {
		override fun isInContext(element: PsiElement): Boolean = false
		override fun isCommentInContext(): Boolean = true
	}

	class Function : JuliaTemplateContextType("Function") {
		override fun isInContext(element: PsiElement): Boolean {
			return PsiTreeUtil.getParentOfType(element, JuliaFunction::class.java) != null
				|| PsiTreeUtil.getParentOfType(element, JuliaCompactFunction::class.java) != null
		}
	}

	override fun isInContext(file: PsiFile, offset: Int): Boolean {
		if (file.fileType != JuliaFileType) return false

		val element = file.findElementAt(offset) ?: file.findElementAt(offset - 1) ?: return false

		return when {
			element.node.elementType == EOL -> false
			element is PsiWhiteSpace -> false
			element.parent is JuliaImportExpr -> false
			element.node.elementType == LINE_COMMENT -> isCommentInContext()

			else -> isInContext(element)
		}
	}

	abstract fun isInContext(element: PsiElement): Boolean

	open fun isCommentInContext(): Boolean = false
}