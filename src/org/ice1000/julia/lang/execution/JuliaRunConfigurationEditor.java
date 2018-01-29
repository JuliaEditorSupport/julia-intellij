package org.ice1000.julia.lang.execution;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.ice1000.julia.lang.JuliaBundle;
import org.ice1000.julia.lang.JuliaFileType;
import org.ice1000.julia.lang.module.JuliaSdkComboBox;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JuliaRunConfigurationEditor extends SettingsEditor<JuliaRunConfiguration> {
	private @NotNull JPanel mainPanel;
	private @NotNull TextFieldWithBrowseButton targetFileField;
	private @NotNull JuliaSdkComboBox sdkComboBox;
	private @NotNull TextFieldWithBrowseButton juliaExeField;
	private @NotNull TextFieldWithBrowseButton workingDirField;
	private @NotNull JCheckBox inlineCheckBox;
	private @NotNull JCheckBox checkBoundsCheckBox;
	private @NotNull JCheckBox colorCheckBox;
	private @NotNull JCheckBox historyCheckBox;
	private @NotNull JCheckBox unsafeFloatCheckBox;
	private @NotNull JCheckBox handleSignalCheckBox;
	private @NotNull JCheckBox startupFileCheckBox;
	private @NotNull JTextField programArgumentsField; // [args...]
	private @NotNull JComboBox optimizationLevelComboBox; // --optimize
	private @NotNull JComboBox jitCompilerOptions; // --compile

	public JuliaRunConfigurationEditor(@NotNull JuliaRunConfiguration configuration) {
		juliaExeField.addBrowseFolderListener(JuliaBundle.message("julia.messages.run.select-compiler"),
			JuliaBundle.message("julia.messages.run.select-compiler.description"),
			null,
			FileChooserDescriptorFactory.createSingleFileDescriptor());
		workingDirField.addBrowseFolderListener(JuliaBundle.message("julia.messages.run.select-working-dir"),
			JuliaBundle.message("julia.messages.run.select-working-dir.description"),
			null,
			FileChooserDescriptorFactory.createSingleFolderDescriptor());
		targetFileField.addBrowseFolderListener(JuliaBundle.message("julia.messages.run.select-julia-file"),
			JuliaBundle.message("julia.messages.run.select-julia-file.description"),
			null,
			FileChooserDescriptorFactory.createSingleFileDescriptor(JuliaFileType.INSTANCE));
		resetEditorFrom(configuration);
	}

	@Override protected void resetEditorFrom(@NotNull JuliaRunConfiguration configuration) {
		juliaExeField.setText(configuration.getJuliaExecutable());
		targetFileField.setText(configuration.getTargetFile());
		workingDirField.setText(configuration.getWorkingDir());
		sdkComboBox.getComboBox().setSelectedItem(configuration.getSdkUsed());
		inlineCheckBox.setSelected(configuration.getInlineOption());
		checkBoundsCheckBox.setSelected(configuration.getCheckBoundsOption());
		colorCheckBox.setSelected(configuration.getColorOption());
		historyCheckBox.setSelected(configuration.getHistoryOption());
		handleSignalCheckBox.setSelected(configuration.getHandleSignalOption());
		unsafeFloatCheckBox.setSelected(configuration.getUnsafeFloatOption());
		startupFileCheckBox.setSelected(configuration.getStartupFileOption());
	}

	@Override protected void applyEditorTo(@NotNull JuliaRunConfiguration configuration) throws ConfigurationException {
		String juliaExecutable = juliaExeField.getText();
		if (Files.isExecutable(Paths.get(juliaExecutable))) configuration.setJuliaExecutable(juliaExecutable);
		else reportInvalidPath(juliaExecutable);
		String targetFile = targetFileField.getText();
		if (Files.isReadable(Paths.get(targetFile))) configuration.setTargetFile(targetFile);
		else reportInvalidPath(targetFile);
		String workingDirectory = workingDirField.getText();
		if (Files.isDirectory(Paths.get(workingDirectory))) configuration.setWorkingDir(workingDirectory);
		else reportInvalidPath(workingDirectory);
		configuration.setSdkUsed(sdkComboBox.getSelectedSdk());
		configuration.setInlineOption(inlineCheckBox.isSelected());
		configuration.setCheckBoundsOption(checkBoundsCheckBox.isSelected());
		configuration.setColorOption(colorCheckBox.isSelected());
		configuration.setHistoryOption(historyCheckBox.isSelected());
		configuration.setHandleSignalOption(handleSignalCheckBox.isSelected());
		configuration.setUnsafeFloatOption(unsafeFloatCheckBox.isSelected());
		configuration.setStartupFileOption(startupFileCheckBox.isSelected());
	}

	@Contract("_ -> fail") private void reportInvalidPath(@NotNull String path) throws ConfigurationException {
		throw new ConfigurationException(JuliaBundle.message("julia.run-config.invalid-path", path));
	}

	@Override protected @NotNull JPanel createEditor() {
		return mainPanel;
	}
}
