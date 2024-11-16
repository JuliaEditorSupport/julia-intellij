// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.ice1000.julia.lang.psi.JuliaTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.ice1000.julia.lang.psi.*;

public class JuliaWhereClauseImpl extends ASTWrapperPsiElement implements JuliaWhereClause {

  public JuliaWhereClauseImpl(@NotNull ASTNode node) {
    super(node);
  }

  public <R> R accept(@NotNull JuliaVisitor<R> visitor) {
    return visitor.visitWhereClause(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JuliaVisitor) accept((JuliaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<JuliaComment> getCommentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JuliaComment.class);
  }

  @Override
  @NotNull
  public List<JuliaSymbol> getSymbolList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JuliaSymbol.class);
  }

  @Override
  @Nullable
  public JuliaTypeParameters getTypeParameters() {
    return PsiTreeUtil.getChildOfType(this, JuliaTypeParameters.class);
  }

}
