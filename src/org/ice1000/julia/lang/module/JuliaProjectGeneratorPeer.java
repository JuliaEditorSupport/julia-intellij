package org.ice1000.julia.lang.module;

import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.ice1000.julia.lang.JuliaBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

import static org.ice1000.julia.lang.module.UtilsKt.getDefaultExePath;
import static org.ice1000.julia.lang.module.UtilsKt.validateJulia;

public class JuliaProjectGeneratorPeer extends JuliaProjectGeneratorPeerBase {
	private @NotNull LinkLabel<Object> juliaWebsite;
	private @NotNull JLabel usefulText;
	private @NotNull TextFieldWithBrowseButton juliaExeField;
	private @NotNull JPanel mainPanel;

	public JuliaProjectGeneratorPeer(@NotNull JuliaSettings settings) {
		super(settings);
		juliaWebsite.setListener((label, o) -> BrowserLauncher.getInstance().open(juliaWebsite.getText()), null);
		juliaExeField.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor()));
		juliaExeField.setText(getDefaultExePath());
	}

	@Nullable @Override public ValidationInfo validate() {
		JuliaSettings settings = getSettings();
		settings.setExePath(juliaExeField.getText());
		settings.initWithExe();
		return validateJulia(settings) ? null : new ValidationInfo(JuliaBundle.message("julia.projects.sdk.invalid"));
	}

	@Override public @NotNull JPanel getComponent() {
		return mainPanel;
	}

}
