/*
 * WARNINGS:
 * - This file MUST NOT be converted to Kotlin!
 * - DO NOT replace <code>String NAME = "Julia"</code> with
 *      <code>String NAME = JuliaBundle.message("julia.name")</code>
 *      but static import JULIA_LANGUAGE_NAME.
 *
 * ERRORS:
 * - Tests will be failed.
 * - LanguageType `language="Julia"` in plugin.xml will become red.
 *
 * @author: zxj5470
 * @date: 2018-01-27
 */
package org.ice1000.julia.lang;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;
import static org.ice1000.julia.lang.Julia_constantsKt.JULIA_LANGUAGE_NAME;

public class JuliaLanguage extends Language {
	public static final JuliaLanguage INSTANCE = new JuliaLanguage();
	private static final String NAME = JULIA_LANGUAGE_NAME;

	private JuliaLanguage() {
		super(NAME);
	}

	@NotNull
	public String getDisplayName() {
		return NAME;
	}

	public boolean isCaseSensitive() {
		return true;
	}
}