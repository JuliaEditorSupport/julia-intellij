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

public class JuliaImportAllExprImpl extends JuliaExprImpl implements JuliaImportAllExpr {

  public JuliaImportAllExprImpl(ASTNode node) {
    super(node);
  }

  @Override
  public <R> R accept(@NotNull JuliaVisitor<R> visitor) {
    return visitor.visitImportAllExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JuliaVisitor) accept((JuliaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public JuliaMemberAccess getMemberAccess() {
    return PsiTreeUtil.getChildOfType(this, JuliaMemberAccess.class);
  }

}