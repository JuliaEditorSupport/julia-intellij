package org.ice1000.julia.lang.module;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.ice1000.julia.lang.JuliaBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

import static org.ice1000.julia.lang.module.Julia_sdksKt.importPathOf;
import static org.ice1000.julia.lang.module.Julia_sdksKt.toJuliaSdkData;

public class JuliaSdkDataConfigurable implements AdditionalDataConfigurable {
	private @NotNull JPanel mainPanel;
	private @NotNull JFormattedTextField textLimitField;
	private @NotNull JFormattedTextField timeLimitField;
	private @NotNull TextFieldWithBrowseButton importPathField;
	private @Nullable Sdk sdk;

	public JuliaSdkDataConfigurable() {
		NumberFormat format = NumberFormat.getIntegerInstance();
		format.setGroupingUsed(false);
		DefaultFormatterFactory factory = new DefaultFormatterFactory(new NumberFormatter(format));
		timeLimitField.setFormatterFactory(factory);
		textLimitField.setFormatterFactory(factory);
	}

	@Override public void setSdk(@NotNull Sdk sdk) {
		this.sdk = sdk;
		JuliaSdkData data = toJuliaSdkData(sdk.getSdkAdditionalData());
		if (data == null) return;
		String home = sdk.getHomePath();
		if (home == null) return;
		importPathField.setText(importPathOf(home, 500L));
		timeLimitField.setValue(data.getTryEvaluateTimeLimit());
		textLimitField.setValue(data.getTryEvaluateTextLimit());
	}

	@Override public boolean isModified() {
		if (sdk == null) return false;
		JuliaSdkData data = toJuliaSdkData(sdk.getSdkAdditionalData());
		return data == null ||
			!textLimitField.getValue().equals(Integer.valueOf(data.getTryEvaluateTextLimit()).longValue()) ||
			!timeLimitField.getValue().equals(data.getTryEvaluateTimeLimit()) ||
			!importPathField.getText().equals(data.getImportPath());
	}

	@Override public @NotNull String getTabName() {
		return JuliaBundle.message("julia.modules.sdk.additional.title");
	}

	@Override public void apply() throws ConfigurationException {
		if (sdk == null) throw new ConfigurationException(JuliaBundle.message("julia.modules.sdk.null-sdk"));
		SdkModificator modificator = sdk.getSdkModificator();
		Object timeLimitFieldValue = timeLimitField.getValue();
		Object textLimitFieldValue = textLimitField.getValue();
		if (!(timeLimitFieldValue instanceof Number && textLimitFieldValue instanceof Number)) return;
		modificator.setSdkAdditionalData(new JuliaSdkData(((Number) timeLimitFieldValue).longValue(),
			((Number) textLimitFieldValue).intValue(),
			importPathField.getText()));
		modificator.commitChanges();
	}

	@Override public @NotNull JComponent createComponent() {
		return mainPanel;
	}
}
