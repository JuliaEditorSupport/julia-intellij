package org.ice1000.julia.lang;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import com.intellij.util.containers.IntStack;
import org.ice1000.julia.lang.psi.JuliaTypes;

%%

%{
  private IntStack stateStack = new IntStack(25);
  private IntStack leftBracketStack = new IntStack(25);
  private int leftBracketCount = 0;
  private int commentDepth = 0;
  private int commentTokenStart = 0;

  private void pushState(int state) {
    stateStack.push(yystate());
    leftBracketStack.push(leftBracketCount);
    leftBracketCount = 0;
    yybegin(state);
  }

  private void popState() {
    leftBracketCount = leftBracketStack.pop();
    yybegin(stateStack.pop());
  }

  public JuliaLexer() {
    this((java.io.Reader) null);
  }
%}

%class JuliaLexer
%implements FlexLexer
%unicode

%function advance
%type IElementType
%eof{
  leftBracketCount = 0;
  stateStack.clear();
  leftBracketStack.clear();
  stateStack = null;
  leftBracketStack = null;
%eof}

STRING_UNICODE=\\((u{HEXDIGIT}{4})|(x{HEXDIGIT}{2}))
CHAR_LITERAL='([^\\\'\x00-\x1F\x7F]|\\[^\'\x00-\x1F\x7F]+)'

