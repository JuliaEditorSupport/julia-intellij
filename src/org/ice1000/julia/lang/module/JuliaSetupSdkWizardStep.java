package org.ice1000.julia.lang.module;

import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.ice1000.julia.lang.JuliaBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

import java.awt.*;
import java.util.ResourceBundle;

import static org.ice1000.julia.lang.module.UtilsKt.*;

public class JuliaSetupSdkWizardStep extends ModuleWizardStep {
	private @NotNull JuliaModuleBuilder builder;
	private @NotNull JPanel mainPanel;
	private @NotNull LinkLabel<Object> juliaWebsite;
	private @NotNull JLabel usefulText;
	private @NotNull TextFieldWithBrowseButton juliaExeField;
	private @NotNull TextFieldWithBrowseButton importPathField;

	public JuliaSetupSdkWizardStep(@NotNull JuliaModuleBuilder builder) {
		this.builder = builder;
		this.usefulText.setVisible(false);
		juliaWebsite.setListener((label, o) -> BrowserLauncher.getInstance().open(juliaWebsite.getText()), null);
		juliaExeField.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor()));
		juliaExeField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
			@Override protected void textChanged(DocumentEvent e) {
				importPathField.setText(importPathOf(juliaExeField.getText(), 500L));
			}
		});
		juliaExeField.setText(getDefaultExePath());
		importPathField.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor()));
		importPathField.setText(importPathOf(getDefaultExePath(), 800L));
	}

	@Override public @NotNull JPanel getComponent() {
		return mainPanel;
	}

	@Override public boolean validate() throws ConfigurationException {
		if (!validateJuliaExe(juliaExeField.getText())) {
			usefulText.setVisible(true);
			throw new ConfigurationException(JuliaBundle.message("julia.modules.invalid"));
		}
		usefulText.setVisible(false);
		return super.validate();
	}

	@Override public void updateDataModel() {
		JuliaSettings settings = new JuliaSettings();
		settings.setExePath(juliaExeField.getText());
		settings.initWithExe();
		builder.settings = settings;
	}

}
