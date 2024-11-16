// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.docfmt.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.ice1000.julia.lang.docfmt.psi.DocfmtTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.ice1000.julia.lang.docfmt.psi.*;

public class DocfmtValueImpl extends ASTWrapperPsiElement implements DocfmtValue {

  public DocfmtValueImpl(@NotNull ASTNode node) {
    super(node);
  }

  public <R> R accept(@NotNull DocfmtVisitor<R> visitor) {
    return visitor.visitValue(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof DocfmtVisitor) accept((DocfmtVisitor)visitor);
    else super.accept(visitor);
  }

}
