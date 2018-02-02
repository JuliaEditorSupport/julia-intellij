package org.ice1000.julia.lang.module;

import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.labels.LinkLabel;
import org.ice1000.julia.lang.JuliaBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

import static org.ice1000.julia.lang.module.Julia_sdksKt.importPathOf;
import static org.ice1000.julia.lang.module.Julia_sdksKt.validateJuliaSDK;

public class JuliaSetupSdkWizardStep extends ModuleWizardStep {
	private @NotNull JuliaModuleBuilder builder;
	private @NotNull JPanel mainPanel;
	private @NotNull LinkLabel<Object> juliaWebsite;
	private @NotNull JLabel usefulText;
	private @NotNull TextFieldWithBrowseButton juliaExeField;
	private @NotNull TextFieldWithBrowseButton importPathField;
	private @NotNull JFormattedTextField textLimitField;
	private @NotNull JFormattedTextField timeLimitField;

	public JuliaSetupSdkWizardStep(@NotNull JuliaModuleBuilder builder) {
		this.builder = builder;
		this.usefulText.setVisible(false);
		juliaWebsite.setListener((label, o) -> BrowserLauncher.getInstance().open(juliaWebsite.getText()), null);
		NumberFormat format = NumberFormat.getIntegerInstance();
		format.setGroupingUsed(false);
		DefaultFormatterFactory factory = new DefaultFormatterFactory(new NumberFormatter(format));
		timeLimitField.setFormatterFactory(factory);
		textLimitField.setFormatterFactory(factory);
		juliaExeField.addActionListener(actionEvent -> importPathField.setText(importPathOf(juliaExeField.getText(), 500L)));
	}

	@Override public @NotNull JPanel getComponent() {
		return mainPanel;
	}

	@Override public boolean validate() throws ConfigurationException {
		if (!validateJuliaSDK(juliaExeField.getText())) {
			usefulText.setVisible(true);
			throw new ConfigurationException(JuliaBundle.message("julia.modules.sdk.invalid"));
		}
		if (!(timeLimitField.getValue() instanceof Number) || !(textLimitField.getValue() instanceof Number))
			throw new ConfigurationException(JuliaBundle.message("julia.module.try-eval.invalid"));
		usefulText.setVisible(false);
		return super.validate();
	}

	@Override public void updateDataModel() {
		Object timeLimitFieldValue = timeLimitField.getValue();
		Object textLimitFieldValue = textLimitField.getValue();
		if (!(timeLimitFieldValue instanceof Number && textLimitFieldValue instanceof Number)) return;
		builder.sdkData = new JuliaSdkData();
	}
}
