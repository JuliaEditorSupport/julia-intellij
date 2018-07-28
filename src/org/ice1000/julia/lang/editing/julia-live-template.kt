package org.ice1000.julia.lang.editing

import com.intellij.codeInsight.template.EverywhereContextType
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.julia.lang.psi.JuliaImportExpr
import org.ice1000.julia.lang.psi.JuliaModuleDeclaration
import org.ice1000.julia.lang.psi.JuliaTypes.EOL
import org.ice1000.julia.lang.psi.JuliaTypes.LINE_COMMENT
import kotlin.reflect.KClass

abstract class JuliaTemplateContextType private constructor(
	id: String,
	presentableName: String,
	baseContextType: KClass<out TemplateContextType>?
) : TemplateContextType(id, presentableName, baseContextType?.java) {
	class Base : JuliaTemplateContextType("JULIA", "Julia", EverywhereContextType::class) {
		override fun isInContext(element: PsiElement): Boolean = true
		override fun isCommentInContext(): Boolean = true
	}

	class Module : JuliaTemplateContextType("JULIA_MODULE", "Module", Base::class) {
		override fun isInContext(element: PsiElement) =
			PsiTreeUtil.getParentOfType(element, JuliaModuleDeclaration::class.java) != null
	}

	override fun isInContext(file: PsiFile, offset: Int): Boolean {
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