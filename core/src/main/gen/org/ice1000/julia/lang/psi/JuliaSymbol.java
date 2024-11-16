// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.ice1000.julia.lang.psi.impl.IJuliaSymbol;

public interface JuliaSymbol extends JuliaExpr, IJuliaSymbol {

  @NotNull
  List<JuliaComment> getCommentList();

  @Nullable
  JuliaExpr getExpr();

  @Nullable
  JuliaTypeAnnotation getTypeAnnotation();

}
