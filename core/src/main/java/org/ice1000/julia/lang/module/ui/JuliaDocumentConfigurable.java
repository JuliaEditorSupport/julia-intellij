package org.ice1000.julia.lang.module.ui;

import com.intellij.openapi.options.Configurable;

import javax.swing.*;

public abstract class JuliaDocumentConfigurable implements Configurable {
	protected JPanel mainPanel;
	protected JPanel darculaEditorPanel;
	protected JPanel intellijEditorPanel;
}
