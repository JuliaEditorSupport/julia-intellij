package org.ice1000.julia.lang.execution;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.RawCommandLineEditor;
import org.ice1000.julia.lang.JuliaBundle;
import org.ice1000.julia.lang.JuliaFileType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class JuliaRunConfigurationEditor extends SettingsEditor<JuliaRunConfiguration> {
	private @NotNull JPanel mainPanel;
	private @NotNull TextFieldWithBrowseButton targetFileField;
	private @NotNull TextFieldWithBrowseButton juliaExeField;
	private @NotNull TextFieldWithBrowseButton workingDirField;
	private @NotNull TextFieldWithBrowseButton systemImageField;
	private @NotNull JCheckBox inlineCheckBox;
	private @NotNull JCheckBox checkBoundsCheckBox;
	private @NotNull JCheckBox colorCheckBox;
	private @NotNull JCheckBox historyCheckBox;
	private @NotNull JCheckBox unsafeFloatCheckBox;
	private @NotNull JCheckBox handleSignalCheckBox;
	private @NotNull JCheckBox startupFileCheckBox;
	private @NotNull JCheckBox launchReplCheckBox;
	private @NotNull JCheckBox systemImageCheckBox;
	private @NotNull RawCommandLineEditor programArgumentsField;
	private @NotNull RawCommandLineEditor additionalOptionsField;
	private @NotNull JComboBox<String> optimizationLevelComboBox; // --optimize
	private @NotNull JComboBox<String> jitCompilerOptions; // --compile
	private @NotNull JComboBox<String> depWarnOptions;
	private @NotNull JComboBox<String> codeCovOptions;
	private @NotNull JComboBox<String> trackAllocOptions;

	public JuliaRunConfigurationEditor(@NotNull JuliaRunConfiguration configuration, @NotNull Project project) {
		juliaExeField.addBrowseFolderListener(JuliaBundle.message("julia.messages.run.select-compiler"),
			JuliaBundle.message("julia.messages.run.select-compiler.description"),
			project,
			FileChooserDescriptorFactory.createSingleFileDescriptor());
		workingDirField.addBrowseFolderListener(JuliaBundle.message("julia.messages.run.select-working-dir"),
			JuliaBundle.message("julia.messages.run.select-working-dir.description"),
			project,
			FileChooserDescriptorFactory.createSingleFolderDescriptor());
		targetFileField.addBrowseFolderListener(JuliaBundle.message("julia.messages.run.select-julia-file"),
			JuliaBundle.message("julia.messages.run.select-julia-file.description"),
			project,
			FileChooserDescriptorFactory.createSingleFileDescriptor(JuliaFileType.INSTANCE));
		String def = "2 (" + JuliaBundle.message("julia.run-config.opt-level.default") + ")";
		String rec = "3 (" + JuliaBundle.message("julia.run-config.opt-level.recommended") + ")";
		systemImageCheckBox.addChangeListener(x -> systemImageField.setEnabled(systemImageCheckBox.isSelected()));
		Arrays.asList("0", "1", def, rec).forEach(optimizationLevelComboBox::addItem);
		Arrays.asList("yes", "no", "all", "min").forEach(jitCompilerOptions::addItem);
		Arrays.asList("yes", "no", "error").forEach(depWarnOptions::addItem);
		List<String> noneUserAll = Arrays.asList("none", "user", "all");
		noneUserAll.forEach(codeCovOptions::addItem);
		noneUserAll.forEach(trackAllocOptions::addItem);
		resetEditorFrom(configuration);
	}

	@Override protected void resetEditorFrom(@NotNull JuliaRunConfiguration configuration) {
		juliaExeField.setText(configuration.getJuliaExecutable());
		targetFileField.setText(configuration.getTargetFile());
		workingDirField.setText(configuration.getWorkingDir());
		inlineCheckBox.setSelected(configuration.getInlineOption());
		checkBoundsCheckBox.setSelected(configuration.getCheckBoundsOption());
		colorCheckBox.setSelected(configuration.getColorOption());
		historyCheckBox.setSelected(configuration.getHistoryOption());
		handleSignalCheckBox.setSelected(configuration.getHandleSignalOption());
		unsafeFloatCheckBox.setSelected(configuration.getUnsafeFloatOption());
		startupFileCheckBox.setSelected(configuration.getStartupFileOption());
		launchReplCheckBox.setSelected(configuration.getLaunchReplOption());
		systemImageCheckBox.setSelected(configuration.getSystemImageOption());
		systemImageField.setEnabled(systemImageCheckBox.isSelected());
		systemImageField.setText(configuration.getSystemImage());
		additionalOptionsField.setText(configuration.getAdditionalOptions());
		programArgumentsField.setText(configuration.getProgramArgs());
		optimizationLevelComboBox.setSelectedIndex(configuration.getOptimizationLevel());
		jitCompilerOptions.setSelectedItem(configuration.getJitCompiler());
		depWarnOptions.setSelectedItem(configuration.getDeprecationWarning());
		codeCovOptions.setSelectedItem(configuration.getCodeCoverage());
		trackAllocOptions.setSelectedItem(configuration.getTrackAllocation());
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
		configuration.setInlineOption(inlineCheckBox.isSelected());
		configuration.setCheckBoundsOption(checkBoundsCheckBox.isSelected());
		configuration.setColorOption(colorCheckBox.isSelected());
		configuration.setHistoryOption(historyCheckBox.isSelected());
		configuration.setHandleSignalOption(handleSignalCheckBox.isSelected());
		configuration.setUnsafeFloatOption(unsafeFloatCheckBox.isSelected());
		configuration.setStartupFileOption(startupFileCheckBox.isSelected());
		configuration.setLaunchReplOption(launchReplCheckBox.isSelected());
		configuration.setSystemImageOption(systemImageCheckBox.isSelected());
		configuration.setSystemImage(systemImageField.getText());
		configuration.setAdditionalOptions((additionalOptionsField.getText()));
		configuration.setProgramArgs((programArgumentsField.getText()));
		configuration.setOptimizationLevel(optimizationLevelComboBox.getSelectedIndex());
		configuration.setJitCompiler(String.valueOf(jitCompilerOptions.getSelectedItem()));
		configuration.setDeprecationWarning(String.valueOf(depWarnOptions.getSelectedItem()));
		configuration.setCodeCoverage(String.valueOf(codeCovOptions.getSelectedItem()));
		configuration.setTrackAllocation(String.valueOf(trackAllocOptions.getSelectedItem()));
	}

	@Contract("_ -> fail") private void reportInvalidPath(@NotNull String path) throws ConfigurationException {
		throw new ConfigurationException(JuliaBundle.message("julia.run-config.invalid-path", path));
	}

	@Override protected @NotNull JPanel createEditor() {
		return mainPanel;
	}

}
