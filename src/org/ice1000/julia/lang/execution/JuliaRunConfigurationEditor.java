package org.ice1000.julia.lang.execution;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class JuliaRunConfigurationEditor extends SettingsEditor<JuliaRunConfiguration> {
	private @NotNull JPanel mainPanel;
	private @NotNull JuliaRunConfiguration configuration;

	public JuliaRunConfigurationEditor(@NotNull JuliaRunConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override protected void resetEditorFrom(@NotNull JuliaRunConfiguration external) {
	}

	@Override protected void applyEditorTo(@NotNull JuliaRunConfiguration external) throws ConfigurationException {
	}

	@Override protected @NotNull JPanel createEditor() {
		return mainPanel;
	}
}
