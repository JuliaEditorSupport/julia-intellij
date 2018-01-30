package org.ice1000.julia.lang;

import java.util.*;
import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import com.intellij.util.containers.IntStack;
import org.ice1000.julia.lang.psi.JuliaTypes;

%%

%{
  private static final class State {
    final int lBraceCount;
    final int state;

    public State(int state, int lBraceCount) {
      this.state = state;
      this.lBraceCount = lBraceCount;
    }

    @Override
    public String toString() {
      return "yystate = " + state + (lBraceCount == 0 ? "" : "lBraceCount = " + lBraceCount);
    }
  }

  protected final Stack<State> myStateStack = new Stack<State>();
  protected int myLeftBraceCount;

  private void pushState(int state) {
    myStateStack.push(new State(yystate(), myLeftBraceCount));
    myLeftBraceCount = 0;
    yybegin(state);
  }

  private void popState() {
    State state = myStateStack.pop();
    myLeftBraceCount = state.lBraceCount;
    yybegin(state.state);
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
  myLeftBraceCount = 0;
  myStateStack.clear();
%eof}

%xstate NESTED_COMMENT STRING_TEMPLATE RAW_STRING_TEMPLATE SHORT_TEMPLATE_ENTRY LONG_TEMPLATE

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

DIGIT=[0-9]
HEX_DIGIT=[0-9a-fA-F]
LETTER=[a-z]|[A-Z]
WHITE_SPACE=[ \n\t\f]+
PROGRAM_COMMENT="#""!"[^\n]*
SINGLE_LINE_COMMENT="/""/"[^\n]*

MULTI_LINE_DEGENERATE_COMMENT = "/*" "*"+ "/"
MULTI_LINE_COMMENT_START      = "/*"
MULTI_LINE_DOC_COMMENT_START  = "/**"
MULTI_LINE_COMMENT_END        = "*/"

RAW_SINGLE_QUOTED_STRING= "r" ((\" ([^\"\n])* \"?) | ("'" ([^\'\n])* \'?))
RAW_TRIPLE_QUOTED_STRING= "r" ({RAW_TRIPLE_QUOTED_LITERAL} | {RAW_TRIPLE_APOS_LITERAL})

RAW_TRIPLE_QUOTED_LITERAL = {THREE_QUO}  ([^\"] | \"[^\"] | \"\"[^\"])* {THREE_QUO}?
RAW_TRIPLE_APOS_LITERAL   = {THREE_APOS} ([^\'] | \'[^\'] | \'\'[^\'])* {THREE_APOS}?

THREE_QUO =  (\"\"\")
THREE_APOS = (\'\'\')

SHORT_TEMPLATE_ENTRY=\${IDENTIFIER_NO_DOLLAR}
LONG_TEMPLATE_ENTRY_START=\$\{

IDENTIFIER_START_NO_DOLLAR={LETTER}|"_"
IDENTIFIER_START={IDENTIFIER_START_NO_DOLLAR}|"$"
IDENTIFIER_PART_NO_DOLLAR={IDENTIFIER_START_NO_DOLLAR}|{DIGIT}
IDENTIFIER_PART={IDENTIFIER_START}|{DIGIT}
IDENTIFIER={IDENTIFIER_START}{IDENTIFIER_PART}*
IDENTIFIER_NO_DOLLAR={IDENTIFIER_START_NO_DOLLAR}{IDENTIFIER_PART_NO_DOLLAR}*

NUMERIC_LITERAL = {NUMBER} | {HEX_NUMBER}
NUMBER = ({DIGIT}+ ("." {DIGIT}+)? {EXPONENT}?) | ("." {DIGIT}+ {EXPONENT}?)
EXPONENT = [Ee] ["+""-"]? {DIGIT}*
HEX_NUMBER = 0 [Xx] {HEX_DIGIT}*

%%

<YYINITIAL> "{" { return LBRACE; }
<YYINITIAL> "}" { return RBRACE; }
<LONG_TEMPLATE> "{" { myLeftBraceCount++; return LBRACE; }
<LONG_TEMPLATE> "}" {
  if (myLeftBraceCount == 0) {
    popState();
    return LONG_TEMPLATE_ENTRY_END;
  }
  myLeftBraceCount--;
  return RBRACE;
}

<YYINITIAL, LONG_TEMPLATE> {WHITE_SPACE} { return WHITE_SPACE; }

// single-line comments
<YYINITIAL, LONG_TEMPLATE> {SINGLE_LINE_COMMENT} { return SINGLE_LINE_COMMENT; }
<YYINITIAL> {PROGRAM_COMMENT} { return SINGLE_LINE_COMMENT; }

// multi-line comments
<YYINITIAL, LONG_TEMPLATE> {MULTI_LINE_DEGENERATE_COMMENT} { return MULTI_LINE_COMMENT;      }

// next rules return temporary IElementType's that are rplaced with DartTokenTypesSets#MULTI_LINE_COMMENT or DartTokenTypesSets#MULTI_LINE_DOC_COMMENT in com.jetbrains.lang.dart.lexer.DartLexer
<YYINITIAL, LONG_TEMPLATE> {MULTI_LINE_DOC_COMMENT_START}  { pushState(MULTI_LINE_COMMENT_STATE); return MULTI_LINE_DOC_COMMENT_START; }
<YYINITIAL, LONG_TEMPLATE> {MULTI_LINE_COMMENT_START}      { pushState(MULTI_LINE_COMMENT_STATE); return MULTI_LINE_COMMENT_START; }

<NESTED_COMMENT> {MULTI_LINE_COMMENT_START} {
  pushState(MULTI_LINE_COMMENT_STATE);
  return MULTI_LINE_COMMENT_BODY;
}

<NESTED_COMMENT> [^] { return MULTI_LINE_COMMENT_BODY; }
<NESTED_COMMENT> {MULTI_LINE_COMMENT_END} {
  popState();
  return yystate() == MULTI_LINE_COMMENT_STATE
      ? MULTI_LINE_COMMENT_BODY // inner comment closed
      : MULTI_LINE_COMMENT_END;
}

// reserved words

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

<YYINITIAL, LONG_TEMPLATE> {IDENTIFIER}           { return IDENTIFIER; }
<YYINITIAL, LONG_TEMPLATE> "["                { return LBRACKET; }
<YYINITIAL, LONG_TEMPLATE> "]"                { return RBRACKET; }
<YYINITIAL, LONG_TEMPLATE> "("                { return LPAREN; }
<YYINITIAL, LONG_TEMPLATE> ")"                { return RPAREN; }
<YYINITIAL, LONG_TEMPLATE> ";"                { return SEMICOLON; }
<YYINITIAL, LONG_TEMPLATE> "-"                { return MINUS; }
<YYINITIAL, LONG_TEMPLATE> "-="               { return MINUS_EQ; }
<YYINITIAL, LONG_TEMPLATE> "--"               { return MINUS_MINUS; }
<YYINITIAL, LONG_TEMPLATE> "+"                { return PLUS; }
<YYINITIAL, LONG_TEMPLATE> "++"               { return PLUS_PLUS; }
<YYINITIAL, LONG_TEMPLATE> "+="               { return PLUS_EQ; }
<YYINITIAL, LONG_TEMPLATE> "/"                { return DIV; }
<YYINITIAL, LONG_TEMPLATE> "/="               { return DIV_EQ; }
<YYINITIAL, LONG_TEMPLATE> "*"                { return MUL; }
<YYINITIAL, LONG_TEMPLATE> "*="               { return MUL_EQ; }
<YYINITIAL, LONG_TEMPLATE> "~/"               { return INT_DIV; }
<YYINITIAL, LONG_TEMPLATE> "~/="              { return INT_DIV_EQ; }
<YYINITIAL, LONG_TEMPLATE> "%="               { return REM_EQ; }
<YYINITIAL, LONG_TEMPLATE> "%"                { return REM; }
<YYINITIAL, LONG_TEMPLATE> "~"                { return BIN_NOT; }
<YYINITIAL, LONG_TEMPLATE> "!"                { return NOT; }

<YYINITIAL, LONG_TEMPLATE> "=>"  { return EXPRESSION_BODY_DEF; }
<YYINITIAL, LONG_TEMPLATE> "="   { return EQ; }
<YYINITIAL, LONG_TEMPLATE> "=="  { return EQ_EQ; }
<YYINITIAL, LONG_TEMPLATE> "!="  { return NEQ; }
<YYINITIAL, LONG_TEMPLATE> "."   { return DOT; }
<YYINITIAL, LONG_TEMPLATE> ".."  { return DOT_DOT; }
<YYINITIAL, LONG_TEMPLATE> ","   { return COMMA; }
<YYINITIAL, LONG_TEMPLATE> ":"   { return COLON; }
<YYINITIAL, LONG_TEMPLATE> ">"   { return GT; }
<YYINITIAL, LONG_TEMPLATE> "<"   { return LT; }
<YYINITIAL, LONG_TEMPLATE> "<="  { return LT_EQ; }
<YYINITIAL, LONG_TEMPLATE> "<<"  { return LT_LT; }
<YYINITIAL, LONG_TEMPLATE> "<<=" { return LT_LT_EQ; }
<YYINITIAL, LONG_TEMPLATE> "?"   { return QUEST; }
<YYINITIAL, LONG_TEMPLATE> "?."  { return QUEST_DOT; }
<YYINITIAL, LONG_TEMPLATE> "??"  { return QUEST_QUEST; }
<YYINITIAL, LONG_TEMPLATE> "??=" { return QUEST_QUEST_EQ; }
<YYINITIAL, LONG_TEMPLATE> "|"   { return OR; }
<YYINITIAL, LONG_TEMPLATE> "|="  { return OR_EQ; }
<YYINITIAL, LONG_TEMPLATE> "||"  { return OR_OR; }
<YYINITIAL, LONG_TEMPLATE> "||=" { return OR_OR_EQ; }
<YYINITIAL, LONG_TEMPLATE> "^"   { return XOR; }
<YYINITIAL, LONG_TEMPLATE> "^="  { return XOR_EQ; }
<YYINITIAL, LONG_TEMPLATE> "&"   { return AND; }
<YYINITIAL, LONG_TEMPLATE> "&="  { return AND_EQ; }
<YYINITIAL, LONG_TEMPLATE> "&&"  { return AND_AND; }
<YYINITIAL, LONG_TEMPLATE> "&&=" { return AND_AND_EQ; }
<YYINITIAL, LONG_TEMPLATE> "@"   { return AT; }
<YYINITIAL, LONG_TEMPLATE> "#"   { return HASH; }

<YYINITIAL, LONG_TEMPLATE> {NUMERIC_LITERAL} { return NUMBER; }

// raw strings
<YYINITIAL, LONG_TEMPLATE> {RAW_TRIPLE_QUOTED_STRING} { return RAW_TRIPLE_QUOTED_STRING; }
<YYINITIAL, LONG_TEMPLATE> {RAW_SINGLE_QUOTED_STRING} { return RAW_SINGLE_QUOTED_STRING; }

// string start
<YYINITIAL, LONG_TEMPLATE> \"           { pushState(QUO_STRING);        return OPEN_QUOTE;    }
<YYINITIAL, LONG_TEMPLATE> \'           { pushState(APOS_STRING);       return OPEN_QUOTE;    }
<YYINITIAL, LONG_TEMPLATE> {THREE_QUO}  { pushState(THREE_QUO_STRING);  return OPEN_QUOTE;    }
<YYINITIAL, LONG_TEMPLATE> {THREE_APOS} { pushState(THREE_APOS_STRING); return OPEN_QUOTE;    }
// correct string end
<STRING_TEMPLATE> \" { popState(); return CLOSING_QUOTE; }
<RAW_STRING_TEMPLATE> {THREE_QUO} { popState(); return CLOSING_QUOTE; }
// string content
<STRING_TEMPLATE> ([^\\\"\n\$] | (\\ [^\n]))*   { return REGULAR_STRING_PART; }
<RAW_STRING_TEMPLATE> ([^\\\"\$])* { return REGULAR_STRING_PART; }
<RAW_STRING_TEMPLATE> (\"[^\"]) | (\"\"[^\"]) { yypushback(1); return REGULAR_STRING_PART; } // pushback because we could capture '\' that escapes something
<RAW_STRING_TEMPLATE> (\\[^]) { return REGULAR_STRING_PART; } // escape sequence

// bad string interpolation (no identifier after '$')
<STRING_TEMPLATE, RAW_STRING_TEMPLATE> \$   { return SHORT_TEMPLATE_ENTRY_START; }
// short string interpolation
<STRING_TEMPLATE, RAW_STRING_TEMPLATE> {SHORT_TEMPLATE_ENTRY} {
  pushState(SHORT_TEMPLATE_ENTRY);
  yypushback(yylength() - 1);
  return SHORT_TEMPLATE_ENTRY_START;
}

// long string interpolation
<STRING_TEMPLATE, RAW_STRING_TEMPLATE> {LONG_TEMPLATE_ENTRY_START} {
  pushState(LONG_TEMPLATE_ENTRY);
  return LONG_TEMPLATE_ENTRY_START;
}

<SHORT_TEMPLATE_ENTRY> {IDENTIFIER_NO_DOLLAR}    { popState(); return IDENTIFIER; }

[^] { return BAD_CHARACTER; }