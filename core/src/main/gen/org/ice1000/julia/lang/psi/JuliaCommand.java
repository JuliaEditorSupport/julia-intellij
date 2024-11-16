// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JuliaCommand extends JuliaExpr {

  @NotNull
  List<JuliaStringContent> getStringContentList();

  @NotNull
  List<JuliaTemplate> getTemplateList();

}
