// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public interface JuliaTypedNamedVariable extends PsiNameIdentifierOwner {

  @NotNull
  List<JuliaComment> getCommentList();

  @NotNull
  List<JuliaExpr> getExprList();

  @Nullable
  JuliaTypeAnnotation getTypeAnnotation();

}
