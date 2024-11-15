package org.ice1000.julia.lang;

import com.intellij.lang.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static org.ice1000.julia.lang.Julia_constantsKt.JULIA_EXTENSION;
import static org.ice1000.julia.lang.Julia_constantsKt.JULIA_LANGUAGE_NAME;

/**
 * WARNINGS:
 * - This file MUST NOT be converted to Kotlin!
 * - DO NOT replace <code>String NAME = "Julia"</code> with
 * <code>String NAME = JuliaBundle.message("julia.name")</code>
 * but static import JULIA_LANGUAGE_NAME.
 * <p>
 * ERRORS:
 * - Tests will be failed.
 * - LanguageType `language="Julia"` in plugin.xml will become red.
 *
 * @author zxj5470
 */
public final class JuliaLanguage extends Language {
	public static final @NotNull JuliaLanguage INSTANCE = new JuliaLanguage();

	private JuliaLanguage() {
		super(JULIA_LANGUAGE_NAME, "text/" + JULIA_EXTENSION);
	}

	@Override @Contract(pure = true) public boolean isCaseSensitive() {
		return false;
	}
}