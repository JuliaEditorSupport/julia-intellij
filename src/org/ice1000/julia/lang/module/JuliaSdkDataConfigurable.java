package org.ice1000.julia.lang.module;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable;
import com.intellij.openapi.projectRoots.Sdk;
import org.ice1000.julia.lang.JuliaBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

import static org.ice1000.julia.lang.module.Julia_sdksKt.toJuliaSdkData;

public class JuliaSdkDataConfigurable implements AdditionalDataConfigurable {
	private @NotNull JPanel mainPanel;
	private @NotNull JFormattedTextField textLimitField;
	private @NotNull JFormattedTextField timeLimitField;
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
		timeLimitField.setValue(data.getTryEvaluateTimeLimit());
		textLimitField.setValue(data.getTryEvaluateTextLimit());
	}

	@Override public boolean isModified() {
		if (sdk == null) return false;
		JuliaSdkData data = toJuliaSdkData(sdk.getSdkAdditionalData());
		return data == null ||
				!textLimitField.getValue().equals(Integer.valueOf(data.getTryEvaluateTextLimit()).longValue()) ||
				!timeLimitField.getValue().equals(data.getTryEvaluateTimeLimit());
	}

	@Override public @NotNull String getTabName() {
		return JuliaBundle.message("julia.modules.sdk.try-eval.title");
	}

	@Override public void apply() throws ConfigurationException {
	}

	@Override public @NotNull JComponent createComponent() {
		return mainPanel;
	}
}
