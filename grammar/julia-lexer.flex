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
BAREMODULE_KEYWORD=baremodule
BREAK_KEYWORD=break
CONTINUE_KEYWORD=continue
INCLUDE_KEYWORD=include
EXPORT_KEYWORD=export
IMPORT_KEYWORD=import
USING_KEYWORD=using
IF_KEYWORD=if
ELSEIF_KEYWORD=elseif
ELSE_KEYWORD=else
FOR_KEYWORD=for
IN_KEYWORD=in
WHILE_KEYWORD=while
RETURN_KEYWORD=return
TRY_KEYWORD=try
CATCH_KEYWORD=catch
FINALLY_KEYWORD=finally
FUNCTION_KEYWORD=function
TRUE_KEYWORD=true
FALSE_KEYWORD=false

INCOMPLETE_STRING=\"([^\"\x00-\x1F\x7F]|\\[\'\"bnrt]|(\\u[a-fA-F0-9]{4}))*
STRING={INCOMPLETE_STRING}\"
INCOMPLETE_RAW_STRING=\"\"\"([^\"]|\"(\?!\"\")|\"\"(\?!\"))*
RAW_STRING={INCOMPLETE_RAW_STRING}\"\"\"

LINE_COMMENT=#[^\n]*

LEFT_BRACKET=\(
RIGHT_BRACKET=\)
LEFT_B_BRACKET=\{
RIGHT_B_BRACKET=\}
DOT_SYM=\.
COMMA_SYM=,
COLON_SYM=:
SEMICOLON_SYM=;
DOUBLE_COLON=::
EQ_SYM==

SYMBOL=[a-zA-Z_]([a-zA-Z\d_\!])*

DIGIT=[\d_]

NUM_SUFFIX=-?{DIGIT}+
P_SUFFIX=[pP]{NUM_SUFFIX}
E_SUFFIX=[eE]{NUM_SUFFIX}
F_SUFFIX=[fF]{NUM_SUFFIX}
HEX_NUM=0[xX][0-9a-fA-F]+({P_SUFFIX}|{E_SUFFIX}|{F_SUFFIX})?
OCT_NUM=0[oO][0-7]+
BIN_NUM=0[bB][01]+
DEC_NUM={DIGIT}+({E_SUFFIX}|{F_SUFFIX})?
INTEGER={HEX_NUM}|{OCT_NUM}|{BIN_NUM}|{DEC_NUM}
FLOAT=(({DIGIT}+\.{DIGIT}*)|({DIGIT}*\.{DIGIT}+)){E_SUFFIX}?

EOL=\n
WHITE_SPACE=[ \t\r]
OTHERWISE=[^ \t\r\n]

%state AFTER_EXPR

%%

<AFTER_EXPR> {EOL} { yybegin(YYINITIAL); return JuliaTypes.EOL; }
<AFTER_EXPR> {SEMICOLON_SYM} { yybegin(YYINITIAL); return JuliaTypes.SEMICOLON_SYM; }
<AFTER_EXPR> {SYMBOL} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {INTEGER} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {FLOAT} { return TokenType.BAD_CHARACTER; }

<AFTER_EXPR> {RAW_STRING} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {STRING} { return TokenType.BAD_CHARACTER; }

<AFTER_EXPR> {END_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {MODULE_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {BAREMODULE_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {BREAK_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {CONTINUE_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {INCLUDE_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {EXPORT_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {IF_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {IN_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {IMPORT_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {USING_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {ELSEIF_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {ELSE_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {FOR_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {WHILE_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {RETURN_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {TRY_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {CATCH_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {FINALLY_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {FUNCTION_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {TRUE_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {FALSE_KEYWORD} { return TokenType.BAD_CHARACTER; }

{EOL} { return TokenType.WHITE_SPACE; }
{WHITE_SPACE}+ { return TokenType.WHITE_SPACE; }
{LINE_COMMENT}+ { return JuliaTypes.LINE_COMMENT; }

{LEFT_BRACKET} { return JuliaTypes.LEFT_BRACKET; }
{RIGHT_BRACKET} { return JuliaTypes.RIGHT_BRACKET; }
{LEFT_B_BRACKET} { return JuliaTypes.LEFT_B_BRACKET; }
{RIGHT_B_BRACKET} { return JuliaTypes.RIGHT_B_BRACKET; }
{DOT_SYM} { return JuliaTypes.DOT_SYM; }
{DOUBLE_COLON} { return JuliaTypes.DOUBLE_COLON; }
{COLON_SYM} { return JuliaTypes.COLON_SYM; }
{SEMICOLON_SYM} { return JuliaTypes.SEMICOLON_SYM; }
{COMMA_SYM} { return JuliaTypes.COMMA_SYM; }
{EQ_SYM} { return JuliaTypes.EQ_SYM; }

{END_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.END_KEYWORD; }
{MODULE_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.MODULE_KEYWORD; }
{BAREMODULE_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.BAREMODULE_KEYWORD; }
{BREAK_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.BREAK_KEYWORD; }
{CONTINUE_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.CONTINUE_KEYWORD; }
{INCLUDE_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.INCLUDE_KEYWORD; }
{EXPORT_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.EXPORT_KEYWORD; }
{IF_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.IF_KEYWORD; }
{IN_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.IN_KEYWORD; }
{IMPORT_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.IMPORT_KEYWORD; }
{USING_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.USING_KEYWORD; }
{ELSEIF_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.ELSEIF_KEYWORD; }
{ELSE_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.ELSE_KEYWORD; }
{FOR_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.FOR_KEYWORD; }
{WHILE_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.WHILE_KEYWORD; }
{RETURN_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.RETURN_KEYWORD; }
{TRY_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.TRY_KEYWORD; }
{CATCH_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.CATCH_KEYWORD; }
{FINALLY_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.FINALLY_KEYWORD; }
{FUNCTION_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.FUNCTION_KEYWORD; }
{TRUE_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.TRUE_KEYWORD; }
{FALSE_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.FALSE_KEYWORD; }

{SYMBOL} { yybegin(AFTER_EXPR); return JuliaTypes.SYM; }
{INTEGER} { yybegin(AFTER_EXPR); return JuliaTypes.INT_LITERAL; }
{FLOAT} { yybegin(AFTER_EXPR); return JuliaTypes.FLOAT_LITERAL; }

{RAW_STRING} { yybegin(AFTER_EXPR); return JuliaTypes.RAW_STR; }
{INCOMPLETE_RAW_STRING} { return TokenType.BAD_CHARACTER; }
{STRING} { yybegin(AFTER_EXPR); return JuliaTypes.STR; }
{INCOMPLETE_STRING} { return TokenType.BAD_CHARACTER; }

{OTHERWISE} { return TokenType.BAD_CHARACTER; }
