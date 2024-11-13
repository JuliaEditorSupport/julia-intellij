package org.ice1000.julia.lang;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import com.intellij.util.containers.*;
import org.ice1000.julia.lang.psi.JuliaTypes;

%%

%{
  private static final IntStack stateStack = new IntStack();
  private static final IntStack leftBracketStack = new IntStack();
  private static int leftBraceCount = 0;
  private static boolean noInAndUnion = false;

  /** 虎哥化 */
  private void hugify(int state) {
    stateStack.push(yystate());
    leftBracketStack.push(leftBraceCount);
    leftBraceCount = 0;
    yybegin(state);
  }

  /** 去虎哥化 */
  private void dehugify() {
    leftBraceCount = leftBracketStack.pop();
    yybegin(stateStack.pop());
  }

  /** 重新虎哥化 */
  private void rehugify(int state) {
    dehugify();
    hugify(state);
  }

  private static void init() {
    leftBraceCount = 0;
    noInAndUnion = false;
    stateStack.clear();
    leftBracketStack.clear();
  }

  public JuliaLexer() {
    this((java.io.Reader) null);
    init();
  }
%}

%class JuliaLexer
%implements FlexLexer
%unicode

%function advance
%type IElementType
%eof{
  init();
%eof}

%state NESTED_COMMENT
%state STRING_TEMPLATE
%state RAW_STRING_TEMPLATE
%state SHORT_TEMPLATE
%state LONG_TEMPLATE
%state CMD_STRING_TEMPLATE
%state INSIDE_REGEX
%state INSIDE_RAW_STR
%state INSIDE_BYTE_ARRAY
%state INSIDE_VERSION
%state AFTER_SIMPLE_LIT
%state AFTER_COLON

MISC_COMPARISON_SYM=[∉∋∌⊆⊈⊂⊄⊊∝∊∍∥∦∷∺∻∽∾≁≃≄≅≆≇≈≉≊≋≌≍≎≐≑≒≓≔≕≖≗≘≙≚≛≜≝≞≟≣≦≧≨≩≪≫≬≭≮≯≰≱≲≳≴≵≶≷≸≹≺≻≼≽≾≿⊀⊁⊃⊅⊇⊉⊋⊏⊐⊑⊒⊜⊩⊬⊮⊰⊱⊲⊳⊴⊵⊶⊷⋍⋐⋑⋕⋖⋗⋘⋙⋚⋛⋜⋝⋞⋟⋠⋡⋢⋣⋤⋥⋦⋧⋨⋩⋪⋫⋬⋭⋲⋳⋴⋵⋶⋷⋸⋹⋺⋻⋼⋽⋾⋿⟈⟉⟒⦷⧀⧁⧡⧣⧤⧥⩦⩧⩪⩫⩬⩭⩮⩯⩰⩱⩲⩳⩴⩵⩶⩷⩸⩹⩺⩻⩼⩽⩾⩿⪀⪁⪂⪃⪄⪅⪆⪇⪈⪉⪊⪋⪌⪍⪎⪏⪐⪑⪒⪓⪔⪕⪖⪗⪘⪙⪚⪛⪜⪝⪞⪟⪠⪡⪢⪣⪤⪥⪦⪧⪨⪩⪪⪫⪬⪭⪮⪯⪰⪱⪲⪳⪴⪵⪶⪷⪸⪹⪺⪻⪼⪽⪾⪿⫀⫁⫂⫃⫄⫅⫆⫇⫈⫉⫊⫋⫌⫍⫎⫏⫐⫑⫒⫓⫔⫕⫖⫗⫘⫙⫷⫸⫹⫺⊢⊣⟂]
MISC_PLUS_SYM=[⊕⊖⊞⊟++∪∨⊔±∓∔∸≂≏⊎⊽⋎⋓⧺⧻⨈⨢⨣⨤⨥⨦⨧⨨⨩⨪⨫⨬⨭⨮⨹⨺⩁⩂⩅⩊⩌⩏⩐⩒⩔⩖⩗⩛⩝⩡⩢⩣]
// temporarily removed ⋅ and ×
MISC_MULTIPLY_SYM=[∘∩∧⊗⊘⊙⊚⊛⊠⊡⊓∗∙∤⅋≀⊼⋄⋆⋇⋉⋊⋋⋌⋏⋒⟑⦸⦼⦾⦿⧶⧷⨇⨰⨱⨲⨳⨴⨵⨶⨷⨸⨻⨼⨽⩀⩃⩄⩋⩍⩎⩑⩓⩕⩘⩚⩜⩞⩟⩠⫛⊍▷⨝⟕⟖⟗]
MISC_EXPONENT_SYM=[↑↓⇵⟰⟱⤈⤉⤊⤋⤒⤓⥉⥌⥍⥏⥑⥔⥕⥘⥙⥜⥝⥠⥡⥣⥥⥮⥯￪￬]
MISC_ARROW_SYM=[←→↔↚↛↞↠↢↣↦↤↮⇎⇍⇏⇐⇒⇔⇴⇶⇷⇸⇹⇺⇻⇼⇽⇾⇿⟵⟶⟷⟹⟺⟻⟼⟽⟾⟿⤀⤁⤂⤃⤄⤅⤆⤇⤌⤍⤎⤏⤐⤑⤔⤕⤖⤗⤘⤝⤞⤟⤠⥄⥅⥆⥇⥈⥊⥋⥎⥐⥒⥓⥖⥗⥚⥛⥞⥟⥢⥤⥦⥧⥨⥩⥪⥫⥬⥭⥰⧴⬱⬰⬲⬳⬴⬵⬶⬷⬸⬹⬺⬻⬼⬽⬾⬿⭀⭁⭂⭃⭄⭇⭈⭉⭊⭋⭌￩￫⇜⇝↜↝↩↪↫↬↼↽⇀⇁⇄⇆⇇⇉⇋⇌⇚⇛⇠⇢]

