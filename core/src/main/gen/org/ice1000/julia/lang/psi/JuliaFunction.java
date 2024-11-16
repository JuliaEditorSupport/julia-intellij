// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.ice1000.julia.lang.psi.impl.IJuliaFunctionDeclaration;

public interface JuliaFunction extends JuliaExpr, IJuliaFunctionDeclaration {

  @NotNull
  List<JuliaComment> getCommentList();

  @Nullable
  JuliaFunctionSignature getFunctionSignature();

  @Nullable
  JuliaSymbol getSymbol();

  @Nullable
  JuliaTypeParameters getTypeParameters();

  @Nullable
  JuliaTypedNamedVariable getTypedNamedVariable();

  @NotNull
  List<JuliaWhereClause> getWhereClauseList();

}
