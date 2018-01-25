package org.ice1000.julia.lang.module;

import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.labels.LinkLabel;
import org.ice1000.julia.lang.JuliaBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Objects;

public class JuliaSetupSdkWizardStep extends ModuleWizardStep {
	private @NotNull JuliaModuleBuilder builder;
	private @NotNull JPanel mainPanel;
	private @NotNull JuliaSdkComboBox comboBox;
	private @NotNull LinkLabel<Object> juliaWebsite;
	private @NotNull JLabel usefulText;

	public JuliaSetupSdkWizardStep(@NotNull JuliaModuleBuilder builder) {
		this.builder = builder;
		this.usefulText.setVisible(false);
		juliaWebsite.setListener((label, o) -> BrowserLauncher.getInstance().open(juliaWebsite.getText()), null);
	}

	@Override public @NotNull JPanel getComponent() {
		return mainPanel;
	}

	@Override public boolean validate() throws ConfigurationException {
		if (StringUtil.isEmpty(comboBox.getSdkName())) {
			usefulText.setVisible(true);
			throw new ConfigurationException(JuliaBundle.message("julia.modules.sdk.invalid"));
		}
		usefulText.setVisible(false);
		return super.validate();
	}

	@Override public void updateDataModel() {
		builder.setSdk(Objects.requireNonNull(comboBox.getSelectedSdk()));
	}
}
