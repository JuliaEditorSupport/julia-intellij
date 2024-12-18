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
import com.intellij.psi.stubs.IStubElementType;

public class JuliaAbstractTypeDeclarationImpl extends JuliaAbstractTypeDeclarationMixin implements JuliaAbstractTypeDeclaration {

  public JuliaAbstractTypeDeclarationImpl(ASTNode node) {
    super(node);
  }

  public JuliaAbstractTypeDeclarationImpl(JuliaAbstractTypeDeclarationClassStub stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public <R> R accept(@NotNull JuliaVisitor<R> visitor) {
    return visitor.visitAbstractTypeDeclaration(this);
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
  public List<JuliaExpr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JuliaExpr.class);
  }

  @Override
  @Nullable
  public JuliaTypeParameters getTypeParameters() {
    return PsiTreeUtil.getChildOfType(this, JuliaTypeParameters.class);
  }

}