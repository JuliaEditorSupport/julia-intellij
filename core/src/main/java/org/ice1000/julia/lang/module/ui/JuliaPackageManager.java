package org.ice1000.julia.lang.module.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.ui.ComboboxWithBrowseButton;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class JuliaPackageManager implements Configurable {
	protected @NotNull JPanel mainPanel;
	protected @NotNull JBTable packagesList;
	protected @NotNull ComboboxWithBrowseButton alternativeExecutables;
	protected @NotNull JPanel actionsPanel;
}
