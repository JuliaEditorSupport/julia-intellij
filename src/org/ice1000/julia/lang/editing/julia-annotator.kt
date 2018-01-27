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
					4 -> if (element.text[2] !in "ux") holder.createInfoAnnotation(element.textRange.narrow(1, 1), null)
							.textAttributes = JuliaHighlighter.CHAR_ESCAPE
					else holder.createErrorAnnotation(element.textRange.narrow(1, 1),
							JuliaBundle.message("julia.lint.invalid-char-escape"))
							.textAttributes = JuliaHighlighter.CHAR_ESCAPE_INVALID
				// '\x00'
					6 -> {
						if (element.text.trimQuotePair().matches(Regex(JULIA_CHAR_SINGLE_UNICODE_X_REGEX)))
							holder.createInfoAnnotation(element.textRange.narrow(1, 1), null).textAttributes = JuliaHighlighter.CHAR_ESCAPE
						else holder.createErrorAnnotation(element.textRange.narrow(1, 1), JuliaBundle.message("julia.lint.invalid-char-escape"))
								.textAttributes = JuliaHighlighter.CHAR_ESCAPE_INVALID
					}
				// '\u0022'
					8 -> {
						if(element.text.trimQuotePair().matches(Regex(JULIA_CHAR_SINGLE_UNICODE_U_REGEX)))
							holder.createInfoAnnotation(element.textRange.narrow(1, 1), null).textAttributes = JuliaHighlighter.CHAR_ESCAPE
						else holder.createErrorAnnotation(element.textRange.narrow(1, 1), JuliaBundle.message("julia.lint.invalid-char-escape"))
								.textAttributes = JuliaHighlighter.CHAR_ESCAPE_INVALID
					}
				// '\xe5\x86\xb0'
					14 -> {
						if (element.text.trimQuotePair().matches(Regex(JULIA_CHAR_TRIPLE_UNICODE_X_REGEX)))
							holder.createInfoAnnotation(element.textRange.narrow(1, 1), null).textAttributes = JuliaHighlighter.CHAR_ESCAPE
						else holder.createErrorAnnotation(element.textRange.narrow(1, 1), JuliaBundle.message("julia.lint.invalid-char-escape"))
								.textAttributes = JuliaHighlighter.CHAR_ESCAPE_INVALID
					}
					else -> holder.createErrorAnnotation(element.textRange.narrow(1, 1),
							JuliaBundle.message("julia.lint.invalid-char-escape"))
							.textAttributes = JuliaHighlighter.CHAR_ESCAPE_INVALID
				}
			is JuliaInteger -> {
				holder.createInfoAnnotation(element, null).textAttributes = JuliaHighlighter.NUMBER
			}
			is JuliaFloat -> {
				holder.createInfoAnnotation(element, null).textAttributes = JuliaHighlighter.NUMBER
			}
			is JuliaString -> {
				val str=element.text.trimQuotePair()
				fun markEscapeChars(escapeString:String,expandSize:Int,matchRegex:String){
					str.indicesOf(escapeString).forEach {
						if (it + expandSize < str.length) {
							val s = str.subSequence(it, it + expandSize)
							if (s.matches(Regex(matchRegex))) {
								holder.createInfoAnnotation(
										element.textRange.subRangeBeginOffsetAndLength(it+1,expandSize),
										null)
										.textAttributes = JuliaHighlighter.STRING_ESCAPE
							} else {
								holder.createErrorAnnotation(
										element.textRange.subRangeBeginOffsetAndLength(it+1,expandSize),
										JuliaBundle.message("julia.lint.invalid-char-escape"))
										.textAttributes = JuliaHighlighter.STRING_ESCAPE_INVALID
							}
						} else {
							holder.createErrorAnnotation(
									element.textRange.narrow(it+1,1),//to the end
									JuliaBundle.message("julia.lint.invalid-char-escape"))
									.textAttributes = JuliaHighlighter.STRING_ESCAPE_INVALID
						}
					}
				}
				markEscapeChars("\\x",4, JULIA_CHAR_SINGLE_UNICODE_X_REGEX)
				markEscapeChars("\\u",6, JULIA_CHAR_SINGLE_UNICODE_U_REGEX)
			}
		}
	}

}
