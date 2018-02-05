package org.ice1000.julia.lang.module;

import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.ice1000.julia.lang.JuliaBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ResourceBundle;

import static org.ice1000.julia.lang.module.Julia_project_serviceKt.getJuliaSettings;
import static org.ice1000.julia.lang.module.UtilsKt.*;

public class JuliaProjectConfigurable implements Configurable {
	private @NotNull JPanel mainPanel;
	private @NotNull JFormattedTextField textLimitField;
	private @NotNull JFormattedTextField timeLimitField;
	private @NotNull TextFieldWithBrowseButton importPathField;
	private @NotNull TextFieldWithBrowseButton juliaExeField;
	private @NotNull LinkLabel<Object> juliaWebsite;
	private @NotNull JLabel version;
	private @NotNull TextFieldWithBrowseButton basePathField;
	private @NotNull JuliaSettings settings;

	public JuliaProjectConfigurable(@NotNull Project project) {
		settings = getJuliaSettings(project).getSettings();
		version.setText(settings.getVersion());
		NumberFormat format = NumberFormat.getIntegerInstance();
		format.setGroupingUsed(false);
		DefaultFormatterFactory factory = new DefaultFormatterFactory(new NumberFormatter(format));
		timeLimitField.setFormatterFactory(factory);
		timeLimitField.setValue(settings.getTryEvaluateTimeLimit());
		textLimitField.setFormatterFactory(factory);
		textLimitField.setValue((long) settings.getTryEvaluateTextLimit());
		juliaWebsite.setListener((label, o) -> BrowserLauncher.getInstance().open(juliaWebsite.getText()), null);
		importPathField.setText(settings.getImportPath());
		importPathField.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor(),
			project));
		basePathField.setText(settings.getBasePath());
		basePathField.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor(),
			project));
		juliaExeField.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor(),
			project));
		juliaExeField.setText(settings.getExePath());
		juliaExeField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
			@Override protected void textChanged(DocumentEvent e) {
				String exePath = juliaExeField.getText();
				importPathField.setText(importPathOf(exePath, 800L));
				version.setText(versionOf(exePath, 800L));
				@Nullable String base = tryGetBase(exePath);
				if (base != null) basePathField.setText(base);
			}
		});
	}

	@Override public @Nls String getDisplayName() {
		return JuliaBundle.message("julia.name");
	}

	@Override public @NotNull JPanel createComponent() {
		return mainPanel;
	}

	@Override public boolean isModified() {
		return !settings.getImportPath().equals(importPathField.getText()) ||
			!settings.getBasePath().equals(basePathField.getText()) ||
			!settings.getExePath().equals(juliaExeField.getText()) ||
			settings.getTryEvaluateTextLimit() != (Long) textLimitField.getValue() ||
			settings.getTryEvaluateTimeLimit() != (Long) timeLimitField.getValue();
	}

	@Override public void apply() throws ConfigurationException {
		Object timeLimitFieldValue = timeLimitField.getValue();
		Object textLimitFieldValue = textLimitField.getValue();
		if (!(timeLimitField.getValue() instanceof Number && textLimitField.getValue() instanceof Number))
			throw new ConfigurationException(JuliaBundle.message("julia.modules.try-eval.invalid"));
		settings.setTryEvaluateTextLimit(((Number) textLimitFieldValue).intValue());
		settings.setTryEvaluateTimeLimit(((Number) timeLimitFieldValue).longValue());
		if (!validateJuliaExe(juliaExeField.getText()))
			throw new ConfigurationException(JuliaBundle.message("julia.modules.invalid"));
		settings.setExePath(juliaExeField.getText());
		settings.setVersion(version.getText());
		settings.setBasePath(basePathField.getText());
		settings.setImportPath(importPathField.getText());
	}

}
