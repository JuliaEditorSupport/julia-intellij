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

public class JuliaAssignOpImpl extends JuliaAssignOpMixin implements JuliaAssignOp {

  public JuliaAssignOpImpl(ASTNode node) {
    super(node);
  }

  public <R> R accept(@NotNull JuliaVisitor<R> visitor) {
    return visitor.visitAssignOp(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JuliaVisitor) accept((JuliaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public JuliaAbstractTypeDeclaration getAbstractTypeDeclaration() {
    return PsiTreeUtil.getChildOfType(this, JuliaAbstractTypeDeclaration.class);
  }

  @Override
  @NotNull
  public List<JuliaComment> getCommentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JuliaComment.class);
  }

  @Override
  @NotNull
  public List<JuliaExpr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JuliaExpr.class);
  }

  @Override
  @Nullable
  public JuliaModuleDeclaration getModuleDeclaration() {
    return PsiTreeUtil.getChildOfType(this, JuliaModuleDeclaration.class);
  }

  @Override
  @Nullable
  public JuliaTypeDeclaration getTypeDeclaration() {
    return PsiTreeUtil.getChildOfType(this, JuliaTypeDeclaration.class);
  }

}
