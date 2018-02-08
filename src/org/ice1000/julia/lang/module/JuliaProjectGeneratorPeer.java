package org.ice1000.julia.lang.module;

import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.platform.ProjectGeneratorPeer;
import com.intellij.ui.components.labels.LinkLabel;
import org.ice1000.julia.lang.JuliaBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static org.ice1000.julia.lang.Julia_constantsKt.JULIA_SDK_HOME_PATH_ID;
import static org.ice1000.julia.lang.module.UtilsKt.getDefaultExePath;
import static org.ice1000.julia.lang.module.UtilsKt.validateJulia;

public abstract class JuliaProjectGeneratorPeer implements Disposable, ProjectGeneratorPeer<JuliaSettings> {
	protected @NotNull LinkLabel<Object> juliaWebsite;
	protected @NotNull JLabel usefulText;
	protected @NotNull TextFieldWithBrowseButton juliaExeField;
	protected @NotNull JPanel mainPanel;
	protected JRadioButton useLocalJuliaDistributionRadioButton;
	protected JRadioButton selectJuliaExecutableRadioButton;

	public JuliaProjectGeneratorPeer() {
		usefulText.setVisible(false);
		juliaWebsite.setListener((label, o) -> BrowserLauncher.getInstance().open(juliaWebsite.getText()), null);
		juliaExeField.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor()));
		juliaExeField.setText(getSettings().getExePath());
	}


	@Nullable @Override public ValidationInfo validate() {
		JuliaSettings settings = getSettings();
		settings.setExePath(juliaExeField.getText());
		settings.initWithExe();
		boolean validate = validateJulia(settings);
		if (validate) PropertiesComponent.getInstance().setValue(JULIA_SDK_HOME_PATH_ID, juliaExeField.getText());
		else usefulText.setVisible(true);
		return validate ? null : new ValidationInfo(JuliaBundle.message("julia.modules.invalid"));
	}

	@Override public @NotNull JPanel getComponent() {
		return mainPanel;
	}

}
