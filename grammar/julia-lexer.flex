package org.ice1000.julia.lang;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import org.ice1000.julia.lang.psi.JuliaTypes;

%%

%{
  private int commentDepth = 0;
  private int commentTokenStart = 0;
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
TYPE_KEYWORD=type
ABSTRACT_KEYWORD=abstract
TYPEALIAS_KEYWORD=typealias
IMMUTABLE_KEYWORD=immutable
TRUE_KEYWORD=true
FALSE_KEYWORD=false
UNION_KEYWORD=union
QUOTE_KEYWORD=quote

STRING_UNICODE=\\((u[a-fA-F0-9]{4})|(x[a-fA-F0-9]{2}))
INCOMPLETE_STRING=\"([^\"\x00-\x1F\x7F]|(\\[^ux])|{STRING_UNICODE})*
STRING={INCOMPLETE_STRING}\"
INCOMPLETE_RAW_STRING=\"\"\"([^\"]|\"(\?!\"\")|\"\"(\?!\"))*
RAW_STRING={INCOMPLETE_RAW_STRING}\"\"\"
INCOMPLETE_CHAR='([^\\\'\x00-\x1F\x7F]|\\[^\x00-\x1F\x7F]+)
CHAR_LITERAL={INCOMPLETE_CHAR}'
REGEX_LITERAL=r('([^'\\]|\\.)*'|\"([^\"\\]|\\.)*\")
BYTE_ARRAY_LITERAL=b('([^'\\]|\\.)*'|\"([^\"\\]|\\.)*\")

