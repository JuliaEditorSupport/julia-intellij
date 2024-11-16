// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.ice1000.julia.lang.psi.impl.IJuliaFunctionDeclaration;

public interface JuliaCompactFunction extends JuliaExpr, IJuliaFunctionDeclaration {

  @NotNull
  List<JuliaComment> getCommentList();

  @NotNull
  List<JuliaExpr> getExprList();

  @NotNull
  JuliaFunctionSignature getFunctionSignature();

  @Nullable
  JuliaTypeParameters getTypeParameters();

  @NotNull
  List<JuliaWhereClause> getWhereClauseList();

}
