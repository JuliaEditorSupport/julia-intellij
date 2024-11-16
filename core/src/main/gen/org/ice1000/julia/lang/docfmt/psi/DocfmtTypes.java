// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.docfmt.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.ice1000.julia.lang.docfmt.DocfmtElementType;
import org.ice1000.julia.lang.docfmt.DocfmtTokenType;
import org.ice1000.julia.lang.docfmt.psi.impl.*;

public interface DocfmtTypes {

  IElementType CONFIG = new DocfmtElementType("CONFIG");
  IElementType VALUE = new DocfmtElementType("VALUE");

  IElementType EOL = new DocfmtTokenType("EOL");
  IElementType EQ_SYM = new DocfmtTokenType("EQ_SYM");
  IElementType INT = new DocfmtTokenType("INT");
  IElementType LINE_COMMENT = new DocfmtTokenType("LINE_COMMENT");
  IElementType SYM = new DocfmtTokenType("SYM");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == CONFIG) {
        return new DocfmtConfigImpl(node);
      }
      else if (type == VALUE) {
        return new DocfmtValueImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
