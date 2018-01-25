package org.ice1000.julia.lang.editing

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.ice1000.julia.lang.psi.JuliaInteger

class JuliaAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is JuliaInteger -> {
				holder.createWarningAnnotation(element, "你妈炸了") // FIXME replace with your annotation
			}
		}
	}
}