LINE_COMMENT=#(\n|[^\n=][^\n]*)
BLOCK_COMMENT_BEGIN=#=
BLOCK_COMMENT_END==#
BLOCK_COMMENT_CONTENT=[^#=]|(=+[^#])

LEFT_BRACKET=\(
RIGHT_BRACKET=\)
LEFT_B_BRACKET=\{
RIGHT_B_BRACKET=\}
LEFT_M_BRACKET=\[
RIGHT_M_BRACKET=\]
DOT_SYM=\.
COMMA_SYM=,
COLON_SYM=:
SEMICOLON_SYM=;
DOUBLE_COLON=::
QUESTION_SYM=\?
EQ_SYM==
AT_SYM=@
SUBTYPE_SYM=<:
INTERPOLATE_SYM=\$
INVERSE_DIV_ASSIGN_SYM=\\\\=
INVERSE_DIV_SYM=\\
IS_SYM====|≡
ISNT_SYM=\!==|≢
LAMBDA_ABSTRACTION=->
SLICE_SYM=\.\.\.
REMAINDER_SYM=%
REMAINDER_ASSIGN_SYM=%=
LESS_THAN_SYM=<
LESS_THAN_OR_EQUAL_SYM=<=|≤
USHR_ASSIGN_SYM=>>>=
USHR_SYM=>>>
AND_SYM=&&
OR_SYM=\|\|
PIPE_SYM=\|>
SHL_SYM=<<
SHL_ASSIGN_SYM=<<=
SHR_SYM=>>
SHR_ASSIGN_SYM=>>=
PLUS_SYM=\+
PLUS_ASSIGN_SYM=\+=
MINUS_SYM=-
MINUS_ASSIGN_SYM=-=
MULTIPLY_SYM=\*
MULTIPLY_ASSIGN_SYM=\*=
UNEQUAL_SYM=\!=|≠
FRACTION_ASSIGN_SYM=\/\/=
FRACTION_SYM=\/\/
GREATER_THAN_SYM=>
GREATER_THAN_OR_EQUAL_SYM=>=|≥
DIVIDE_ASSIGN_SYM=\/=
DIVIDE_SYM=\/
TRANSPOSE_SYM='
FACTORISE_ASSIGN_SYM=\.\\\\=
FACTORISE_SYM=\.\\\\
EXPONENT_ASSIGN_SYM=\^=
EXPONENT_SYM=\^
EQUALS_SYM===
NOT_SYM=\!
BITWISE_NOT_SYM=\~
BITWISE_AND_SYM=\&
BITWISE_AND_ASSIGN_SYM=\&=
BITWISE_OR_SYM=\|
BITWISE_OR_ASSIGN_SYM=\|=
BITWISE_XOR_SYM=\$|⊻
BITWISE_XOR_ASSIGN_SYM=\$=|⊻=
MISC_COMPARISION_SYM=∈|∉|∋|∌|⊆|⊈|⊂|⊄|⊊|∝|∊|∍|∥|∦|∷|∺|∻|∽|∾|≁|≃|≄|≅|≆|≇|≈|≉|≊|≋|≌|≍|≎|≐|≑|≒|≓|≔|≕|≖|≗|≘|≙|≚|≛|≜|≝|≞|≟|≣|≦|≧|≨|≩|≪|≫|≬|≭|≮|≯|≰|≱|≲|≳|≴|≵|≶|≷|≸|≹|≺|≻|≼|≽|≾|≿|⊀|⊁|⊃|⊅|⊇|⊉|⊋|⊏|⊐|⊑|⊒|⊜|⊩|⊬|⊮|⊰|⊱|⊲|⊳|⊴|⊵|⊶|⊷|⋍|⋐|⋑|⋕|⋖|⋗|⋘|⋙|⋚|⋛|⋜|⋝|⋞|⋟|⋠|⋡|⋢|⋣|⋤|⋥|⋦|⋧|⋨|⋩|⋪|⋫|⋬|⋭|⋲|⋳|⋴|⋵|⋶|⋷|⋸|⋹|⋺|⋻|⋼|⋽|⋾|⋿|⟈|⟉|⟒|⦷|⧀|⧁|⧡|⧣|⧤|⧥|⩦|⩧|⩪|⩫|⩬|⩭|⩮|⩯|⩰|⩱|⩲|⩳|⩴|⩵|⩶|⩷|⩸|⩹|⩺|⩻|⩼|⩽|⩾|⩿|⪀|⪁|⪂|⪃|⪄|⪅|⪆|⪇|⪈|⪉|⪊|⪋|⪌|⪍|⪎|⪏|⪐|⪑|⪒|⪓|⪔|⪕|⪖|⪗|⪘|⪙|⪚|⪛|⪜|⪝|⪞|⪟|⪠|⪡|⪢|⪣|⪤|⪥|⪦|⪧|⪨|⪩|⪪|⪫|⪬|⪭|⪮|⪯|⪰|⪱|⪲|⪳|⪴|⪵|⪶|⪷|⪸|⪹|⪺|⪻|⪼|⪽|⪾|⪿|⫀|⫁|⫂|⫃|⫄|⫅|⫆|⫇|⫈|⫉|⫊|⫋|⫌|⫍|⫎|⫏|⫐|⫑|⫒|⫓|⫔|⫕|⫖|⫗|⫘|⫙|⫷|⫸|⫹|⫺|⊢|⊣|⟂

FLOAT_CONSTANT=Inf16|Inf32|Inf|-Inf16|-Inf32|-Inf|NaN16|NaN32|NaN
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

%state NEST_COMMENT

%%

<NEST_COMMENT> {BLOCK_COMMENT_BEGIN} { ++commentDepth; }
<NEST_COMMENT> {BLOCK_COMMENT_CONTENT}+ { }
<NEST_COMMENT> <<EOF>> {
  yybegin(YYINITIAL);
  zzStartRead = commentTokenStart;
  return JuliaTypes.BLOCK_COMMENT;
}

<NEST_COMMENT> {BLOCK_COMMENT_END} {
  if (commentDepth > 0) {
    --commentDepth;
  } else {
    yybegin(YYINITIAL);
    zzStartRead = commentTokenStart;
    return JuliaTypes.BLOCK_COMMENT;
  }
}

{EOL}+ { return JuliaTypes.EOL; }
{WHITE_SPACE}+ { return TokenType.WHITE_SPACE; }

{BLOCK_COMMENT_BEGIN} {
  yybegin(NEST_COMMENT);
  commentDepth = 0;
  commentTokenStart = getTokenStart();
}

{LINE_COMMENT} { return JuliaTypes.LINE_COMMENT; }

{LEFT_BRACKET} { return JuliaTypes.LEFT_BRACKET; }
{RIGHT_BRACKET} { return JuliaTypes.RIGHT_BRACKET; }
{LEFT_B_BRACKET} { return JuliaTypes.LEFT_B_BRACKET; }
{RIGHT_B_BRACKET} { return JuliaTypes.RIGHT_B_BRACKET; }
{LEFT_M_BRACKET} { return JuliaTypes.LEFT_M_BRACKET; }
{RIGHT_M_BRACKET} { return JuliaTypes.RIGHT_M_BRACKET; }
{DOT_SYM} { return JuliaTypes.DOT_SYM; }
{DOUBLE_COLON} { return JuliaTypes.DOUBLE_COLON; }
{COLON_SYM} { return JuliaTypes.COLON_SYM; }
{SEMICOLON_SYM} { return JuliaTypes.SEMICOLON_SYM; }
{COMMA_SYM} { return JuliaTypes.COMMA_SYM; }
{QUESTION_SYM} { return JuliaTypes.QUESTION_SYM; }
{EQ_SYM} { return JuliaTypes.EQ_SYM; }
{AT_SYM} { return JuliaTypes.AT_SYM; }
{SUBTYPE_SYM} { return JuliaTypes.SUBTYPE_SYM; }
{INTERPOLATE_SYM} { return JuliaTypes.INTERPOLATE_SYM; }
{INVERSE_DIV_ASSIGN_SYM} { return JuliaTypes.INVERSE_DIV_ASSIGN_SYM; }
{INVERSE_DIV_SYM} { return JuliaTypes.INVERSE_DIV_SYM; }
{IS_SYM} { return JuliaTypes.IS_SYM; }
{ISNT_SYM} { return JuliaTypes.ISNT_SYM; }
{LAMBDA_ABSTRACTION} { return JuliaTypes.LAMBDA_ABSTRACTION; }
{SLICE_SYM} { return JuliaTypes.SLICE_SYM; }
{REMAINDER_SYM} { return JuliaTypes.REMAINDER_SYM; }
{REMAINDER_ASSIGN_SYM} { return JuliaTypes.REMAINDER_ASSIGN_SYM; }
{LESS_THAN_SYM} { return JuliaTypes.LESS_THAN_SYM; }
{LESS_THAN_OR_EQUAL_SYM} { return JuliaTypes.LESS_THAN_OR_EQUAL_SYM; }
{USHR_ASSIGN_SYM} { return JuliaTypes.USHR_ASSIGN_SYM; }
{USHR_SYM} { return JuliaTypes.USHR_SYM; }
{AND_SYM} { return JuliaTypes.AND_SYM; }
{OR_SYM} { return JuliaTypes.OR_SYM; }
{PIPE_SYM} { return JuliaTypes.PIPE_SYM; }
{DOT_SYM}? {SHL_SYM} { return JuliaTypes.SHL_SYM; }
{DOT_SYM}? {SHR_SYM} { return JuliaTypes.SHR_SYM; }
{DOT_SYM}? {USHR_SYM} { return JuliaTypes.USHR_SYM; }
{DOT_SYM}? {FRACTION_SYM} { return JuliaTypes.FRACTION_SYM; }
{DOT_SYM}? {DIVIDE_SYM} { return JuliaTypes.DIVIDE_SYM; }
{DOT_SYM}? {DIVIDE_ASSIGN_SYM} { return JuliaTypes.DIVIDE_ASSIGN_SYM; }
{DOT_SYM}? {EXPONENT_ASSIGN_SYM} { return JuliaTypes.EXPONENT_ASSIGN_SYM; }
{DOT_SYM}? {FRACTION_ASSIGN_SYM} { return JuliaTypes.FRACTION_ASSIGN_SYM; }
{DOT_SYM}? {MULTIPLY_ASSIGN_SYM} { return JuliaTypes.MULTIPLY_ASSIGN_SYM; }
{DOT_SYM}? {REMAINDER_ASSIGN_SYM} { return JuliaTypes.REMAINDER_ASSIGN_SYM; }
{DOT_SYM}? {REMAINDER_SYM} { return JuliaTypes.REMAINDER_SYM; }
{DOT_SYM}? {EXPONENT_SYM} { return JuliaTypes.EXPONENT_SYM; }
{DOT_SYM}? {MINUS_SYM} { return JuliaTypes.MINUS_SYM; }
{DOT_SYM}? {MINUS_ASSIGN_SYM} { return JuliaTypes.MINUS_ASSIGN_SYM; }
{DOT_SYM}? {MULTIPLY_SYM} { return JuliaTypes.MULTIPLY_SYM; }
{DOT_SYM}? {PLUS_SYM} { return JuliaTypes.PLUS_SYM; }
{DOT_SYM}? {PLUS_ASSIGN_SYM} { return JuliaTypes.PLUS_ASSIGN_SYM; }
{DOT_SYM}? {EQUALS_SYM} { return JuliaTypes.EQUALS_SYM; }
{DOT_SYM}? {UNEQUAL_SYM} { return JuliaTypes.UNEQUAL_SYM; }
{DOT_SYM}? {GREATER_THAN_SYM} { return JuliaTypes.GREATER_THAN_SYM; }
{DOT_SYM}? {LESS_THAN_SYM} { return JuliaTypes.LESS_THAN_SYM; }
{DOT_SYM}? {GREATER_THAN_OR_EQUAL_SYM} { return JuliaTypes.GREATER_THAN_OR_EQUAL_SYM; }
{DOT_SYM}? {LESS_THAN_OR_EQUAL_SYM} { return JuliaTypes.LESS_THAN_OR_EQUAL_SYM; }
{DOT_SYM}? {TRANSPOSE_SYM} { return JuliaTypes.TRANSPOSE_SYM; }
{FACTORISE_ASSIGN_SYM} { return JuliaTypes.FACTORISE_ASSIGN_SYM; }
{FACTORISE_SYM} { return JuliaTypes.FACTORISE_SYM; }
{BITWISE_NOT_SYM} { return JuliaTypes.BITWISE_NOT_SYM; }
{BITWISE_AND_SYM} { return JuliaTypes.BITWISE_AND_SYM; }
{BITWISE_AND_ASSIGN_SYM} { return JuliaTypes.BITWISE_AND_ASSIGN_SYM; }
{BITWISE_OR_SYM} { return JuliaTypes.BITWISE_OR_SYM; }
{BITWISE_XOR_SYM} { return JuliaTypes.BITWISE_XOR_SYM; }
{BITWISE_OR_ASSIGN_SYM} { return JuliaTypes.BITWISE_OR_ASSIGN_SYM; }
{BITWISE_XOR_ASSIGN_SYM} { return JuliaTypes.BITWISE_XOR_ASSIGN_SYM; }

{END_KEYWORD} { return JuliaTypes.END_KEYWORD; }
{BREAK_KEYWORD} { return JuliaTypes.BREAK_KEYWORD; }
{CONTINUE_KEYWORD} { return JuliaTypes.CONTINUE_KEYWORD; }
{TRUE_KEYWORD} { return JuliaTypes.TRUE_KEYWORD; }
{FALSE_KEYWORD} { return JuliaTypes.FALSE_KEYWORD; }
{MODULE_KEYWORD} { return JuliaTypes.MODULE_KEYWORD; }
{BAREMODULE_KEYWORD} { return JuliaTypes.BAREMODULE_KEYWORD; }
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
{FUNCTION_KEYWORD} { return JuliaTypes.FUNCTION_KEYWORD; }
{TYPE_KEYWORD} { return JuliaTypes.TYPE_KEYWORD; }
{ABSTRACT_KEYWORD} { return JuliaTypes.ABSTRACT_KEYWORD; }
{TYPEALIAS_KEYWORD} { return JuliaTypes.TYPEALIAS_KEYWORD; }
{IMMUTABLE_KEYWORD} { return JuliaTypes.IMMUTABLE_KEYWORD; }
{UNION_KEYWORD} { return JuliaTypes.UNION_KEYWORD; }
{QUOTE_KEYWORD} { return JuliaTypes.QUOTE_KEYWORD; }

{REGEX_LITERAL} { return JuliaTypes.REGEX_LITERAL; }
{BYTE_ARRAY_LITERAL} { return JuliaTypes.BYTE_ARRAY_LITERAL; }
{INTEGER} { return JuliaTypes.INT_LITERAL; }
{FLOAT} { return JuliaTypes.FLOAT_LITERAL; }
{FLOAT_CONSTANT} { return JuliaTypes.FLOAT_CONSTANT; }
{SYMBOL} { return JuliaTypes.SYM; }

{RAW_STRING} { return JuliaTypes.RAW_STR; }
{INCOMPLETE_RAW_STRING} { return TokenType.BAD_CHARACTER; }
{STRING} { return JuliaTypes.STR; }
{INCOMPLETE_STRING} { return TokenType.BAD_CHARACTER; }
{CHAR_LITERAL} { return JuliaTypes.CHAR_LITERAL; }
{INCOMPLETE_CHAR} { return TokenType.BAD_CHARACTER; }

{OTHERWISE} { return TokenType.BAD_CHARACTER; }