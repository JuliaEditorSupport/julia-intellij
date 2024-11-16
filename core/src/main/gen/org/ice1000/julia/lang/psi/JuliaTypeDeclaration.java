// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.ice1000.julia.lang.psi.impl.IJuliaTypeDeclaration;
import com.intellij.psi.StubBasedPsiElement;

public interface JuliaTypeDeclaration extends IJuliaTypeDeclaration, StubBasedPsiElement<JuliaTypeDeclarationClassStub> {

  @NotNull
  List<JuliaComment> getCommentList();

  @NotNull
  List<JuliaExpr> getExprList();

  @Nullable
  JuliaTypeParameters getTypeParameters();

}
