// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface JuliaWhereClause extends PsiElement {

  @NotNull
  List<JuliaComment> getCommentList();

  @NotNull
  List<JuliaSymbol> getSymbolList();

  @Nullable
  JuliaTypeParameters getTypeParameters();

}
