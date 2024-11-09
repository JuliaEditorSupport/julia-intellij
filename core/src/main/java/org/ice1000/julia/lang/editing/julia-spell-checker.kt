package org.ice1000.julia.lang.editing

import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.spellchecker.inspections.BaseSplitter
import com.intellij.spellchecker.inspections.IdentifierSplitter
import com.intellij.spellchecker.tokenizer.*
import com.intellij.util.Consumer
import com.intellij.util.io.URLUtil
import org.ice1000.julia.lang.psi.JuliaString
import org.ice1000.julia.lang.psi.JuliaSymbol
import org.jdom.Verifier
import org.jetbrains.annotations.NonNls
import java.util.regex.Pattern

class JuliaSpellCheckingStrategy : SpellcheckingStrategy() {

	companion object {
		private val juliaWordsRegex by lazy {
			this::class.java.getResource("spelling.txt")
				.openStream()
				.bufferedReader().lineSequence().joinToString("|", prefix = "(", postfix = ")")
		}
	}

	private val tokenizer = TokenizerBase<PsiElement>(JuliaPlainTextSplitter)

	override fun getTokenizer(element: PsiElement): Tokenizer<PsiElement> = when (element) {
		is PsiComment,
		is JuliaSymbol -> tokenizer
		is JuliaString -> super.getTokenizer(element).takeIf { it != EMPTY_TOKENIZER } ?: tokenizer
		else -> EMPTY_TOKENIZER
	}

	object JuliaPlainTextSplitter : BaseSplitter() {
		override fun split(text: String?, range: TextRange, consumer: Consumer<TextRange>) {
			if (StringUtil.isEmpty(text)) {
				return
			}

			val substring = range.substring(text!!).replace('\b', '\n')
			if (Verifier.checkCharacterData(SPLIT_PATTERN.matcher(StringUtil.newBombedCharSequence(substring, DELAY)).replaceAll("")) != null) {
				return
			}

			val ws = JuliaTextSplitter
			var from = range.startOffset
			var till: Int
			val matcher = SPLIT_PATTERN.matcher(StringUtil.newBombedCharSequence(range.substring(text), DELAY))
			while (true) {
				BaseSplitter.checkCancelled()
				val toCheck: List<TextRange>
				val wRange: TextRange
				val word: String
				if (matcher.find()) {
					val found = BaseSplitter.matcherRange(range, matcher)
					till = found.startOffset
					if (BaseSplitter.badSize(from, till)) {
						continue
					}
					wRange = TextRange(from, till)
					word = wRange.substring(text)
					from = found.endOffset
				} else { // end hit or zero matches
					wRange = TextRange(from, range.endOffset)
					word = wRange.substring(text)
				}
				toCheck = when {
					word.contains("@") -> BaseSplitter.excludeByPattern(text, wRange, MAIL, 0)
					word.contains("://") -> BaseSplitter.excludeByPattern(text, wRange, URLUtil.URL_PATTERN, 0)
					else -> listOf(wRange)
				}
				for (r in toCheck) {
					ws.split(text, r, consumer)
				}
				if (matcher.hitEnd()) break
			}
		}

		private const val DELAY = 500L

		@NonNls
		private val SPLIT_PATTERN = Pattern.compile("(\\s|\b)")

		@NonNls
		private val MAIL = Pattern.compile("([\\p{L}0-9.\\-_+]+@([\\p{L}0-9\\-_]+(\\.)?)+(com|net|[a-z]{2})?)")
	}

	object JuliaTextSplitter : BaseSplitter() {

		override fun split(text: String?, range: TextRange, consumer: Consumer<TextRange>) {
			if (text == null || StringUtil.isEmpty(text)) {
				return
			}
			doSplit(text, range, consumer)
		}

		private fun doSplit(text: String, range: TextRange, consumer: Consumer<TextRange>) {
			val ws = JuliaWordSplitter
			val matcher = EXTENDED_WORD_AND_SPECIAL.matcher(StringUtil.newBombedCharSequence(text, 500))
			matcher.region(range.startOffset, range.endOffset)
			while (matcher.find()) {
				val found = TextRange(matcher.start(), matcher.end())
				ws.split(text, found, consumer)
			}
		}

		private val EXTENDED_WORD_AND_SPECIAL: Pattern = Pattern.compile("(&[^;]+;)|(([#]|0x[0-9]*)?\\p{L}+'?\\p{L}[_\\p{L}]*)")
	}

	object JuliaWordSplitter : BaseSplitter() {

		override fun split(text: String?, range: TextRange, consumer: Consumer<TextRange>) {
			if (text == null || range.length <= 1) {
				return
			}
			val specialMatcher = SPECIAL.matcher(StringUtil.newBombedCharSequence(text, 500))
			specialMatcher.region(range.startOffset, range.endOffset)
			if (specialMatcher.find()) {
				val found = TextRange(specialMatcher.start(), specialMatcher.end())
				BaseSplitter.addWord(consumer, true, found)
			} else {
				IdentifierSplitter.getInstance().split(text, range, consumer)
			}
		}

		private val SPECIAL = Pattern.compile("&\\p{Alnum}{2};?|#\\p{Alnum}{3,6}|0x\\p{Alnum}?|$juliaWordsRegex", Pattern.CASE_INSENSITIVE)
	}
}
