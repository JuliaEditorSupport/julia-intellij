package org.ice1000.julia.lang.module.ui;

import com.intellij.platform.ProjectGeneratorPeer;
import com.intellij.ui.ComboboxWithBrowseButton;
import com.intellij.ui.components.labels.LinkLabel;
import org.ice1000.julia.lang.module.JuliaSettings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class JuliaProjectGeneratorPeer implements ProjectGeneratorPeer<JuliaSettings> {
	protected @NotNull LinkLabel<Object> juliaWebsite;
	protected @NotNull JLabel usefulText;
	protected @NotNull ComboboxWithBrowseButton juliaExeField;
	protected @NotNull JPanel mainPanel;
	protected @NotNull JRadioButton setupLaterRadioButton;
	protected @NotNull JRadioButton selectJuliaExecutableRadioButton;
}
