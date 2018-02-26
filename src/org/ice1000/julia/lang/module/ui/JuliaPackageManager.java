package org.ice1000.julia.lang.module.ui;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class JuliaPackageManager implements Configurable {
	protected @NotNull JPanel mainPanel;
	private JComboBox comboBox1;
}
