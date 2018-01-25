package org.ice1000.julia.lang;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import org.ice1000.julia.lang.psi.JuliaTypes;

%%

%{
  public JuliaLexer() { this((java.io.Reader) null); }
%}

%class JuliaLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{ return;
%eof}

EOL=\n

INCOMPLETE_STRING=\"([^\"\x00-\x1F\x7F\]\|\[\'\"bnrt]|\\u[a-fA-F0-9]{4})*
STRING={INCOMPLETE_STRING}\"

%%

{INCOMPLETE_STRING} { return TokenType.BAD_CHARACTER; }
{STRING} { yybegin(YYINITIAL); return JuliaTypes.STR; }
