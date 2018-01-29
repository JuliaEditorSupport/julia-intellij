package org.ice1000.julia.lang;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import com.intellij.util.containers.BooleanStack;
import org.ice1000.julia.lang.psi.JuliaTypes;

%%

%{
  private BooleanStack stringTemplateStack = new BooleanStack(25);
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
PRIMITIVE_KEYWORD=primitive
STRUCT_KEYWORD=struct
TYPEALIAS_KEYWORD=typealias
IMMUTABLE_KEYWORD=immutable
MUTABLE_KEYWORD=mutable
TRUE_KEYWORD=true
FALSE_KEYWORD=false
UNION_KEYWORD=union
QUOTE_KEYWORD=quote
BEGIN_KEYWORD=begin
MACRO_KEYWORD=macro
LOCAL_KEYWORD=local
CONST_KEYWORD=const
LET_KEYWORD=let

STRING_UNICODE=\\((u{HEXDIGIT}{4})|(x{HEXDIGIT}{2}))
INCOMPLETE_STRING=\"([^\"\x00-\x1F\x7F]|(\\[^ux])|{STRING_UNICODE})*
STRING={INCOMPLETE_STRING}\"
INCOMPLETE_RAW_STRING=\"\"\"([^\"]|\"(\?!\"\")|\"\"(\?!\"))*
RAW_STRING={INCOMPLETE_RAW_STRING}\"\"\"
INCOMPLETE_CHAR='([^\\\'\x00-\x1F\x7F]|\\[^\'\x00-\x1F\x7F]+)
CHAR_LITERAL={INCOMPLETE_CHAR}'
REGEX_LITERAL=r('([^'\\]|\\.)*'|\"([^\"\\]|\\.)*\")
BYTE_ARRAY_LITERAL=b('([^'\\]|\\.)*'|\"([^\"\\]|\\.)*\")

