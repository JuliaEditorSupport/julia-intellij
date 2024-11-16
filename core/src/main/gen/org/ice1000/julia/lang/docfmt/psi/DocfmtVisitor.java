// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.docfmt.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import org.ice1000.julia.lang.docfmt.IDocfmtConfig;

public class DocfmtVisitor<R> extends PsiElementVisitor {

  public R visitConfig(@NotNull DocfmtConfig o) {
    return visitIDocfmtConfig(o);
  }

  public R visitValue(@NotNull DocfmtValue o) {
    return visitPsiElement(o);
  }

  public R visitIDocfmtConfig(@NotNull IDocfmtConfig o) {
    visitElement(o);
    return null;
  }

  public R visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
    return null;
  }

}
