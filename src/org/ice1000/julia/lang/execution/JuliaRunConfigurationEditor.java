package org.ice1000.julia.lang.execution;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.ice1000.julia.lang.JuliaBundle;
import org.ice1000.julia.lang.JuliaFileType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class JuliaRunConfigurationEditor extends SettingsEditor<JuliaRunConfiguration> {
	private @NotNull JPanel mainPanel;
	private @NotNull TextFieldWithBrowseButton targetFileField;
	private @NotNull TextFieldWithBrowseButton juliaExeField;
	private @NotNull TextFieldWithBrowseButton workingDirField;
	private @NotNull JCheckBox inlineCheckBox;
	private @NotNull JCheckBox checkBoundsCheckBox;
	private @NotNull JCheckBox colorCheckBox;
	private @NotNull JCheckBox historyCheckBox;
	private @NotNull JCheckBox unsafeFloatCheckBox;
	private @NotNull JCheckBox handleSignalCheckBox;
	private @NotNull JCheckBox startupFileCheckBox;
	private @NotNull JCheckBox launchReplCheckBox;
	private @NotNull JCheckBox quietReplCheckBox;
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
		launchReplCheckBox.addChangeListener(changeEvent -> quietReplCheckBox.setEnabled(launchReplCheckBox.isSelected()));
		String def = "2 (" + JuliaBundle.message("julia.run-config.opt-level.default") + ")";
		String rec = "3 (" + JuliaBundle.message("julia.run-config.opt-level.recommended") + ")";
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
		quietReplCheckBox.setSelected(configuration.getQuietReplOption());
		quietReplCheckBox.setEnabled(launchReplCheckBox.isSelected());
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
		configuration.setQuietReplOption(quietReplCheckBox.isSelected());
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

	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
		mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
		final JLabel label1 = new JLabel();
		this.$$$loadLabelText$$$(label1,
			ResourceBundle.getBundle("org/ice1000/julia/lang/julia-bundle").getString("julia.run-config.script-path"));
		mainPanel.add(label1,
			new GridConstraints(0,
				0,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false));
		targetFileField = new TextFieldWithBrowseButton();
		mainPanel.add(targetFileField,
			new GridConstraints(0,
				1,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				null,
				null,
				null,
				0,
				false));
		final JLabel label2 = new JLabel();
		this.$$$loadLabelText$$$(label2,
			ResourceBundle.getBundle("org/ice1000/julia/lang/julia-bundle").getString("julia.run-config.working-dir"));
		mainPanel.add(label2,
			new GridConstraints(1,
				0,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false));
		workingDirField = new TextFieldWithBrowseButton();
		mainPanel.add(workingDirField,
			new GridConstraints(1,
				1,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				null,
				null,
				null,
				0,
				false));
		final JBScrollPane jBScrollPane1 = new JBScrollPane();
		mainPanel.add(jBScrollPane1,
			new GridConstraints(3,
				0,
				1,
				2,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
				null,
				null,
				null,
				0,
				false));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(13, 2, new Insets(0, 0, 0, 0), -1, -1));
		jBScrollPane1.setViewportView(panel1);
		panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),
			null,
			TitledBorder.DEFAULT_JUSTIFICATION,
			TitledBorder.DEFAULT_POSITION,
			this.$$$getFont$$$(null, -1, -1, panel1.getFont())));
		final Spacer spacer1 = new Spacer();
		panel1.add(spacer1,
			new GridConstraints(12,
				0,
				1,
				2,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_VERTICAL,
				1,
				GridConstraints.SIZEPOLICY_WANT_GROW,
				null,
				null,
				null,
				0,
				false));
		inlineCheckBox = new JCheckBox();
		this.$$$loadButtonText$$$(inlineCheckBox,
			ResourceBundle.getBundle("org/ice1000/julia/lang/julia-bundle").getString("julia.run-config.inline"));
		panel1.add(inlineCheckBox,
			new GridConstraints(0,
				0,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				new Dimension(191, 24),
				null,
				0,
				false));
		historyCheckBox = new JCheckBox();
		this.$$$loadButtonText$$$(historyCheckBox,
			ResourceBundle.getBundle("org/ice1000/julia/lang/julia-bundle").getString("julia.run-config.history"));
		panel1.add(historyCheckBox,
			new GridConstraints(1,
				1,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false));
		colorCheckBox = new JCheckBox();
		this.$$$loadButtonText$$$(colorCheckBox,
			ResourceBundle.getBundle("org/ice1000/julia/lang/julia-bundle").getString("julia.run-config.color"));
		panel1.add(colorCheckBox,
			new GridConstraints(1,
				0,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				new Dimension(191, 24),
				null,
				0,
				false));
		handleSignalCheckBox = new JCheckBox();
		this.$$$loadButtonText$$$(handleSignalCheckBox,
			ResourceBundle.getBundle("org/ice1000/julia/lang/julia-bundle").getString("julia.run-config.handle-signal"));
		panel1.add(handleSignalCheckBox,
			new GridConstraints(2,
				1,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false));
		startupFileCheckBox = new JCheckBox();
		this.$$$loadButtonText$$$(startupFileCheckBox,
			ResourceBundle.getBundle("org/ice1000/julia/lang/julia-bundle").getString("julia.run-config.load-startup"));
		panel1.add(startupFileCheckBox,
			new GridConstraints(3,
				0,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				new Dimension(191, 24),
				null,
				0,
				false));
		final JLabel label3 = new JLabel();
		this.$$$loadLabelText$$$(label3,
			ResourceBundle.getBundle("org/ice1000/julia/lang/julia-bundle").getString("julia.run-config.opt-level"));
		panel1.add(label3,
			new GridConstraints(6,
				0,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				new Dimension(191, 20),
				null,
				0,
				false));
		optimizationLevelComboBox = new JComboBox();
		panel1.add(optimizationLevelComboBox,
			new GridConstraints(6,
				1,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false));
		final JLabel label4 = new JLabel();
		this.$$$loadLabelText$$$(label4,
			ResourceBundle.getBundle("org/ice1000/julia/lang/julia-bundle").getString("julia.run-config.jit-options"));
		panel1.add(label4,
			new GridConstraints(7,
				0,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				new Dimension(191, 20),
				null,
				0,
				false));
		jitCompilerOptions = new JComboBox();
		panel1.add(jitCompilerOptions,
			new GridConstraints(7,
				1,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false));
		final JLabel label5 = new JLabel();
		this.$$$loadLabelText$$$(label5,
			ResourceBundle.getBundle("org/ice1000/julia/lang/julia-bundle").getString("julia.run-config.more-options"));
		panel1.add(label5,
			new GridConstraints(8,
				0,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				new Dimension(191, 20),
				null,
				0,
				false));
		checkBoundsCheckBox = new JCheckBox();
		this.$$$loadButtonText$$$(checkBoundsCheckBox,
			ResourceBundle.getBundle("org/ice1000/julia/lang/julia-bundle").getString("julia.run-config.check-bounds"));
		panel1.add(checkBoundsCheckBox,
			new GridConstraints(2,
				0,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				new Dimension(191, 24),
				null,
				0,
				false));
		unsafeFloatCheckBox = new JCheckBox();
		this.$$$loadButtonText$$$(unsafeFloatCheckBox,
			ResourceBundle.getBundle("org/ice1000/julia/lang/julia-bundle").getString("julia.run-config.unsafe-float"));
		panel1.add(unsafeFloatCheckBox,
			new GridConstraints(0,
				1,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false));
		final JLabel label6 = new JLabel();
		this.$$$loadLabelText$$$(label6,
			ResourceBundle.getBundle("org/ice1000/julia/lang/julia-bundle").getString("julia.run-config.dep-warn"));
		panel1.add(label6,
			new GridConstraints(9,
				0,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				new Dimension(191, 20),
				null,
				0,
				false));
		depWarnOptions = new JComboBox();
		panel1.add(depWarnOptions,
			new GridConstraints(9,
				1,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false));
		final JLabel label7 = new JLabel();
		this.$$$loadLabelText$$$(label7,
			ResourceBundle.getBundle("org/ice1000/julia/lang/julia-bundle").getString("julia.run-config.code-cov"));
		panel1.add(label7,
			new GridConstraints(10,
				0,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				new Dimension(191, 20),
				null,
				0,
				false));
		codeCovOptions = new JComboBox();
		panel1.add(codeCovOptions,
			new GridConstraints(10,
				1,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false));
		final JLabel label8 = new JLabel();
		this.$$$loadLabelText$$$(label8,
			ResourceBundle.getBundle("org/ice1000/julia/lang/julia-bundle").getString("julia.run-config.track-alloc"));
		panel1.add(label8,
			new GridConstraints(11,
				0,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				new Dimension(191, 20),
				null,
				0,
				false));
		trackAllocOptions = new JComboBox();
		panel1.add(trackAllocOptions,
			new GridConstraints(11,
				1,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false));
		final JLabel label9 = new JLabel();
		this.$$$loadLabelText$$$(label9,
			ResourceBundle.getBundle("org/ice1000/julia/lang/julia-bundle").getString("julia.run-config.exe"));
		panel1.add(label9,
			new GridConstraints(5,
				0,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				new Dimension(191, 20),
				null,
				0,
				false));
		juliaExeField = new TextFieldWithBrowseButton();
		panel1.add(juliaExeField,
			new GridConstraints(5,
				1,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				null,
				null,
				null,
				0,
				false));
		additionalOptionsField = new RawCommandLineEditor();
		panel1.add(additionalOptionsField,
			new GridConstraints(8,
				1,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				null,
				null,
				null,
				0,
				false));
		launchReplCheckBox = new JCheckBox();
		this.$$$loadButtonText$$$(launchReplCheckBox,
			ResourceBundle.getBundle("org/ice1000/julia/lang/julia-bundle").getString("julia.run-config.launch-repl"));
		panel1.add(launchReplCheckBox,
			new GridConstraints(3,
				1,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false));
		quietReplCheckBox = new JCheckBox();
		this.$$$loadButtonText$$$(quietReplCheckBox,
			ResourceBundle.getBundle("org/ice1000/julia/lang/julia-bundle").getString("julia.run-config.quiet-repl"));
		panel1.add(quietReplCheckBox,
			new GridConstraints(4,
				1,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false));
		final JLabel label10 = new JLabel();
		this.$$$loadLabelText$$$(label10,
			ResourceBundle.getBundle("org/ice1000/julia/lang/julia-bundle").getString("julia.run-config.prog-args"));
		mainPanel.add(label10,
			new GridConstraints(2,
				0,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false));
		programArgumentsField = new RawCommandLineEditor();
		mainPanel.add(programArgumentsField,
			new GridConstraints(2,
				1,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				null,
				null,
				null,
				0,
				false));
	}

	/**
	 * @noinspection ALL
	 */
	private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
		if (currentFont == null) return null;
		String resultName;
		if (fontName == null) {
			resultName = currentFont.getName();
		} else {
			Font testFont = new Font(fontName, Font.PLAIN, 10);
			if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
				resultName = fontName;
			} else {
				resultName = currentFont.getName();
			}
		}
		return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
	}

	/**
	 * @noinspection ALL
	 */
	private void $$$loadLabelText$$$(JLabel component, String text) {
		StringBuffer result = new StringBuffer();
		boolean haveMnemonic = false;
		char mnemonic = '\0';
		int mnemonicIndex = -1;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '&') {
				i++;
				if (i == text.length()) break;
				if (!haveMnemonic && text.charAt(i) != '&') {
					haveMnemonic = true;
					mnemonic = text.charAt(i);
					mnemonicIndex = result.length();
				}
			}
			result.append(text.charAt(i));
		}
		component.setText(result.toString());
		if (haveMnemonic) {
			component.setDisplayedMnemonic(mnemonic);
			component.setDisplayedMnemonicIndex(mnemonicIndex);
		}
	}

	/**
	 * @noinspection ALL
	 */
	private void $$$loadButtonText$$$(AbstractButton component, String text) {
		StringBuffer result = new StringBuffer();
		boolean haveMnemonic = false;
		char mnemonic = '\0';
		int mnemonicIndex = -1;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '&') {
				i++;
				if (i == text.length()) break;
				if (!haveMnemonic && text.charAt(i) != '&') {
					haveMnemonic = true;
					mnemonic = text.charAt(i);
					mnemonicIndex = result.length();
				}
			}
			result.append(text.charAt(i));
		}
		component.setText(result.toString());
		if (haveMnemonic) {
			component.setMnemonic(mnemonic);
			component.setDisplayedMnemonicIndex(mnemonicIndex);
		}
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return mainPanel;
	}
}
