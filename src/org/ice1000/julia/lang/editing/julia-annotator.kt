package org.ice1000.julia.lang.editing

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.ice1000.julia.lang.*
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
			// 0, 1, 2 are impossible, 3: 'a'
				0, 1, 2, 3 -> {
				}
			// '\n'
				4 -> if (element.text[1] !in "ux") holder.createInfoAnnotation(element, null)
						.textAttributes = JuliaHighlighter.CHAR_ESCAPE
				else holder.createErrorAnnotation(element.textRange.narrow(1, 1), JuliaBundle.message("julia.lint.invalid-char-escape"))
						.textAttributes = JuliaHighlighter.CHAR_ESCAPE_INVALID
			// '\x00'
				6 -> {
					// TODO do validation
				}
			// '\u0022'
				8 -> {
					// TODO do validation
				}
				else -> holder.createErrorAnnotation(element.textRange.narrow(1, 1), JuliaBundle.message("julia.lint.invalid-char-escape"))
						.textAttributes = JuliaHighlighter.CHAR_ESCAPE_INVALID
			}
			is JuliaInteger -> {
				// TODO provide numerical conversions
			}
			is JuliaFloat -> {
				// TODO provide numerical conversions
			}
			is JuliaString -> {
				// TODO do validation
			}
		}
	}
}
