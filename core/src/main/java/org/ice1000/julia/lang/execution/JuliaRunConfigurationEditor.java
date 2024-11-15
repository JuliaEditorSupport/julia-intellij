package org.ice1000.julia.lang.execution;

import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.ComboboxWithBrowseButton;
import com.intellij.ui.RawCommandLineEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class JuliaRunConfigurationEditor extends SettingsEditor<JuliaRunConfiguration> {
	protected @NotNull JPanel mainPanel;
	protected @NotNull TextFieldWithBrowseButton targetFileField;
	protected @NotNull ComboboxWithBrowseButton juliaExeField;
	protected @NotNull TextFieldWithBrowseButton workingDirField;
	protected @NotNull TextFieldWithBrowseButton systemImageField;
	protected @NotNull JCheckBox inlineCheckBox;
	protected @NotNull JCheckBox checkBoundsCheckBox;
	protected @NotNull JCheckBox colorCheckBox;
	protected @NotNull JCheckBox historyCheckBox;
	protected @NotNull JCheckBox unsafeFloatCheckBox;
	protected @NotNull JCheckBox handleSignalCheckBox;
	protected @NotNull JCheckBox startupFileCheckBox;
	protected @NotNull JCheckBox launchReplCheckBox;
	protected @NotNull JCheckBox systemImageCheckBox;
	protected @NotNull RawCommandLineEditor programArgumentsField;
	protected @NotNull RawCommandLineEditor additionalOptionsField;
	protected @NotNull ComboBox<String> optimizationLevelComboBox; // --optimize
	protected @NotNull ComboBox<String> jitCompilerOptions; // --compile
	protected @NotNull ComboBox<String> depWarnOptions;
	protected @NotNull ComboBox<String> codeCovOptions;
	protected @NotNull ComboBox<String> trackAllocOptions;
}
