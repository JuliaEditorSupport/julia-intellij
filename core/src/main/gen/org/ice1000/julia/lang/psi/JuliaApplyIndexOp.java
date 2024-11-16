// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JuliaApplyIndexOp extends JuliaExpr {

  @NotNull
  List<JuliaComment> getCommentList();

  @Nullable
  JuliaComprehensionElement getComprehensionElement();

  @NotNull
  JuliaExpr getExpr();

  @NotNull
  List<JuliaExprOrEnd> getExprOrEndList();

  @NotNull
  List<JuliaTypeAnnotation> getTypeAnnotationList();

}
