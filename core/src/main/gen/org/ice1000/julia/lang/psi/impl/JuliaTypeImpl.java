// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.ice1000.julia.lang.psi.JuliaTypes.*;
import org.ice1000.julia.lang.psi.*;

public class JuliaTypeImpl extends JuliaExprImpl implements JuliaType {

  public JuliaTypeImpl(ASTNode node) {
    super(node);
  }

  @Override
  public <R> R accept(@NotNull JuliaVisitor<R> visitor) {
    return visitor.visitType(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JuliaVisitor) accept((JuliaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public JuliaExpr getExpr() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, JuliaExpr.class));
  }

  @Override
  @NotNull
  public JuliaTypeParameters getTypeParameters() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, JuliaTypeParameters.class));
  }

  @Override
  @Nullable
  public JuliaWhereClause getWhereClause() {
    return PsiTreeUtil.getChildOfType(this, JuliaWhereClause.class);
  }

}
