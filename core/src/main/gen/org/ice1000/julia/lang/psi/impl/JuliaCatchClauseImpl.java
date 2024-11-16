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

public class JuliaCatchClauseImpl extends JuliaCatchDeclarationMixin implements JuliaCatchClause {

  public JuliaCatchClauseImpl(ASTNode node) {
    super(node);
  }

  public <R> R accept(@NotNull JuliaVisitor<R> visitor) {
    return visitor.visitCatchClause(this);
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
  @Nullable
  public JuliaStatements getStatements() {
    return PsiTreeUtil.getChildOfType(this, JuliaStatements.class);
  }

  @Override
  @Nullable
  public JuliaSymbol getSymbol() {
    return PsiTreeUtil.getChildOfType(this, JuliaSymbol.class);
  }

}