REGULAR_STRING_PART_LITERAL=[^\$()`]+
STRING_ESCAPE=\\[^]
STRING_INTERPOLATE_START=\$\(

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
COLON_ASSIGN_SYM=:=
SEMICOLON_SYM=;
DOUBLE_COLON=::
QUESTION_SYM=\?
EQ_SYM==
AT_SYM=@
SUBTYPE_SYM=<:
BACK_QUOTE_SYM=`
INTERPOLATE_SYM=\$
INVERSE_DIV_ASSIGN_SYM=\\\\=
INVERSE_DIV_SYM=\\
IS_SYM====|≡
ISNT_SYM=\!==|≢
LAMBDA_ABSTRACTION=->
ARROW_SYM==>
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
INVRESE_PIPE_SYM=<\|
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
DIVIDE_ASSIGN_SYM=\/=|÷=
DIVIDE_SYM=\/|÷
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
IN_SYM=∈
MISC_COMPARISON_SYM=[∉∋∌⊆⊈⊂⊄⊊∝∊∍∥∦∷∺∻∽∾≁≃≄≅≆≇≈≉≊≋≌≍≎≐≑≒≓≔≕≖≗≘≙≚≛≜≝≞≟≣≦≧≨≩≪≫≬≭≮≯≰≱≲≳≴≵≶≷≸≹≺≻≼≽≾≿⊀⊁⊃⊅⊇⊉⊋⊏⊐⊑⊒⊜⊩⊬⊮⊰⊱⊲⊳⊴⊵⊶⊷⋍⋐⋑⋕⋖⋗⋘⋙⋚⋛⋜⋝⋞⋟⋠⋡⋢⋣⋤⋥⋦⋧⋨⋩⋪⋫⋬⋭⋲⋳⋴⋵⋶⋷⋸⋹⋺⋻⋼⋽⋾⋿⟈⟉⟒⦷⧀⧁⧡⧣⧤⧥⩦⩧⩪⩫⩬⩭⩮⩯⩰⩱⩲⩳⩴⩵⩶⩷⩸⩹⩺⩻⩼⩽⩾⩿⪀⪁⪂⪃⪄⪅⪆⪇⪈⪉⪊⪋⪌⪍⪎⪏⪐⪑⪒⪓⪔⪕⪖⪗⪘⪙⪚⪛⪜⪝⪞⪟⪠⪡⪢⪣⪤⪥⪦⪧⪨⪩⪪⪫⪬⪭⪮⪯⪰⪱⪲⪳⪴⪵⪶⪷⪸⪹⪺⪻⪼⪽⪾⪿⫀⫁⫂⫃⫄⫅⫆⫇⫈⫉⫊⫋⫌⫍⫎⫏⫐⫑⫒⫓⫔⫕⫖⫗⫘⫙⫷⫸⫹⫺⊢⊣⟂]
MISC_PLUS_SYM=[⊕⊖⊞⊟++∪∨⊔±∓∔∸≂≏⊎⊽⋎⋓⧺⧻⨈⨢⨣⨤⨥⨦⨧⨨⨩⨪⨫⨬⨭⨮⨹⨺⩁⩂⩅⩊⩌⩏⩐⩒⩔⩖⩗⩛⩝⩡⩢⩣]
MISC_MULTIPLY_SYM=[⋅∘×∩∧⊗⊘⊙⊚⊛⊠⊡⊓∗∙∤⅋≀⊼⋄⋆⋇⋉⋊⋋⋌⋏⋒⟑⦸⦼⦾⦿⧶⧷⨇⨰⨱⨲⨳⨴⨵⨶⨷⨸⨻⨼⨽⩀⩃⩄⩋⩍⩎⩑⩓⩕⩘⩚⩜⩞⩟⩠⫛⊍▷⨝⟕⟖⟗]
MISC_EXPONENT_SYM=[↑↓⇵⟰⟱⤈⤉⤊⤋⤒⤓⥉⥌⥍⥏⥑⥔⥕⥘⥙⥜⥝⥠⥡⥣⥥⥮⥯￪￬]
SPECIAL_ARROW_SYM=--|-->
MISC_ARROW_SYM=[←→↔↚↛↞↠↢↣↦↤↮⇎⇍⇏⇐⇒⇔⇴⇶⇷⇸⇹⇺⇻⇼⇽⇾⇿⟵⟶⟷⟹⟺⟻⟼⟽⟾⟿⤀⤁⤂⤃⤄⤅⤆⤇⤌⤍⤎⤏⤐⤑⤔⤕⤖⤗⤘⤝⤞⤟⤠⥄⥅⥆⥇⥈⥊⥋⥎⥐⥒⥓⥖⥗⥚⥛⥞⥟⥢⥤⥦⥧⥨⥩⥪⥫⥬⥭⥰⧴⬱⬰⬲⬳⬴⬵⬶⬷⬸⬹⬺⬻⬼⬽⬾⬿⭀⭁⭂⭃⭄⭇⭈⭉⭊⭋⭌￩￫⇜⇝↜↝↩↪↫↬↼↽⇀⇁⇄⇆⇇⇉⇋⇌⇚⇛⇠⇢]

FLOAT_CONSTANT=Inf16|Inf32|Inf|-Inf16|-Inf32|-Inf|NaN16|NaN32|NaN
//SYMBOL=[a-zA-Z_]([a-zA-Z\d_\!])+
//SYMBOL=[^\x00-\x20+\-*/\\$#\{\}()\[\]<>|&?~;\"\'\`@]+
SYMBOL={VALID_CHAR}({VALID_CHAR}|[\d\!])*
VALID_CHAR=[a-zA-Z_\u0100-\uffff]
DIGIT=[\d_]

NUM_SUFFIX=-?{DIGIT}+
P_SUFFIX=[pP]{NUM_SUFFIX}
E_SUFFIX=[eE]{NUM_SUFFIX}
F_SUFFIX=[fF]{NUM_SUFFIX}
HEXDIGIT=[a-fA-F0-9]
HEX_NUM=0[xX]{HEXDIGIT}+({P_SUFFIX}|{E_SUFFIX}|{F_SUFFIX})?
OCT_NUM=0[oO][0-7]+
BIN_NUM=0[bB][01]+
DEC_NUM={DIGIT}+({E_SUFFIX}|{F_SUFFIX})?
INTEGER={HEX_NUM}|{OCT_NUM}|{BIN_NUM}|{DEC_NUM}
FLOAT=(({DIGIT}+\.{DIGIT}*)|({DIGIT}*\.{DIGIT}+)){E_SUFFIX}?

EOL=\n
WHITE_SPACE=[ \t\r]
OTHERWISE=[^ \t\r\n]

%state NEST_COMMENT
%state STRING_TEMPLATE

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

<STRING_TEMPLATE> {BACK_QUOTE_SYM} {
  yybegin(YYINITIAL);
  return JuliaTypes.BACK_QUOTE_SYM;
}

<STRING_TEMPLATE> {STRING_UNICODE} { return JuliaTypes.STRING_UNICODE; }
<STRING_TEMPLATE> {STRING_INTERPOLATE_START} {
  stringTemplateStack.push(true);
  yybegin(YYINITIAL);
  return JuliaTypes.STRING_INTERPOLATE_START;
}

<STRING_TEMPLATE> {STRING_ESCAPE} { return JuliaTypes.STRING_ESCAPE; }
<STRING_TEMPLATE> {REGULAR_STRING_PART_LITERAL} { return JuliaTypes.REGULAR_STRING_PART_LITERAL; }
<YYINITIAL> {BACK_QUOTE_SYM} {
  yybegin(STRING_TEMPLATE);
  return JuliaTypes.BACK_QUOTE_SYM;
}

{EOL}+ { return JuliaTypes.EOL; }
{WHITE_SPACE}+ { return TokenType.WHITE_SPACE; }

{BLOCK_COMMENT_BEGIN} {
  yybegin(NEST_COMMENT);
  commentDepth = 0;
  commentTokenStart = getTokenStart();
}

{LINE_COMMENT} { return JuliaTypes.LINE_COMMENT; }
{RAW_STRING} { return JuliaTypes.RAW_STR; }
{INCOMPLETE_RAW_STRING} { return TokenType.BAD_CHARACTER; }
{STRING} { return JuliaTypes.STR; }
{INCOMPLETE_STRING} { return TokenType.BAD_CHARACTER; }
{CHAR_LITERAL} { return JuliaTypes.CHAR_LITERAL; }
{INCOMPLETE_CHAR} { return TokenType.BAD_CHARACTER; }

{LEFT_BRACKET} { stringTemplateStack.push(false); return JuliaTypes.LEFT_BRACKET; }
{RIGHT_BRACKET} {
  if (stringTemplateStack.pop()) yybegin(STRING_TEMPLATE);
  return JuliaTypes.RIGHT_BRACKET;
}

{LEFT_B_BRACKET} { return JuliaTypes.LEFT_B_BRACKET; }
{RIGHT_B_BRACKET} { return JuliaTypes.RIGHT_B_BRACKET; }
{LEFT_M_BRACKET} { return JuliaTypes.LEFT_M_BRACKET; }
{RIGHT_M_BRACKET} { return JuliaTypes.RIGHT_M_BRACKET; }
{DOUBLE_COLON} { return JuliaTypes.DOUBLE_COLON; }
{COLON_SYM} { return JuliaTypes.COLON_SYM; }
{COLON_ASSIGN_SYM} { return JuliaTypes.COLON_ASSIGN_SYM; }
{SEMICOLON_SYM} { return JuliaTypes.SEMICOLON_SYM; }
{COMMA_SYM} { return JuliaTypes.COMMA_SYM; }
{QUESTION_SYM} { return JuliaTypes.QUESTION_SYM; }
{EQ_SYM} { return JuliaTypes.EQ_SYM; }
{AT_SYM} { return JuliaTypes.AT_SYM; }
{SUBTYPE_SYM} { return JuliaTypes.SUBTYPE_SYM; }
{INTERPOLATE_SYM} { return JuliaTypes.INTERPOLATE_SYM; }
{LAMBDA_ABSTRACTION} { return JuliaTypes.LAMBDA_ABSTRACTION; }
{ARROW_SYM} { return JuliaTypes.ARROW_SYM; }
{SLICE_SYM} { return JuliaTypes.SLICE_SYM; }
{AND_SYM} { return JuliaTypes.AND_SYM; }
{OR_SYM} { return JuliaTypes.OR_SYM; }
{DOT_SYM}? {INVERSE_DIV_ASSIGN_SYM} { return JuliaTypes.INVERSE_DIV_ASSIGN_SYM; }
{DOT_SYM}? {INVERSE_DIV_SYM} { return JuliaTypes.INVERSE_DIV_SYM; }
{DOT_SYM}? {NOT_SYM} { return JuliaTypes.NOT_SYM; }
{DOT_SYM}? {IS_SYM} { return JuliaTypes.IS_SYM; }
{DOT_SYM}? {ISNT_SYM} { return JuliaTypes.ISNT_SYM; }
{DOT_SYM}? {PIPE_SYM} { return JuliaTypes.PIPE_SYM; }
{DOT_SYM}? {INVRESE_PIPE_SYM} { return JuliaTypes.INVERSE_PIPE_SYM; }
{DOT_SYM}? {REMAINDER_SYM} { return JuliaTypes.REMAINDER_SYM; }
{DOT_SYM}? {REMAINDER_ASSIGN_SYM} { return JuliaTypes.REMAINDER_ASSIGN_SYM; }
{DOT_SYM}? {SHL_ASSIGN_SYM} { return JuliaTypes.SHL_ASSIGN_SYM; }
{DOT_SYM}? {SHR_ASSIGN_SYM} { return JuliaTypes.SHR_ASSIGN_SYM; }
{DOT_SYM}? {USHR_ASSIGN_SYM} { return JuliaTypes.USHR_ASSIGN_SYM; }
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
{DOT_SYM}? {EXPONENT_SYM} { return JuliaTypes.EXPONENT_SYM; }
{DOT_SYM}? {MINUS_SYM} { return JuliaTypes.MINUS_SYM; }
{DOT_SYM}? {MULTIPLY_SYM} { return JuliaTypes.MULTIPLY_SYM; }
{DOT_SYM}? {PLUS_SYM} { return JuliaTypes.PLUS_SYM; }
{DOT_SYM}? {MINUS_ASSIGN_SYM} { return JuliaTypes.MINUS_ASSIGN_SYM; }
{DOT_SYM}? {PLUS_ASSIGN_SYM} { return JuliaTypes.PLUS_ASSIGN_SYM; }
{DOT_SYM}? {EQUALS_SYM} { return JuliaTypes.EQUALS_SYM; }
{DOT_SYM}? {UNEQUAL_SYM} { return JuliaTypes.UNEQUAL_SYM; }
{DOT_SYM}? {GREATER_THAN_SYM} { return JuliaTypes.GREATER_THAN_SYM; }
{DOT_SYM}? {LESS_THAN_SYM} { return JuliaTypes.LESS_THAN_SYM; }
{DOT_SYM}? {GREATER_THAN_OR_EQUAL_SYM} { return JuliaTypes.GREATER_THAN_OR_EQUAL_SYM; }
{DOT_SYM}? {LESS_THAN_OR_EQUAL_SYM} { return JuliaTypes.LESS_THAN_OR_EQUAL_SYM; }
{DOT_SYM}? {TRANSPOSE_SYM} { return JuliaTypes.TRANSPOSE_SYM; }
{DOT_SYM}? {IN_SYM} { return JuliaTypes.IN_SYM; }
{DOT_SYM}? {MISC_COMPARISON_SYM} { return JuliaTypes.MISC_COMPARISON_SYM; }
{DOT_SYM}? {MISC_PLUS_SYM} { return JuliaTypes.MISC_PLUS_SYM; }
{DOT_SYM}? {MISC_MULTIPLY_SYM} { return JuliaTypes.MISC_MULTIPLY_SYM; }
{DOT_SYM}? {MISC_EXPONENT_SYM} { return JuliaTypes.MISC_EXPONENT_SYM; }
{DOT_SYM}? {FACTORISE_SYM} { return JuliaTypes.FACTORISE_SYM; }
{DOT_SYM}? {BITWISE_AND_SYM} { return JuliaTypes.BITWISE_AND_SYM; }
{DOT_SYM}? {BITWISE_OR_SYM} { return JuliaTypes.BITWISE_OR_SYM; }
{DOT_SYM}? {BITWISE_XOR_SYM} { return JuliaTypes.BITWISE_XOR_SYM; }
{DOT_SYM}? {BITWISE_AND_ASSIGN_SYM} { return JuliaTypes.BITWISE_AND_ASSIGN_SYM; }
{DOT_SYM}? {BITWISE_OR_ASSIGN_SYM} { return JuliaTypes.BITWISE_OR_ASSIGN_SYM; }
{DOT_SYM}? {BITWISE_XOR_ASSIGN_SYM} { return JuliaTypes.BITWISE_XOR_ASSIGN_SYM; }
{DOT_SYM}? {FACTORISE_ASSIGN_SYM} { return JuliaTypes.FACTORISE_ASSIGN_SYM; }
{DOT_SYM}? {MISC_ARROW_SYM} { return JuliaTypes.MISC_ARROW_SYM; }
{DOT_SYM}? {EQ_SYM} { return JuliaTypes.ASSIGN_SYM; }
{DOT_SYM} { return JuliaTypes.DOT_SYM; }
{SPECIAL_ARROW_SYM} { return JuliaTypes.SPECIAL_ARROW_SYM; }
{BITWISE_NOT_SYM} { return JuliaTypes.BITWISE_NOT_SYM; }

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
{PRIMITIVE_KEYWORD} { return JuliaTypes.PRIMITIVE_KEYWORD; }
{STRUCT_KEYWORD} { return JuliaTypes.STRUCT_KEYWORD; }
{TYPEALIAS_KEYWORD} { return JuliaTypes.TYPEALIAS_KEYWORD; }
{IMMUTABLE_KEYWORD} { return JuliaTypes.IMMUTABLE_KEYWORD; }
{MUTABLE_KEYWORD} { return JuliaTypes.MUTABLE_KEYWORD; }
{UNION_KEYWORD} { return JuliaTypes.UNION_KEYWORD; }
{QUOTE_KEYWORD} { return JuliaTypes.QUOTE_KEYWORD; }
{BEGIN_KEYWORD} { return JuliaTypes.BEGIN_KEYWORD; }
{MACRO_KEYWORD} { return JuliaTypes.MACRO_KEYWORD; }
{LOCAL_KEYWORD} { return JuliaTypes.LOCAL_KEYWORD; }
{CONST_KEYWORD} { return JuliaTypes.CONST_KEYWORD; }
{LET_KEYWORD} { return JuliaTypes.LET_KEYWORD; }

{REGEX_LITERAL} { return JuliaTypes.REGEX_LITERAL; }
{BYTE_ARRAY_LITERAL} { return JuliaTypes.BYTE_ARRAY_LITERAL; }
{INTEGER} { return JuliaTypes.INT_LITERAL; }
{FLOAT} { return JuliaTypes.FLOAT_LITERAL; }
{FLOAT_CONSTANT} { return JuliaTypes.FLOAT_CONSTANT; }
{SYMBOL} { return JuliaTypes.SYM; }

{OTHERWISE} { return TokenType.BAD_CHARACTER; }