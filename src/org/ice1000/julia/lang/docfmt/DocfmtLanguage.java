package org.ice1000.julia.lang.docfmt;

import com.intellij.lang.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static org.ice1000.julia.lang.Julia_constantsKt.DOCFMT_EXTENSION;
import static org.ice1000.julia.lang.Julia_constantsKt.DOCFMT_LANGUAGE_NAME;

/**
 * Language support for https://github.com/ZacLN/DocumentFormat.jl
 *
 * @author ice1000
 */
public final class DocfmtLanguage extends Language {
	public static final @NotNull DocfmtLanguage INSTANCE = new DocfmtLanguage();

	private DocfmtLanguage() {
		super(DOCFMT_LANGUAGE_NAME, "text/" + DOCFMT_EXTENSION);
	}

	@Override @Contract(pure = true) public boolean isCaseSensitive() {
		return true;
	}
}
