package org.ice1000.julia.lang.module;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.platform.ProjectGeneratorPeer;
import com.intellij.ui.components.labels.LinkLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class JuliaProjectGeneratorPeer implements Disposable, ProjectGeneratorPeer<JuliaSettings> {
	protected @NotNull LinkLabel<Object> juliaWebsite;
	protected @NotNull JLabel usefulText;
	protected @NotNull TextFieldWithBrowseButton juliaExeField;
	protected @NotNull JPanel mainPanel;
	protected @NotNull JRadioButton useLocalJuliaDistributionRadioButton;
	protected @NotNull JRadioButton selectJuliaExecutableRadioButton;
}
