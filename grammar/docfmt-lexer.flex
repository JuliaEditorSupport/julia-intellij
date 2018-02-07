package org.ice1000.julia.lang.docfmt;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import org.ice1000.julia.lang.docfmt.psi.DocfmtTypes;

%%

%{
  public DocfmtLexer() {
    this((java.io.Reader) null);
  }
%}

%class DocfmtLexer
%implements FlexLexer
%unicode

%function advance
%type IElementType
%eof{
%eof}

LINE_COMMENT=#[^\r\n]*
WHITE_SPACE_CHAR=[\ \n\r\t\f]

%%

\n { return DocfmtTypes.EOL; }
[a-zA-Z]+ { return DocfmtTypes.SYM; }
\d+ { return DocfmtTypes.INT; }
= { return DocfmtTypes.EQ_SYM; }
{LINE_COMMENT} { return DocfmtTypes.LINE_COMMENT; }
{WHITE_SPACE_CHAR} { return TokenType.WHITE_SPACE; }
[^] { return TokenType.BAD_CHARACTER; }
