// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JuliaIfExpr extends JuliaExpr {

  @NotNull
  List<JuliaComment> getCommentList();

  @Nullable
  JuliaElseClause getElseClause();

  @NotNull
  List<JuliaElseIfClause> getElseIfClauseList();

  @Nullable
  JuliaExpr getExpr();

  @Nullable
  JuliaStatements getStatements();

}
