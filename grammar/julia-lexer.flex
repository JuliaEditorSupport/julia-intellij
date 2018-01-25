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

// must be after expressions
<AFTER_EXPR> {EOL} { yybegin(YYINITIAL); return JuliaTypes.EOL; }
<AFTER_EXPR> {SEMICOLON_SYM} { yybegin(YYINITIAL); return JuliaTypes.SEMICOLON_SYM; }

// cannot be put after an expression with no eol between
<AFTER_EXPR> {SYMBOL} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {INTEGER} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {FLOAT} { return TokenType.BAD_CHARACTER; }

<AFTER_EXPR> {RAW_STRING} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {STRING} { return TokenType.BAD_CHARACTER; }

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
<AFTER_EXPR> {FOR_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {WHILE_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {RETURN_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {TRY_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {FUNCTION_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {TRUE_KEYWORD} { return TokenType.BAD_CHARACTER; }
<AFTER_EXPR> {FALSE_KEYWORD} { return TokenType.BAD_CHARACTER; }

{EOL} { return TokenType.WHITE_SPACE; }
{WHITE_SPACE}+ { return TokenType.WHITE_SPACE; }
{LINE_COMMENT}+ { yybegin(YYINITIAL); return JuliaTypes.LINE_COMMENT; }

// can be put before an expression with no eol between
{LEFT_BRACKET} { yybegin(YYINITIAL); return JuliaTypes.LEFT_BRACKET; }
{RIGHT_BRACKET} { yybegin(YYINITIAL); return JuliaTypes.RIGHT_BRACKET; }
{LEFT_B_BRACKET} { yybegin(YYINITIAL); return JuliaTypes.LEFT_B_BRACKET; }
{RIGHT_B_BRACKET} { yybegin(YYINITIAL); return JuliaTypes.RIGHT_B_BRACKET; }
{DOT_SYM} { yybegin(YYINITIAL); return JuliaTypes.DOT_SYM; }
{DOUBLE_COLON} { yybegin(YYINITIAL); return JuliaTypes.DOUBLE_COLON; }
{COLON_SYM} { yybegin(YYINITIAL); return JuliaTypes.COLON_SYM; }
{SEMICOLON_SYM} { yybegin(YYINITIAL); return JuliaTypes.SEMICOLON_SYM; }
{COMMA_SYM} { yybegin(YYINITIAL); return JuliaTypes.COMMA_SYM; }
{EQ_SYM} { yybegin(YYINITIAL); return JuliaTypes.EQ_SYM; }

// cannot be put before an expression with no eol between
{END_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.END_KEYWORD; }
{BREAK_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.BREAK_KEYWORD; }
{CONTINUE_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.CONTINUE_KEYWORD; }
{TRUE_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.TRUE_KEYWORD; }
{FALSE_KEYWORD} { yybegin(AFTER_EXPR); return JuliaTypes.FALSE_KEYWORD; }

// can be put before an expression with no eol between
{MODULE_KEYWORD} { yybegin(YYINITIAL); return JuliaTypes.MODULE_KEYWORD; }
{BAREMODULE_KEYWORD} { yybegin(YYINITIAL); return JuliaTypes.BAREMODULE_KEYWORD; }
{INCLUDE_KEYWORD} { yybegin(YYINITIAL); return JuliaTypes.INCLUDE_KEYWORD; }
{EXPORT_KEYWORD} { yybegin(YYINITIAL); return JuliaTypes.EXPORT_KEYWORD; }
{IF_KEYWORD} { yybegin(YYINITIAL); return JuliaTypes.IF_KEYWORD; }
{IN_KEYWORD} { yybegin(YYINITIAL); return JuliaTypes.IN_KEYWORD; }
{IMPORT_KEYWORD} { yybegin(YYINITIAL); return JuliaTypes.IMPORT_KEYWORD; }
{USING_KEYWORD} { yybegin(YYINITIAL); return JuliaTypes.USING_KEYWORD; }
{ELSEIF_KEYWORD} { yybegin(YYINITIAL); return JuliaTypes.ELSEIF_KEYWORD; }
{ELSE_KEYWORD} { yybegin(YYINITIAL); return JuliaTypes.ELSE_KEYWORD; }
{FOR_KEYWORD} { yybegin(YYINITIAL); return JuliaTypes.FOR_KEYWORD; }
{WHILE_KEYWORD} { yybegin(YYINITIAL); return JuliaTypes.WHILE_KEYWORD; }
{RETURN_KEYWORD} { yybegin(YYINITIAL); return JuliaTypes.RETURN_KEYWORD; }
{TRY_KEYWORD} { yybegin(YYINITIAL); return JuliaTypes.TRY_KEYWORD; }
{CATCH_KEYWORD} { yybegin(YYINITIAL); return JuliaTypes.CATCH_KEYWORD; }
{FINALLY_KEYWORD} { yybegin(YYINITIAL); return JuliaTypes.FINALLY_KEYWORD; }
{FUNCTION_KEYWORD} { yybegin(YYINITIAL); return JuliaTypes.FUNCTION_KEYWORD; }

// cannot be put before an expression with no eol between
{SYMBOL} { yybegin(AFTER_EXPR); return JuliaTypes.SYM; }
{INTEGER} { yybegin(AFTER_EXPR); return JuliaTypes.INT_LITERAL; }
{FLOAT} { yybegin(AFTER_EXPR); return JuliaTypes.FLOAT_LITERAL; }

// cannot be put before an expression with no eol between
{RAW_STRING} { yybegin(AFTER_EXPR); return JuliaTypes.RAW_STR; }
{INCOMPLETE_RAW_STRING} { return TokenType.BAD_CHARACTER; }
{STRING} { yybegin(AFTER_EXPR); return JuliaTypes.STR; }
{INCOMPLETE_STRING} { return TokenType.BAD_CHARACTER; }

{OTHERWISE} { return TokenType.BAD_CHARACTER; }