STRING_ESCAPE=\\[^]
STRING_INTERPOLATE_START=\$\(

LINE_COMMENT=#(\n|[^\n=][^\n]*)
BLOCK_COMMENT_BEGIN=#=
BLOCK_COMMENT_END==#
BLOCK_COMMENT_CONTENT=[^#=]|(=[^#])

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
QUOTE_SYM=\"
TRIPLE_QUOTE_SYM=\"\"\"
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
SPECIAL_ARROW_SYM=-->?
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
OTHERWISE=[^]

%state NEST_COMMENT
%state STRING_TEMPLATE
%state LONG_TEMPLATE
%state RAW_STRING_TEMPLATE
%state CMD_TEMPLATE
%state AFTER_INTERPOLATE

%%

<NEST_COMMENT> {BLOCK_COMMENT_BEGIN} { ++commentDepth; }
<NEST_COMMENT> {BLOCK_COMMENT_CONTENT}+ { }
<NEST_COMMENT> <<EOF>> {
  popState();
  zzStartRead = commentTokenStart;
  return JuliaTypes.BLOCK_COMMENT;
}

<NEST_COMMENT> {BLOCK_COMMENT_END} {
  if (commentDepth > 0) {
    --commentDepth;
  } else {
    popState();
    zzStartRead = commentTokenStart;
    return JuliaTypes.BLOCK_COMMENT;
  }
}

<CMD_TEMPLATE> {BACK_QUOTE_SYM} { popState(); return JuliaTypes.BACK_QUOTE_END; }
<STRING_TEMPLATE> {QUOTE_SYM} { popState(); return JuliaTypes.QUOTE_END; }
<RAW_STRING_TEMPLATE> {TRIPLE_QUOTE_SYM} { popState(); return JuliaTypes.TRIPLE_QUOTE_END; }

<STRING_TEMPLATE, CMD_TEMPLATE, RAW_STRING_TEMPLATE> {STRING_UNICODE} { return JuliaTypes.STRING_UNICODE; }
<AFTER_INTERPOLATE> {SYMBOL} { popState(); return JuliaTypes.SYM; }
<STRING_TEMPLATE, CMD_TEMPLATE, RAW_STRING_TEMPLATE> {STRING_INTERPOLATE_START} {
  pushState(LONG_TEMPLATE);
  return JuliaTypes.STRING_INTERPOLATE_START;
}

<STRING_TEMPLATE, CMD_TEMPLATE, RAW_STRING_TEMPLATE> {INTERPOLATE_SYM} {
  pushState(AFTER_INTERPOLATE);
  return JuliaTypes.INTERPOLATE_SYM;
}

<STRING_TEMPLATE, CMD_TEMPLATE, RAW_STRING_TEMPLATE> {STRING_ESCAPE} { return JuliaTypes.STRING_ESCAPE; }
<STRING_TEMPLATE> [^$\\\"]+ { return JuliaTypes.REGULAR_STRING_PART_LITERAL; }
<CMD_TEMPLATE> [^$\\`]+ { return JuliaTypes.REGULAR_STRING_PART_LITERAL; }
<RAW_STRING_TEMPLATE> ([^$\\\"]|\"[^$\\\"]|\"\"[^$\\\"])+ { return JuliaTypes.REGULAR_STRING_PART_LITERAL; }
<YYINITIAL, LONG_TEMPLATE> {BACK_QUOTE_SYM} { pushState(CMD_TEMPLATE); return JuliaTypes.BACK_QUOTE_START; }
<YYINITIAL, LONG_TEMPLATE> {QUOTE_SYM} { pushState(STRING_TEMPLATE); return JuliaTypes.QUOTE_START; }
<YYINITIAL, LONG_TEMPLATE> {TRIPLE_QUOTE_SYM} { pushState(RAW_STRING_TEMPLATE); return JuliaTypes.TRIPLE_QUOTE_START; }

<YYINITIAL, LONG_TEMPLATE> {EOL}+ { return JuliaTypes.EOL; }
<YYINITIAL, LONG_TEMPLATE> {WHITE_SPACE}+ { return TokenType.WHITE_SPACE; }
<YYINITIAL, LONG_TEMPLATE> {BLOCK_COMMENT_BEGIN} {
  pushState(NEST_COMMENT);
  commentDepth = 0;
  commentTokenStart = getTokenStart();
}

<YYINITIAL, LONG_TEMPLATE> {LINE_COMMENT} { return JuliaTypes.LINE_COMMENT; }
<YYINITIAL, LONG_TEMPLATE> {CHAR_LITERAL} { return JuliaTypes.CHAR_LITERAL; }

<YYINITIAL> {LEFT_BRACKET} { return JuliaTypes.LEFT_BRACKET; }
<YYINITIAL> {RIGHT_BRACKET} { return JuliaTypes.RIGHT_BRACKET; }
<LONG_TEMPLATE> {LEFT_BRACKET} { ++leftBracketCount; return JuliaTypes.LEFT_BRACKET; }
<LONG_TEMPLATE> {RIGHT_BRACKET} {
  if (leftBracketCount == 0) {
    popState();
    return JuliaTypes.STRING_INTERPOLATE_END;
  }
  --leftBracketCount;
  return JuliaTypes.RIGHT_BRACKET;
}

<YYINITIAL, LONG_TEMPLATE> {LEFT_B_BRACKET} { return JuliaTypes.LEFT_B_BRACKET; }
<YYINITIAL, LONG_TEMPLATE> {RIGHT_B_BRACKET} { return JuliaTypes.RIGHT_B_BRACKET; }
<YYINITIAL, LONG_TEMPLATE> {LEFT_M_BRACKET} { return JuliaTypes.LEFT_M_BRACKET; }
<YYINITIAL, LONG_TEMPLATE> {RIGHT_M_BRACKET} { return JuliaTypes.RIGHT_M_BRACKET; }
<YYINITIAL, LONG_TEMPLATE> {DOUBLE_COLON} { return JuliaTypes.DOUBLE_COLON; }
<YYINITIAL, LONG_TEMPLATE> {COLON_SYM} { return JuliaTypes.COLON_SYM; }
<YYINITIAL, LONG_TEMPLATE> {COLON_ASSIGN_SYM} { return JuliaTypes.COLON_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {SEMICOLON_SYM} { return JuliaTypes.SEMICOLON_SYM; }
<YYINITIAL, LONG_TEMPLATE> {COMMA_SYM} { return JuliaTypes.COMMA_SYM; }
<YYINITIAL, LONG_TEMPLATE> {QUESTION_SYM} { return JuliaTypes.QUESTION_SYM; }
<YYINITIAL, LONG_TEMPLATE> {EQ_SYM} { return JuliaTypes.EQ_SYM; }
<YYINITIAL, LONG_TEMPLATE> {AT_SYM} { return JuliaTypes.AT_SYM; }
<YYINITIAL, LONG_TEMPLATE> {SUBTYPE_SYM} { return JuliaTypes.SUBTYPE_SYM; }
<YYINITIAL, LONG_TEMPLATE> {INTERPOLATE_SYM} { return JuliaTypes.INTERPOLATE_SYM; }
<YYINITIAL, LONG_TEMPLATE> {LAMBDA_ABSTRACTION} { return JuliaTypes.LAMBDA_ABSTRACTION; }
<YYINITIAL, LONG_TEMPLATE> {ARROW_SYM} { return JuliaTypes.ARROW_SYM; }
<YYINITIAL, LONG_TEMPLATE> {SLICE_SYM} { return JuliaTypes.SLICE_SYM; }
<YYINITIAL, LONG_TEMPLATE> {AND_SYM} { return JuliaTypes.AND_SYM; }
<YYINITIAL, LONG_TEMPLATE> {OR_SYM} { return JuliaTypes.OR_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {INVERSE_DIV_ASSIGN_SYM} { return JuliaTypes.INVERSE_DIV_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {INVERSE_DIV_SYM} { return JuliaTypes.INVERSE_DIV_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {NOT_SYM} { return JuliaTypes.NOT_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {IS_SYM} { return JuliaTypes.IS_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {ISNT_SYM} { return JuliaTypes.ISNT_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {PIPE_SYM} { return JuliaTypes.PIPE_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {INVRESE_PIPE_SYM} { return JuliaTypes.INVERSE_PIPE_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {REMAINDER_SYM} { return JuliaTypes.REMAINDER_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {REMAINDER_ASSIGN_SYM} { return JuliaTypes.REMAINDER_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {SHL_ASSIGN_SYM} { return JuliaTypes.SHL_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {SHR_ASSIGN_SYM} { return JuliaTypes.SHR_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {USHR_ASSIGN_SYM} { return JuliaTypes.USHR_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {SHL_SYM} { return JuliaTypes.SHL_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {SHR_SYM} { return JuliaTypes.SHR_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {USHR_SYM} { return JuliaTypes.USHR_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {FRACTION_SYM} { return JuliaTypes.FRACTION_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {DIVIDE_SYM} { return JuliaTypes.DIVIDE_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {DIVIDE_ASSIGN_SYM} { return JuliaTypes.DIVIDE_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {EXPONENT_ASSIGN_SYM} { return JuliaTypes.EXPONENT_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {FRACTION_ASSIGN_SYM} { return JuliaTypes.FRACTION_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {MULTIPLY_ASSIGN_SYM} { return JuliaTypes.MULTIPLY_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {REMAINDER_ASSIGN_SYM} { return JuliaTypes.REMAINDER_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {EXPONENT_SYM} { return JuliaTypes.EXPONENT_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {MINUS_SYM} { return JuliaTypes.MINUS_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {MULTIPLY_SYM} { return JuliaTypes.MULTIPLY_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {PLUS_SYM} { return JuliaTypes.PLUS_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {MINUS_ASSIGN_SYM} { return JuliaTypes.MINUS_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {PLUS_ASSIGN_SYM} { return JuliaTypes.PLUS_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {EQUALS_SYM} { return JuliaTypes.EQUALS_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {UNEQUAL_SYM} { return JuliaTypes.UNEQUAL_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {GREATER_THAN_SYM} { return JuliaTypes.GREATER_THAN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {LESS_THAN_SYM} { return JuliaTypes.LESS_THAN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {GREATER_THAN_OR_EQUAL_SYM} { return JuliaTypes.GREATER_THAN_OR_EQUAL_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {LESS_THAN_OR_EQUAL_SYM} { return JuliaTypes.LESS_THAN_OR_EQUAL_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {TRANSPOSE_SYM} { return JuliaTypes.TRANSPOSE_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {IN_SYM} { return JuliaTypes.IN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {MISC_COMPARISON_SYM} { return JuliaTypes.MISC_COMPARISON_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {MISC_PLUS_SYM} { return JuliaTypes.MISC_PLUS_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {MISC_MULTIPLY_SYM} { return JuliaTypes.MISC_MULTIPLY_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {MISC_EXPONENT_SYM} { return JuliaTypes.MISC_EXPONENT_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {FACTORISE_SYM} { return JuliaTypes.FACTORISE_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {BITWISE_AND_SYM} { return JuliaTypes.BITWISE_AND_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {BITWISE_OR_SYM} { return JuliaTypes.BITWISE_OR_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {BITWISE_XOR_SYM} { return JuliaTypes.BITWISE_XOR_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {BITWISE_AND_ASSIGN_SYM} { return JuliaTypes.BITWISE_AND_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {BITWISE_OR_ASSIGN_SYM} { return JuliaTypes.BITWISE_OR_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {BITWISE_XOR_ASSIGN_SYM} { return JuliaTypes.BITWISE_XOR_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {FACTORISE_ASSIGN_SYM} { return JuliaTypes.FACTORISE_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {MISC_ARROW_SYM} { return JuliaTypes.MISC_ARROW_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM}? {EQ_SYM} { return JuliaTypes.ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {DOT_SYM} { return JuliaTypes.DOT_SYM; }
<YYINITIAL, LONG_TEMPLATE> {SPECIAL_ARROW_SYM} { return JuliaTypes.SPECIAL_ARROW_SYM; }
<YYINITIAL, LONG_TEMPLATE> {BITWISE_NOT_SYM} { return JuliaTypes.BITWISE_NOT_SYM; }

<YYINITIAL, LONG_TEMPLATE> "end" { return JuliaTypes.END_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "break" { return JuliaTypes.BREAK_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "continue" { return JuliaTypes.CONTINUE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "true" { return JuliaTypes.TRUE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "false" { return JuliaTypes.FALSE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "module" { return JuliaTypes.MODULE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "baremodule" { return JuliaTypes.BAREMODULE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "include" { return JuliaTypes.INCLUDE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "export" { return JuliaTypes.EXPORT_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "if" { return JuliaTypes.IF_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "in" { return JuliaTypes.IN_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "import" { return JuliaTypes.IMPORT_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "using" { return JuliaTypes.USING_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "elseif" { return JuliaTypes.ELSEIF_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "else" { return JuliaTypes.ELSE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "for" { return JuliaTypes.FOR_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "while" { return JuliaTypes.WHILE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "return" { return JuliaTypes.RETURN_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "try" { return JuliaTypes.TRY_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "catch" { return JuliaTypes.CATCH_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "finally" { return JuliaTypes.FINALLY_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "function" { return JuliaTypes.FUNCTION_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "type" { return JuliaTypes.TYPE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "abstract" { return JuliaTypes.ABSTRACT_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "primitive" { return JuliaTypes.PRIMITIVE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "struct" { return JuliaTypes.STRUCT_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "typealias" { return JuliaTypes.TYPEALIAS_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "immutable" { return JuliaTypes.IMMUTABLE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "mutable" { return JuliaTypes.MUTABLE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "union" { return JuliaTypes.UNION_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "quote" { return JuliaTypes.QUOTE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "begin" { return JuliaTypes.BEGIN_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "macro" { return JuliaTypes.MACRO_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "local" { return JuliaTypes.LOCAL_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "const" { return JuliaTypes.CONST_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> "let" { return JuliaTypes.LET_KEYWORD; }

<YYINITIAL, LONG_TEMPLATE> {INTEGER} { return JuliaTypes.INT_LITERAL; }
<YYINITIAL, LONG_TEMPLATE> {FLOAT} { return JuliaTypes.FLOAT_LITERAL; }
<YYINITIAL, LONG_TEMPLATE> {FLOAT_CONSTANT} { return JuliaTypes.FLOAT_CONSTANT; }
<YYINITIAL, LONG_TEMPLATE> {SYMBOL} { return JuliaTypes.SYM; }

{OTHERWISE} {
  System.out.println("Slaves get your ass back here");
  return TokenType.BAD_CHARACTER;
}
