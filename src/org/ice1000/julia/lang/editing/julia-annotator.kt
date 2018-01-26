package org.ice1000.julia.lang.editing

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.ice1000.julia.lang.JuliaHighlighter
import org.ice1000.julia.lang.psi.*

class JuliaAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is JuliaTypeName -> holder.createInfoAnnotation(element, null)
					.textAttributes = JuliaHighlighter.TYPE_NAME
			is JuliaFunctionName -> holder.createInfoAnnotation(element, null)
					.textAttributes = JuliaHighlighter.FUNCTION_NAME
			is JuliaAbstractTypeName -> holder.createInfoAnnotation(element, null)
					.textAttributes = JuliaHighlighter.ABSTRACT_TYPE_NAME
			is JuliaModuleName -> holder.createInfoAnnotation(element, null)
					.textAttributes = JuliaHighlighter.MODULE_NAME
			is JuliaChar -> when (element.textLength) {
			// example: '\n'
				4 -> if (element.text[1] !in "ux") holder.createInfoAnnotation(element, null)
						.textAttributes = JuliaHighlighter.CHAR_ESCAPE
				else holder.createErrorAnnotation(TextRange(element.textRange.startOffset + 1, element.textRange.endOffset - 1), null)
						.textAttributes = JuliaHighlighter.CHAR_ESCAPE_INVALID
			}
			is JuliaInteger -> {
				// TODO provide numerical conversions
			}
			is JuliaFloat -> {
				// TODO provide numerical conversions
			}
			is JuliaString -> {
				holder.createInfoAnnotation(element, " string ♂ ")
			}
		}
	}
}
