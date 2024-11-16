// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JuliaFunctionSignature extends PsiElement {

  @NotNull
  List<JuliaComment> getCommentList();

  @Nullable
  JuliaTypeAnnotation getTypeAnnotation();

  @NotNull
  List<JuliaTypedNamedVariable> getTypedNamedVariableList();

}
