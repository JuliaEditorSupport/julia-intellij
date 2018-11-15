package org.ice1000.julia.lang.module.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.ComboboxWithBrowseButton;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.labels.LinkLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class JuliaProjectConfigurable implements Configurable {
	protected @NotNull JPanel mainPanel;
	protected @NotNull JFormattedTextField textLimitField;
	protected @NotNull JFormattedTextField timeLimitField;
	protected @NotNull TextFieldWithBrowseButton importPathField;
	protected @NotNull ComboboxWithBrowseButton juliaExeField;
	protected @NotNull LinkLabel<Object> juliaWebsite;
	protected @NotNull JLabel version;
	protected @NotNull TextFieldWithBrowseButton basePathField;
	protected @NotNull JButton installAutoFormatButton;
	protected @NotNull JBCheckBox unicodeInputCheckBox;
	protected @NotNull JBCheckBox showEvalHintCheckBox;
	protected @NotNull JBCheckBox globalUnicodeCheckBox;
	protected @NotNull LinkLabel<Object> refreshButton;
	protected @NotNull JFormattedTextField maxCharacterToConvertToCompact;
}
