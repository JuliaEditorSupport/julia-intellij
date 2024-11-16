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

public class JuliaUserTypeImpl extends ASTWrapperPsiElement implements JuliaUserType {

  public JuliaUserTypeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public <R> R accept(@NotNull JuliaVisitor<R> visitor) {
    return visitor.visitUserType(this);
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
  @Nullable
  public JuliaTypeParameters getTypeParameters() {
    return PsiTreeUtil.getChildOfType(this, JuliaTypeParameters.class);
  }

}
