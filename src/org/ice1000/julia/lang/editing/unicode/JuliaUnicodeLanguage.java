package org.ice1000.julia.lang.editing.unicode;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;

import static org.ice1000.julia.lang.Julia_constantsKt.JULIA_UNICODE_LANGUAGE_NAME;

/**
 * @author ice1000
 */
public class JuliaUnicodeLanguage extends Language {
	public static final @NotNull JuliaUnicodeLanguage INSTANCE = new JuliaUnicodeLanguage();

	@Override public @NotNull LanguageFileType getAssociatedFileType() {
		return JuliaUnicodeFileType.INSTANCE;
	}

	private JuliaUnicodeLanguage() {
		super(JULIA_UNICODE_LANGUAGE_NAME);
	}
}