WHITE_SPACE=[ \r\t\f]

LINE_COMMENT=#(\n|[^\n=][^\n]*)
BLOCK_COMMENT_CONTENT=([^#=]|(=[^#])|(#[^=]))

TRIPLE_QUOTE_SYM=\"\"\"

SHORT_TEMPLATE=\${SIMPLE_SYMBOL}
LONG_TEMPLATE_START=\$\(

//SYMBOL=[a-zA-Z_]([a-zA-Z\d_\!])+
//SYMBOL=[^\x00-\x20+\-*/\\$#\{\}()\[\]<>|&?~;\"\'\`@]+
SIMPLE_SYMBOL={VALID_CHAR}({VALID_CHAR}|[\d\!])*
VALID_CHAR=[a-zA-Z_\U0000A0-\U10ffff]

STRING_UNICODE=\\((u{HEXDIGIT}{4})|(x{HEXDIGIT}{2}))
CHAR_LITERAL='([^\\\'\x00-\x1F\x7F]|[a-zA-Z_\u0000A0-\u10ffff])+'
STRING_ESCAPE=\\[^]

DIGIT=[\d_]
NUM_PART=\d({DIGIT}*\d)?

NUM_SUFFIX=-?{DIGIT}+
P_SUFFIX=[pP]{NUM_SUFFIX}
E_SUFFIX=[eE]{NUM_SUFFIX}
F_SUFFIX=[fF]{NUM_SUFFIX}
HEXDIGIT=[a-fA-F0-9]
HEX_NUM=0[xX]{HEXDIGIT}+({P_SUFFIX}|{E_SUFFIX}|{F_SUFFIX})?
OCT_NUM=0[oO][0-7]+
BIN_NUM=0[bB][01]+
DEC_NUM={NUM_PART}
INTEGER={HEX_NUM}|{OCT_NUM}|{BIN_NUM}|{DEC_NUM}
FLOAT=({NUM_PART}|({NUM_PART}+\.{NUM_PART}*)|({NUM_PART}*\.{NUM_PART}+))({E_SUFFIX}|{F_SUFFIX})?|({HEX_NUM}+\.{HEXDIGIT}*p{DEC_NUM})

OTHERWISE=[^]

%%

<YYINITIAL, LONG_TEMPLATE> \[ { return JuliaTypes.LEFT_M_BRACKET; }
<YYINITIAL, LONG_TEMPLATE> \] { return JuliaTypes.RIGHT_M_BRACKET; }
<YYINITIAL, LONG_TEMPLATE> \{ { return JuliaTypes.LEFT_B_BRACKET; }
<YYINITIAL, LONG_TEMPLATE> \} { return JuliaTypes.RIGHT_B_BRACKET; }
<YYINITIAL> \( { return JuliaTypes.LEFT_BRACKET; }
<YYINITIAL> \) {
  hugify(AFTER_SIMPLE_LIT);
  return JuliaTypes.RIGHT_BRACKET;
}

<LONG_TEMPLATE> \( { leftBraceCount++; return JuliaTypes.LEFT_BRACKET; }
<LONG_TEMPLATE> \) {
  if (leftBraceCount == 0) {
    dehugify();
    return JuliaTypes.STRING_INTERPOLATE_END;
  }
  leftBraceCount--;
  return JuliaTypes.RIGHT_BRACKET;
}

<YYINITIAL, LONG_TEMPLATE> \n+ { noInAndUnion = false; return JuliaTypes.EOL; }
<YYINITIAL, LONG_TEMPLATE> {WHITE_SPACE} { return TokenType.WHITE_SPACE; }

<YYINITIAL, LONG_TEMPLATE> {LINE_COMMENT} { return JuliaTypes.LINE_COMMENT; }
<YYINITIAL, LONG_TEMPLATE, AFTER_SIMPLE_LIT, NESTED_COMMENT> #= {
  hugify(NESTED_COMMENT);
  return JuliaTypes.BLOCK_COMMENT_START;
}

<NESTED_COMMENT> {BLOCK_COMMENT_CONTENT}+ { return JuliaTypes.BLOCK_COMMENT_BODY; }
<NESTED_COMMENT> =# {
  dehugify();
  return JuliaTypes.BLOCK_COMMENT_END;
}

<YYINITIAL, LONG_TEMPLATE> @{SIMPLE_SYMBOL} { return JuliaTypes.MACRO_SYM; }

<YYINITIAL, LONG_TEMPLATE> r\" { hugify(INSIDE_REGEX); return JuliaTypes.REGEX_START; }
<YYINITIAL, LONG_TEMPLATE> raw\" { hugify(INSIDE_RAW_STR); return JuliaTypes.RAW_STR_START; }
<YYINITIAL, LONG_TEMPLATE> v\" { hugify(INSIDE_VERSION); return JuliaTypes.VERSION_START; }
<YYINITIAL, LONG_TEMPLATE> b\" { hugify(INSIDE_BYTE_ARRAY); return JuliaTypes.BYTE_ARRAY_START; }
<INSIDE_VERSION,
 INSIDE_RAW_STR> [^\"]+ { return JuliaTypes.REGULAR_STRING_PART_LITERAL; }

<YYINITIAL, LONG_TEMPLATE> end { return JuliaTypes.END_KEYWORD; }

<YYINITIAL, LONG_TEMPLATE> break { return JuliaTypes.BREAK_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> where { return JuliaTypes.WHERE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> do { return JuliaTypes.DO_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> continue { return JuliaTypes.CONTINUE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> true { return JuliaTypes.TRUE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> false { return JuliaTypes.FALSE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> module { return JuliaTypes.MODULE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> baremodule { return JuliaTypes.BAREMODULE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> export { return JuliaTypes.EXPORT_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> if { return JuliaTypes.IF_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> isa { return JuliaTypes.ISA_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> in {
  return noInAndUnion ? JuliaTypes.SYM : JuliaTypes.IN_KEYWORD;
}

<YYINITIAL, LONG_TEMPLATE> importall { noInAndUnion = true; return JuliaTypes.IMPORTALL_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> import { noInAndUnion = true; return JuliaTypes.IMPORT_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> using { return JuliaTypes.USING_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> elseif { return JuliaTypes.ELSEIF_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> else { return JuliaTypes.ELSE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> for { return JuliaTypes.FOR_KEYWORD; }

<YYINITIAL, LONG_TEMPLATE> while { return JuliaTypes.WHILE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> return { return JuliaTypes.RETURN_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> try { return JuliaTypes.TRY_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> catch { return JuliaTypes.CATCH_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> finally { return JuliaTypes.FINALLY_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> function { return JuliaTypes.FUNCTION_KEYWORD; }
// <YYINITIAL, LONG_TEMPLATE> type { return JuliaTypes.TYPE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> abstract[ \n\r\t]+type { return JuliaTypes.ABSTRACT_TYPE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> abstract { return JuliaTypes.ABSTRACT_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> primitive[ \n\r\t]+type { return JuliaTypes.PRIMITIVE_TYPE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> struct { return JuliaTypes.STRUCT_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> typealias { return JuliaTypes.TYPEALIAS_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> immutable { return JuliaTypes.IMMUTABLE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> mutable { return JuliaTypes.MUTABLE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> union {
  return noInAndUnion ? JuliaTypes.SYM : JuliaTypes.UNION_KEYWORD;
}

<YYINITIAL, LONG_TEMPLATE> quote { return JuliaTypes.QUOTE_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> begin { return JuliaTypes.BEGIN_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> macro { return JuliaTypes.MACRO_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> global { return JuliaTypes.GLOBAL_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> local { return JuliaTypes.LOCAL_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> const { return JuliaTypes.CONST_KEYWORD; }
<YYINITIAL, LONG_TEMPLATE> let { return JuliaTypes.LET_KEYWORD; }

<YYINITIAL, LONG_TEMPLATE> :: { return JuliaTypes.DOUBLE_COLON; }
<YYINITIAL, LONG_TEMPLATE> : { hugify(AFTER_COLON); return JuliaTypes.COLON_SYM; }
<YYINITIAL, LONG_TEMPLATE> := { return JuliaTypes.COLON_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> ; { return JuliaTypes.SEMICOLON_SYM; }
<YYINITIAL, LONG_TEMPLATE> , { return JuliaTypes.COMMA_SYM; }
<YYINITIAL, LONG_TEMPLATE> \? { return JuliaTypes.QUESTION_SYM; }
<YYINITIAL, LONG_TEMPLATE> = { return JuliaTypes.EQ_SYM; }
<YYINITIAL, LONG_TEMPLATE> <: { return JuliaTypes.SUBTYPE_SYM; }
<YYINITIAL, LONG_TEMPLATE> >: { return JuliaTypes.SUPERTYPE_SYM; }
<YYINITIAL, LONG_TEMPLATE> \$ { return JuliaTypes.INTERPOLATE_SYM; }
<YYINITIAL, LONG_TEMPLATE> -> { return JuliaTypes.LAMBDA_ABSTRACTION; }
<YYINITIAL, LONG_TEMPLATE> => { return JuliaTypes.ARROW_SYM; }
<YYINITIAL, LONG_TEMPLATE> \.\.\. { return JuliaTypes.SLICE_SYM; }
<YYINITIAL, LONG_TEMPLATE> && { return JuliaTypes.AND_SYM; }
<YYINITIAL, LONG_TEMPLATE> \|\| { return JuliaTypes.OR_SYM; }
<YYINITIAL, LONG_TEMPLATE> -->? { return JuliaTypes.SPECIAL_ARROW_SYM; }
<YYINITIAL, LONG_TEMPLATE> \~ { return JuliaTypes.BITWISE_NOT_SYM; }
<YYINITIAL, LONG_TEMPLATE> \\\\= { return JuliaTypes.FACTORISE_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> \\\\ { return JuliaTypes.FACTORISE_SYM; }
<YYINITIAL, LONG_TEMPLATE> \\= { return JuliaTypes.INVERSE_DIV_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> \\ { return JuliaTypes.INVERSE_DIV_SYM; }
<YYINITIAL, LONG_TEMPLATE> \! { return JuliaTypes.NOT_SYM; }
<YYINITIAL, LONG_TEMPLATE> (===|≡) { return JuliaTypes.IS_SYM; }
<YYINITIAL, LONG_TEMPLATE> (\!==|≢) { return JuliaTypes.ISNT_SYM; }
<YYINITIAL, LONG_TEMPLATE> \|> { return JuliaTypes.PIPE_SYM; }
<YYINITIAL, LONG_TEMPLATE> <\| { return JuliaTypes.INVERSE_PIPE_SYM; }
<YYINITIAL, LONG_TEMPLATE> %= { return JuliaTypes.REMAINDER_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> % { return JuliaTypes.REMAINDER_SYM; }
<YYINITIAL, LONG_TEMPLATE> <<= { return JuliaTypes.SHL_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> >>= { return JuliaTypes.SHR_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> >>>= { return JuliaTypes.USHR_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> << { return JuliaTypes.SHL_SYM; }
<YYINITIAL, LONG_TEMPLATE> >> { return JuliaTypes.SHR_SYM; }
<YYINITIAL, LONG_TEMPLATE> >>> { return JuliaTypes.USHR_SYM; }
<YYINITIAL, LONG_TEMPLATE> \/\/= { return JuliaTypes.FRACTION_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> \/\/ { return JuliaTypes.FRACTION_SYM; }
<YYINITIAL, LONG_TEMPLATE> (\/=|÷=) { return JuliaTypes.DIVIDE_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> (\/|÷) { return JuliaTypes.DIVIDE_SYM; }
<YYINITIAL, LONG_TEMPLATE> \^= { return JuliaTypes.EXPONENT_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> \*= { return JuliaTypes.MULTIPLY_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> \+= { return JuliaTypes.PLUS_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> \^ { return JuliaTypes.EXPONENT_SYM; }
<YYINITIAL, LONG_TEMPLATE> \* { return JuliaTypes.MULTIPLY_SYM; }
<YYINITIAL, LONG_TEMPLATE> \+ { return JuliaTypes.PLUS_SYM; }
<YYINITIAL, LONG_TEMPLATE> -= { return JuliaTypes.MINUS_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> == { return JuliaTypes.EQUALS_SYM; }
<YYINITIAL, LONG_TEMPLATE> - { return JuliaTypes.MINUS_SYM; }
<YYINITIAL, LONG_TEMPLATE> (\!=|≠) { return JuliaTypes.UNEQUAL_SYM; }
<YYINITIAL, LONG_TEMPLATE> > { return JuliaTypes.GREATER_THAN_SYM; }
<YYINITIAL, LONG_TEMPLATE> < { return JuliaTypes.LESS_THAN_SYM; }
<YYINITIAL, LONG_TEMPLATE> (>=|≥) { return JuliaTypes.GREATER_THAN_OR_EQUAL_SYM; }
<YYINITIAL, LONG_TEMPLATE> (<=|≤) { return JuliaTypes.LESS_THAN_OR_EQUAL_SYM; }
<YYINITIAL, LONG_TEMPLATE> ' { return JuliaTypes.TRANSPOSE_SYM; }
<YYINITIAL, LONG_TEMPLATE> = { return JuliaTypes.ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> \&= { return JuliaTypes.BITWISE_AND_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> \|= { return JuliaTypes.BITWISE_OR_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> \& { return JuliaTypes.BITWISE_AND_SYM; }
<YYINITIAL, LONG_TEMPLATE> \| { return JuliaTypes.BITWISE_OR_SYM; }
<YYINITIAL, LONG_TEMPLATE> (\$=|⊻=) { return JuliaTypes.BITWISE_XOR_ASSIGN_SYM; }
<YYINITIAL, LONG_TEMPLATE> (\$|⊻) { return JuliaTypes.BITWISE_XOR_SYM; }
<YYINITIAL, LONG_TEMPLATE> ∈ { return JuliaTypes.IN_SYM; }
<YYINITIAL, LONG_TEMPLATE> {MISC_ARROW_SYM} { return JuliaTypes.MISC_ARROW_SYM; }
<YYINITIAL, LONG_TEMPLATE> {MISC_COMPARISON_SYM} { return JuliaTypes.MISC_COMPARISON_SYM; }
<YYINITIAL, LONG_TEMPLATE> {MISC_PLUS_SYM} { return JuliaTypes.MISC_PLUS_SYM; }
<YYINITIAL, LONG_TEMPLATE> {MISC_MULTIPLY_SYM} { return JuliaTypes.MISC_MULTIPLY_SYM; }
<YYINITIAL, LONG_TEMPLATE> {MISC_EXPONENT_SYM} { return JuliaTypes.MISC_EXPONENT_SYM; }
<YYINITIAL, LONG_TEMPLATE> \. { return JuliaTypes.DOT_SYM; }
<YYINITIAL, LONG_TEMPLATE> \.\. { return JuliaTypes.DOUBLE_DOT_SYM; }

<YYINITIAL, LONG_TEMPLATE> Inf16|Inf32|Inf|-Inf16|-Inf32|-Inf|NaN16|NaN32|NaN {
  // hugify(AFTER_SIMPLE_LIT);
  return JuliaTypes.FLOAT_CONSTANT;
}

<AFTER_COLON> {SIMPLE_SYMBOL} { dehugify(); return JuliaTypes.SYM; }

<INSIDE_REGEX> \" { rehugify(AFTER_SIMPLE_LIT); return JuliaTypes.REGEX_END; }
<INSIDE_RAW_STR> \" { dehugify(); return JuliaTypes.RAW_STR_END; }
<INSIDE_VERSION> \" { dehugify(); return JuliaTypes.VERSION_END; }
<INSIDE_BYTE_ARRAY> \" { dehugify(); return JuliaTypes.BYTE_ARRAY_END; }

<YYINITIAL, LONG_TEMPLATE> {SIMPLE_SYMBOL}\' { return JuliaTypes.SYM; }
<YYINITIAL, LONG_TEMPLATE> {SIMPLE_SYMBOL} { hugify(AFTER_SIMPLE_LIT); return JuliaTypes.SYM; }
<YYINITIAL, LONG_TEMPLATE> {SIMPLE_SYMBOL} \! /= {
  yypushback(1);
  hugify(AFTER_SIMPLE_LIT);
  return JuliaTypes.SYM;
}

<YYINITIAL, LONG_TEMPLATE> {INTEGER} {
  hugify(AFTER_SIMPLE_LIT);
  return JuliaTypes.INT_LITERAL;
}

<YYINITIAL, LONG_TEMPLATE> {FLOAT} {
  hugify(AFTER_SIMPLE_LIT);
  return JuliaTypes.FLOAT_LITERAL;
}

<YYINITIAL, LONG_TEMPLATE> {CHAR_LITERAL} {
  // hugify(AFTER_SIMPLE_LIT);
  return JuliaTypes.CHAR_LITERAL;
}

<AFTER_SIMPLE_LIT> {VALID_CHAR}|[\'\"] {
  dehugify();
  yypushback(1);
  return JuliaTypes.IMPLICIT_MULTIPLY_SYM;
}

<AFTER_SIMPLE_LIT, AFTER_COLON> {OTHERWISE} { dehugify(); yypushback(1); }

<YYINITIAL, LONG_TEMPLATE> \" { hugify(STRING_TEMPLATE); return JuliaTypes.QUOTE_START; }
<YYINITIAL, LONG_TEMPLATE> ` { hugify(CMD_STRING_TEMPLATE); return JuliaTypes.CMD_QUOTE_START; }
<YYINITIAL, LONG_TEMPLATE> {TRIPLE_QUOTE_SYM} { hugify(RAW_STRING_TEMPLATE); return JuliaTypes.TRIPLE_QUOTE_START; }

<STRING_TEMPLATE> \" {
  rehugify(AFTER_SIMPLE_LIT);
  return JuliaTypes.QUOTE_END;
}

<CMD_STRING_TEMPLATE> ` {
  rehugify(AFTER_SIMPLE_LIT);
  return JuliaTypes.CMD_QUOTE_END;
}

<RAW_STRING_TEMPLATE> {TRIPLE_QUOTE_SYM} {
  rehugify(AFTER_SIMPLE_LIT);
  return JuliaTypes.TRIPLE_QUOTE_END;
}

<STRING_TEMPLATE> [^\\\"\$]+ { return JuliaTypes.REGULAR_STRING_PART_LITERAL; }
<INSIDE_REGEX> [^\\\"]+ { return JuliaTypes.REGULAR_STRING_PART_LITERAL; }
<CMD_STRING_TEMPLATE> [^\\`\$]+ { return JuliaTypes.REGULAR_STRING_PART_LITERAL; }
<RAW_STRING_TEMPLATE> ([^\\\"\$]|(\"[^\"])|(\"\"[^\"]))+ { return JuliaTypes.REGULAR_STRING_PART_LITERAL; }

<STRING_TEMPLATE,
 INSIDE_REGEX,
 CMD_STRING_TEMPLATE,
 RAW_STRING_TEMPLATE,
 INSIDE_BYTE_ARRAY> {STRING_UNICODE} { return JuliaTypes.STRING_UNICODE; }
<STRING_TEMPLATE,
 INSIDE_REGEX,
 CMD_STRING_TEMPLATE,
 RAW_STRING_TEMPLATE,
 INSIDE_BYTE_ARRAY> {STRING_ESCAPE} { return JuliaTypes.STRING_ESCAPE; }
// byte arrays can have unicode/escapes
<INSIDE_BYTE_ARRAY> [^\"\\]+ { return JuliaTypes.REGULAR_STRING_PART_LITERAL; }
<STRING_TEMPLATE,
 CMD_STRING_TEMPLATE,
 RAW_STRING_TEMPLATE> \$ { return JuliaTypes.SHORT_INTERPOLATE_SYM; }
<STRING_TEMPLATE,
 CMD_STRING_TEMPLATE,
 RAW_STRING_TEMPLATE> {SHORT_TEMPLATE} {
  hugify(SHORT_TEMPLATE);
  yypushback(yylength() - 1);
  return JuliaTypes.SHORT_INTERPOLATE_SYM;
}

<STRING_TEMPLATE, CMD_STRING_TEMPLATE, RAW_STRING_TEMPLATE> {LONG_TEMPLATE_START} {
  hugify(LONG_TEMPLATE);
  return JuliaTypes.STRING_INTERPOLATE_START;
}

{LONG_TEMPLATE_START} {return JuliaTypes.EXPR_INTERPOLATE_START;}

<SHORT_TEMPLATE> {SIMPLE_SYMBOL} { dehugify(); return JuliaTypes.SYM; }

{OTHERWISE} { return TokenType.BAD_CHARACTER; }
