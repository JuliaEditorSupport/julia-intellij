// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.ice1000.julia.lang.psi.impl.IJuliaModuleDeclaration;
import com.intellij.psi.StubBasedPsiElement;

public interface JuliaModuleDeclaration extends IJuliaModuleDeclaration, StubBasedPsiElement<JuliaModuleDeclarationClassStub> {

  @NotNull
  List<JuliaComment> getCommentList();

  @Nullable
  JuliaStatements getStatements();

  @Nullable
  JuliaSymbol getSymbol();

}
