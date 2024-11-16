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

public class JuliaCommandImpl extends JuliaExprImpl implements JuliaCommand {

  public JuliaCommandImpl(ASTNode node) {
    super(node);
  }

  @Override
  public <R> R accept(@NotNull JuliaVisitor<R> visitor) {
    return visitor.visitCommand(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JuliaVisitor) accept((JuliaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<JuliaStringContent> getStringContentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JuliaStringContent.class);
  }

  @Override
  @NotNull
  public List<JuliaTemplate> getTemplateList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JuliaTemplate.class);
  }

}
