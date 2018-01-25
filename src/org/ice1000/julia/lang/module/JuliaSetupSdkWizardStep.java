package org.ice1000.julia.lang.module;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Objects;

public class JuliaSetupSdkWizardStep extends ModuleWizardStep{
    private @NotNull JuliaModuleBuilder builder;
    private @NotNull JPanel mainPanel;
    private JuliaSdkCombobox comboBox;

    public JuliaSetupSdkWizardStep(@NotNull JuliaModuleBuilder builder) {
        this.builder = builder;
    }

    @Override
    public JComponent getComponent() {
        return mainPanel;
    }

    @Override
    public void updateDataModel() {
        builder.setSdk(Objects.requireNonNull(comboBox.getSelectedSdk()));
    }
}
