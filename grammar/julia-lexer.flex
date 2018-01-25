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
END_KEYWORD=end
MODULE_KEYWORD=module

INCOMPLETE_STRING=\"([^\"\x00-\x1F\x7F\]\|\[\'\"bnrt]|\\u[a-fA-F0-9]{4})*
STRING={INCOMPLETE_STRING}\"

SYMBOL=[a-zA-Z_]([a-zA-Z\d_\!])*

WHITE_SPACE=[ \t\r]
OTHERWISE=[^ \t\r]

%%

{WHITE_SPACE}+ { return TokenType.WHITE_SPACE; }


{END_KEYWORD} { return JuliaTypes.END_KEYWORD; }
{MODULE_KEYWORD} { return JuliaTypes.MODULE_KEYWORD; }
{SYMBOL} { return JuliaTypes.SYM; }

{INCOMPLETE_STRING} { return TokenType.BAD_CHARACTER; }
{STRING} { return JuliaTypes.STR; }

{OTHERWISE} { return TokenType.BAD_CHARACTER; }
