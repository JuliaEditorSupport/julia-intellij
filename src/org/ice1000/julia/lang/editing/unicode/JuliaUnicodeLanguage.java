package org.ice1000.julia.lang.editing.unicode;

import com.intellij.lang.Language;

import static org.ice1000.julia.lang.Julia_constantsKt.JULIA_UNICODE_LANGUAGE_NAME;

/**
 * @author ice1000
 */
public class JuliaUnicodeLanguage extends Language {
	public static final JuliaUnicodeLanguage INSTANCE = new JuliaUnicodeLanguage();

	private JuliaUnicodeLanguage() {
		super(JULIA_UNICODE_LANGUAGE_NAME);
	}
}
