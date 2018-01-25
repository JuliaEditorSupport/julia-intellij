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

END_KEYWORD=end
MODULE_KEYWORD=module
BREAK_KEYWORD=break
CONTINUE_KEYWORD=continue
INCLUDE_KEYWORD=include
EXPORT_KEYWORD=export
IMPORT_KEYWORD=import
USING_KEYWORD=using
IF_KEYWORD=IF_keyword
ELSEIF_KEYWORD=elseif
ELSE_KEYWORD=else
FOR_KEYWORD=for
IN_KEYWORD=IN_keyword
WHILE_KEYWORD=while
RETURN_KEYWORD=return
TRY_KEYWORD=try
CATCH_KEYWORD=catch
FINALLY_KEYWORD=finally

INCOMPLETE_STRING=\"([^\"\x00-\x1F\x7F\]\|\[\'\"bnrt]|\\u[a-fA-F0-9]{4})*
STRING={INCOMPLETE_STRING}\"
INCOMPLETE_RAW_STRING=\"\"\"([^\"]|\"(\?!\"\")|\"\"(\?!\"))*
RAW_STRING=\"\"\"

LEFT_BRACKET=\(
RIGHT_BRACKET=\)
DOT_SYM=\.
COMMA_SYM=,
COLON_SYM=:
EQ_SYM==

SYMBOL=[a-zA-Z_]([a-zA-Z\d_\!])*

WHITE_SPACE=[ \t\r\n]
OTHERWISE=[^ \t\r\n]

%%

{WHITE_SPACE}+ { return TokenType.WHITE_SPACE; }

{LEFT_BRACKET} { return JuliaTypes.LEFT_BRACKET; }
{RIGHT_BRACKET} { return JuliaTypes.RIGHT_BRACKET; }
{DOT_SYM} { return JuliaTypes.DOT_SYM; }
{COLON_SYM} { return JuliaTypes.COLON_SYM; }
{COMMA_SYM} { return JuliaTypes.COMMA_SYM; }
{EQ_SYM} { return JuliaTypes.EQ_SYM; }

{END_KEYWORD} { return JuliaTypes.END_KEYWORD; }
{MODULE_KEYWORD} { return JuliaTypes.MODULE_KEYWORD; }
{BREAK_KEYWORD} { return JuliaTypes.BREAK_KEYWORD; }
{CONTINUE_KEYWORD} { return JuliaTypes.CONTINUE_KEYWORD; }
{INCLUDE_KEYWORD} { return JuliaTypes.INCLUDE_KEYWORD; }
{EXPORT_KEYWORD} { return JuliaTypes.EXPORT_KEYWORD; }
{IF_KEYWORD} { return JuliaTypes.IF_KEYWORD; }
{IN_KEYWORD} { return JuliaTypes.IN_KEYWORD; }
{IMPORT_KEYWORD} { return JuliaTypes.IMPORT_KEYWORD; }
{USING_KEYWORD} { return JuliaTypes.USING_KEYWORD; }
{ELSEIF_KEYWORD} { return JuliaTypes.ELSEIF_KEYWORD; }
{ELSE_KEYWORD} { return JuliaTypes.ELSE_KEYWORD; }
{FOR_KEYWORD} { return JuliaTypes.FOR_KEYWORD; }
{WHILE_KEYWORD} { return JuliaTypes.WHILE_KEYWORD; }
{RETURN_KEYWORD} { return JuliaTypes.RETURN_KEYWORD; }
{TRY_KEYWORD} { return JuliaTypes.TRY_KEYWORD; }
{CATCH_KEYWORD} { return JuliaTypes.CATCH_KEYWORD; }
{FINALLY_KEYWORD} { return JuliaTypes.FINALLY_KEYWORD; }

{SYMBOL} { return JuliaTypes.SYM; }

{INCOMPLETE_RAW_STRING} { return TokenType.BAD_CHARACTER; }
{RAW_STRING} { return JuliaTypes.RAW_STR; }
{INCOMPLETE_STRING} { return TokenType.BAD_CHARACTER; }
{STRING} { return JuliaTypes.STR; }

{OTHERWISE} { return TokenType.BAD_CHARACTER; }
