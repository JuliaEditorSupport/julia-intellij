// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.ice1000.julia.lang.psi.impl.DocStringOwner;

public interface JuliaMacro extends JuliaExpr, PsiNameIdentifierOwner, DocStringOwner {

  @NotNull
  List<JuliaComment> getCommentList();

  @Nullable
  JuliaSymbol getSymbol();

  @Nullable
  JuliaUntypedVariables getUntypedVariables();

}
