package org.ice1000.julia.lang.editing

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.ice1000.julia.lang.psi.*

class JuliaAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is JuliaInteger -> {
				holder.createWarningAnnotation(element, "integer")
			}
			is JuliaFloat -> {
				holder.createInfoAnnotation(element, "float")
			}
			is JuliaString -> {
				holder.createInfoAnnotation(element, " string ♂ ")
			}
			is JuliaFunctionDeclaration -> {
				holder.createInfoAnnotation(element, "FuncDef")
			}
			is JuliaIfStatement -> {
				holder.createInfoAnnotation(element, "if statement `if x < y ` ")
			}
			is JuliaForStatement -> {
				holder.createInfoAnnotation(element, "for statement format: `for i in `")
			}
			is JuliaWhileStatement -> {

			}
			is JuliaTypeName->{
				println(element)
				holder.createInfoAnnotation(element, "A type name")
			}
			is JuliaSymbol->{
				println(element)
				holder.createInfoAnnotation(element, "A symbol")
			}
			is JuliaComment -> {
				holder.createInfoAnnotation(element, "it is a comment") // it doesn't work???
			}
		}
	}
}
