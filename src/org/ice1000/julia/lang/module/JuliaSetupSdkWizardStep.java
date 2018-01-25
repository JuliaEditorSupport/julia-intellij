package org.ice1000.julia.lang.module;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.text.StringUtil;
import org.ice1000.julia.lang.JuliaBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Objects;

public class JuliaSetupSdkWizardStep extends ModuleWizardStep {
	private @NotNull JuliaModuleBuilder builder;
	private @NotNull JPanel mainPanel;
	private @NotNull JuliaSdkComboBox comboBox;

	public JuliaSetupSdkWizardStep(@NotNull JuliaModuleBuilder builder) {
		this.builder = builder;
	}

	@Override public @NotNull JComponent getComponent() {
		return mainPanel;
	}

	@Override public boolean validate() throws ConfigurationException {
		if (StringUtil.isEmpty(comboBox.getSdkName())) {
			// juliaWebsiteDescription.setVisible(true);
			throw new ConfigurationException(JuliaBundle.message("julia.modules.sdk.invalid"));
		}
		// FIXME assigned to Hex :D
		// juliaWebsiteDescription.setVisible(false);
		return super.validate();
	}

	@Override public void updateDataModel() {
		builder.setSdk(Objects.requireNonNull(comboBox.getSelectedSdk()));
	}
}
