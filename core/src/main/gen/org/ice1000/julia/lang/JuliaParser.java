// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static org.ice1000.julia.lang.psi.JuliaTypes.*;
import static org.ice1000.julia.lang.parsing.JuliaGeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class JuliaParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType type, PsiBuilder builder) {
    parseLight(type, builder);
    return builder.getTreeBuilt();
  }

  public void parseLight(IElementType type, PsiBuilder builder) {
    boolean result;
    builder = adapt_builder_(type, builder, this, EXTENDS_SETS_);
    Marker marker = enter_section_(builder, 0, _COLLAPSE_, null);
    result = parse_root_(type, builder);
    exit_section_(builder, 0, marker, type, result, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType type, PsiBuilder builder) {
    return parse_root_(type, builder, 0);
  }

  static boolean parse_root_(IElementType type, PsiBuilder builder, int level) {
    return juliaFile(builder, level + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(ASSIGN_LEVEL_OPERATOR_INDEXING, BRACKETED_EXPR_INDEXING, DOT_APPLY_FUNCTION_OP_INDEXING, END,
      EXPR_OR_END, EXPR_WRAPPER, MULTIPLY_INDEXING, PLUS_INDEXING,
      QUOTE_INDEXING, RANGE_INDEXING, SPLICE_INDEXING, TERNARY_OP_INDEXING),
    create_token_set_(AND_OP, APPLY_FUNCTION_OP, APPLY_INDEX_OP, APPLY_MACRO_OP,
      ARRAY, ARROW_OP, ASSIGN_LEVEL_OP, ASSIGN_OP,
      BEGIN_BLOCK, BITWISE_LEVEL_OP, BIT_WISE_NOT_OP, BOOLEAN_LIT,
      BRACKETED_COMPREHENSION_EXPR, BRACKETED_EXPR, BREAK_EXPR, BYTE_ARRAY,
      CHAR_LIT, COLON_BLOCK, COMMAND, COMPACT_FUNCTION,
      COMPARISON_LEVEL_OP, COMPOUND_QUOTE_OP, CONTINUE_EXPR, DOT_APPLY_FUNCTION_OP,
      DO_BLOCK, EXPONENT_OP, EXPORT, EXPR,
      EXPR_INTERPOLATE_OP, FLOAT_LIT, FOR_COMPREHENSION, FOR_EXPR,
      FRACTION_OP, FUNCTION, GLOBAL_STATEMENT, IF_EXPR,
      IMPLICIT_MULTIPLY_OP, IMPORT_ALL_EXPR, IMPORT_EXPR, INTEGER,
      IN_OP, ISA_OP, LAMBDA, LET,
      MACRO, MACRO_SYMBOL, MEMBER_ACCESS_OP, MISC_ARROWS_OP,
      MISC_EXPONENT_OP, MULTIPLY_LEVEL_OP, MULTI_ASSIGN_OP, NOT_OP,
      OP_AS_SYMBOL, OR_OP, PIPE_LEVEL_OP, PLUS_LEVEL_OP,
      PRIMITIVE_TYPE_DECLARATION, QUOTE_OP, RANGE_OP, RAW_STRING,
      REGEX, RETURN_EXPR, SPLICE_OP, STRING,
      STRING_LIKE_MULTIPLY_OP, SYMBOL, SYMBOL_LHS, TERNARY_OP,
      TRANSPOSE_OP, TRY_CATCH, TUPLE, TYPE,
      TYPE_ALIAS, TYPE_OP, UNARY_INTERPOLATE_OP, UNARY_MINUS_OP,
      UNARY_OP_AS_SYMBOL, UNARY_PLUS_OP, UNARY_SUBTYPE_OP, UNARY_TYPE_OP,
      USING, VERSION_NUMBER, WHILE_EXPR),
  };

  /* ********************************************************** */
  // ABSTRACT_TYPE_KEYWORD endOfLine
  //   symbol typeParameters? (SUBTYPE_SYM endOfLine expr)? SEMICOLON_SYM? endOfLine
  //  END_KEYWORD
  public static boolean abstractTypeDeclaration(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "abstractTypeDeclaration")) return false;
    if (!nextTokenIs(builder, ABSTRACT_TYPE_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, ABSTRACT_TYPE_DECLARATION, null);
    result = consumeToken(builder, ABSTRACT_TYPE_KEYWORD);
    pinned = result; // pin = 1
    result = result && report_error_(builder, endOfLine(builder, level + 1));
    result = pinned && report_error_(builder, symbol(builder, level + 1)) && result;
    result = pinned && report_error_(builder, abstractTypeDeclaration_3(builder, level + 1)) && result;
    result = pinned && report_error_(builder, abstractTypeDeclaration_4(builder, level + 1)) && result;
    result = pinned && report_error_(builder, abstractTypeDeclaration_5(builder, level + 1)) && result;
    result = pinned && report_error_(builder, endOfLine(builder, level + 1)) && result;
    result = pinned && consumeToken(builder, END_KEYWORD) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // typeParameters?
  private static boolean abstractTypeDeclaration_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "abstractTypeDeclaration_3")) return false;
    typeParameters(builder, level + 1);
    return true;
  }

  // (SUBTYPE_SYM endOfLine expr)?
  private static boolean abstractTypeDeclaration_4(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "abstractTypeDeclaration_4")) return false;
    abstractTypeDeclaration_4_0(builder, level + 1);
    return true;
  }

  // SUBTYPE_SYM endOfLine expr
  private static boolean abstractTypeDeclaration_4_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "abstractTypeDeclaration_4_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, SUBTYPE_SYM);
    result = result && endOfLine(builder, level + 1);
    result = result && expr(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // SEMICOLON_SYM?
  private static boolean abstractTypeDeclaration_5(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "abstractTypeDeclaration_5")) return false;
    consumeToken(builder, SEMICOLON_SYM);
    return true;
  }

  /* ********************************************************** */
  // (DOT_SYM | DOUBLE_DOT_SYM | SLICE_SYM)? memberAccess
  static boolean access(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "access")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = access_0(builder, level + 1);
    result = result && memberAccess(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (DOT_SYM | DOUBLE_DOT_SYM | SLICE_SYM)?
  private static boolean access_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "access_0")) return false;
    access_0_0(builder, level + 1);
    return true;
  }

  // DOT_SYM | DOUBLE_DOT_SYM | SLICE_SYM
  private static boolean access_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "access_0_0")) return false;
    boolean result;
    result = consumeToken(builder, DOT_SYM);
    if (!result) result = consumeToken(builder, DOUBLE_DOT_SYM);
    if (!result) result = consumeToken(builder, SLICE_SYM);
    return result;
  }

  /* ********************************************************** */
  // (symbol | LEFT_B_BRACKET symbol RIGHT_B_BRACKET)
  //     ((SUBTYPE_SYM) endOfLine symbol typeParameters?)?
  //  | (endOfLine LEFT_B_BRACKET endOfLine)
  //     (afterWhere (commaSep afterWhere)*)?
  //      COMMA_SYM? endOfLine
  //    RIGHT_B_BRACKET
  static boolean afterWhere(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "afterWhere")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = afterWhere_0(builder, level + 1);
    if (!result) result = afterWhere_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (symbol | LEFT_B_BRACKET symbol RIGHT_B_BRACKET)
  //     ((SUBTYPE_SYM) endOfLine symbol typeParameters?)?
  private static boolean afterWhere_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "afterWhere_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = afterWhere_0_0(builder, level + 1);
    result = result && afterWhere_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // symbol | LEFT_B_BRACKET symbol RIGHT_B_BRACKET
  private static boolean afterWhere_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "afterWhere_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = symbol(builder, level + 1);
    if (!result) result = afterWhere_0_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // LEFT_B_BRACKET symbol RIGHT_B_BRACKET
  private static boolean afterWhere_0_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "afterWhere_0_0_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, LEFT_B_BRACKET);
    result = result && symbol(builder, level + 1);
    result = result && consumeToken(builder, RIGHT_B_BRACKET);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ((SUBTYPE_SYM) endOfLine symbol typeParameters?)?
  private static boolean afterWhere_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "afterWhere_0_1")) return false;
    afterWhere_0_1_0(builder, level + 1);
    return true;
  }

  // (SUBTYPE_SYM) endOfLine symbol typeParameters?
  private static boolean afterWhere_0_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "afterWhere_0_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, SUBTYPE_SYM);
    result = result && endOfLine(builder, level + 1);
    result = result && symbol(builder, level + 1);
    result = result && afterWhere_0_1_0_3(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // typeParameters?
  private static boolean afterWhere_0_1_0_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "afterWhere_0_1_0_3")) return false;
    typeParameters(builder, level + 1);
    return true;
  }

  // (endOfLine LEFT_B_BRACKET endOfLine)
  //     (afterWhere (commaSep afterWhere)*)?
  //      COMMA_SYM? endOfLine
  //    RIGHT_B_BRACKET
  private static boolean afterWhere_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "afterWhere_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = afterWhere_1_0(builder, level + 1);
    result = result && afterWhere_1_1(builder, level + 1);
    result = result && afterWhere_1_2(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && consumeToken(builder, RIGHT_B_BRACKET);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // endOfLine LEFT_B_BRACKET endOfLine
  private static boolean afterWhere_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "afterWhere_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = endOfLine(builder, level + 1);
    result = result && consumeToken(builder, LEFT_B_BRACKET);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (afterWhere (commaSep afterWhere)*)?
  private static boolean afterWhere_1_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "afterWhere_1_1")) return false;
    afterWhere_1_1_0(builder, level + 1);
    return true;
  }

  // afterWhere (commaSep afterWhere)*
  private static boolean afterWhere_1_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "afterWhere_1_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = afterWhere(builder, level + 1);
    result = result && afterWhere_1_1_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (commaSep afterWhere)*
  private static boolean afterWhere_1_1_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "afterWhere_1_1_0_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!afterWhere_1_1_0_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "afterWhere_1_1_0_1", pos)) break;
    }
    return true;
  }

  // commaSep afterWhere
  private static boolean afterWhere_1_1_0_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "afterWhere_1_1_0_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = commaSep(builder, level + 1);
    result = result && afterWhere(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // COMMA_SYM?
  private static boolean afterWhere_1_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "afterWhere_1_2")) return false;
    consumeToken(builder, COMMA_SYM);
    return true;
  }

  /* ********************************************************** */
  // DOUBLE_COLON | DOT_SYM | privateOperaSymbols
  static boolean allowQuoteSymbols(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "allowQuoteSymbols")) return false;
    if (!nextTokenIs(builder, "", AND_SYM, ARROW_SYM,
      ASSIGN_SYM, BITWISE_AND_SYM, BITWISE_NOT_SYM, BITWISE_OR_SYM, BITWISE_XOR_SYM, COLON_SYM,
      DIVIDE_SYM, DOT_SYM, DOUBLE_COLON, EQUALS_SYM, EQ_SYM, EXPONENT_SYM,
      FACTORISE_SYM, FRACTION_SYM, GREATER_THAN_OR_EQUAL_SYM, GREATER_THAN_SYM, INVERSE_DIV_SYM, INVRESE_PIPE_SYM,
      IN_SYM, ISNT_SYM, IS_SYM, LAMBDA_ABSTRACTION, LESS_THAN_OR_EQUAL_SYM, LESS_THAN_SYM,
      MISC_ARROW_SYM, MISC_COMPARISON_SYM, MISC_EXPONENT_SYM, MISC_MULTIPLY_SYM, MISC_PLUS_SYM, MULTIPLY_SYM,
      OR_SYM, PIPE_SYM, QUESTION_SYM, REMAINDER_SYM, SHL_SYM, SHR_SYM,
      SLICE_SYM, SPECIAL_ARROW_SYM, SUBTYPE_SYM, SUPERTYPE_SYM, TRANSPOSE_SYM, UNEQUAL_SYM, USHR_SYM)) return false;
    boolean result;
    result = consumeToken(builder, DOUBLE_COLON);
    if (!result) result = consumeToken(builder, DOT_SYM);
    if (!result) result = privateOperaSymbols(builder, level + 1);
    return result;
  }

  /* ********************************************************** */
  // SEMICOLON_SYM endOfLine
  //  (expressionList endOfLine)?
  //  (commaSep)?
  //  (symbol SLICE_SYM)?
  public static boolean arguments(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "arguments")) return false;
    if (!nextTokenIs(builder, SEMICOLON_SYM)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, ARGUMENTS, null);
    result = consumeToken(builder, SEMICOLON_SYM);
    pinned = result; // pin = 1
    result = result && report_error_(builder, endOfLine(builder, level + 1));
    result = pinned && report_error_(builder, arguments_2(builder, level + 1)) && result;
    result = pinned && report_error_(builder, arguments_3(builder, level + 1)) && result;
    result = pinned && arguments_4(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // (expressionList endOfLine)?
  private static boolean arguments_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "arguments_2")) return false;
    arguments_2_0(builder, level + 1);
    return true;
  }

  // expressionList endOfLine
  private static boolean arguments_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "arguments_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = expressionList(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (commaSep)?
  private static boolean arguments_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "arguments_3")) return false;
    arguments_3_0(builder, level + 1);
    return true;
  }

  // (commaSep)
  private static boolean arguments_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "arguments_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = commaSep(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (symbol SLICE_SYM)?
  private static boolean arguments_4(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "arguments_4")) return false;
    arguments_4_0(builder, level + 1);
    return true;
  }

  // symbol SLICE_SYM
  private static boolean arguments_4_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "arguments_4_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = symbol(builder, level + 1);
    result = result && consumeToken(builder, SLICE_SYM);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // COLON_ASSIGN_SYM
  //  | BITWISE_AND_ASSIGN_SYM
  //  | BITWISE_OR_ASSIGN_SYM
  //  | BITWISE_XOR_ASSIGN_SYM
  //  | INVERSE_DIV_ASSIGN_SYM
  //  | SHL_ASSIGN_SYM
  //  | SHR_ASSIGN_SYM
  //  | REMAINDER_ASSIGN_SYM
  //  | USHR_ASSIGN_SYM
  //  | PLUS_ASSIGN_SYM
  //  | MINUS_ASSIGN_SYM
  //  | MULTIPLY_ASSIGN_SYM
  //  | FRACTION_ASSIGN_SYM
  //  | DIVIDE_ASSIGN_SYM
  //  | FACTORISE_ASSIGN_SYM
  //  | EXPONENT_ASSIGN_SYM
  public static boolean assignLevelOperator(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "assignLevelOperator")) return false;
    if (!nextTokenIs(builder, "<assign level operator>", BITWISE_AND_ASSIGN_SYM, BITWISE_OR_ASSIGN_SYM,
      BITWISE_XOR_ASSIGN_SYM, COLON_ASSIGN_SYM, DIVIDE_ASSIGN_SYM, EXPONENT_ASSIGN_SYM, FACTORISE_ASSIGN_SYM, FRACTION_ASSIGN_SYM,
      INVERSE_DIV_ASSIGN_SYM, MINUS_ASSIGN_SYM, MULTIPLY_ASSIGN_SYM, PLUS_ASSIGN_SYM, REMAINDER_ASSIGN_SYM, SHL_ASSIGN_SYM, SHR_ASSIGN_SYM, USHR_ASSIGN_SYM)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, ASSIGN_LEVEL_OPERATOR, "<assign level operator>");
    result = consumeToken(builder, COLON_ASSIGN_SYM);
    if (!result) result = consumeToken(builder, BITWISE_AND_ASSIGN_SYM);
    if (!result) result = consumeToken(builder, BITWISE_OR_ASSIGN_SYM);
    if (!result) result = consumeToken(builder, BITWISE_XOR_ASSIGN_SYM);
    if (!result) result = consumeToken(builder, INVERSE_DIV_ASSIGN_SYM);
    if (!result) result = consumeToken(builder, SHL_ASSIGN_SYM);
    if (!result) result = consumeToken(builder, SHR_ASSIGN_SYM);
    if (!result) result = consumeToken(builder, REMAINDER_ASSIGN_SYM);
    if (!result) result = consumeToken(builder, USHR_ASSIGN_SYM);
    if (!result) result = consumeToken(builder, PLUS_ASSIGN_SYM);
    if (!result) result = consumeToken(builder, MINUS_ASSIGN_SYM);
    if (!result) result = consumeToken(builder, MULTIPLY_ASSIGN_SYM);
    if (!result) result = consumeToken(builder, FRACTION_ASSIGN_SYM);
    if (!result) result = consumeToken(builder, DIVIDE_ASSIGN_SYM);
    if (!result) result = consumeToken(builder, FACTORISE_ASSIGN_SYM);
    if (!result) result = consumeToken(builder, EXPONENT_ASSIGN_SYM);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // SHR_SYM
  //  | USHR_SYM
  //  | SHL_SYM
  public static boolean bitwiseLevelOperator(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "bitwiseLevelOperator")) return false;
    if (!nextTokenIs(builder, "<bitwise level operator>", SHL_SYM, SHR_SYM, USHR_SYM)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, BITWISE_LEVEL_OPERATOR, "<bitwise level operator>");
    result = consumeToken(builder, SHR_SYM);
    if (!result) result = consumeToken(builder, USHR_SYM);
    if (!result) result = consumeToken(builder, SHL_SYM);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // BLOCK_COMMENT_START (BLOCK_COMMENT_BODY | blockComment)* BLOCK_COMMENT_END
  static boolean blockComment(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "blockComment")) return false;
    if (!nextTokenIs(builder, BLOCK_COMMENT_START)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, BLOCK_COMMENT_START);
    result = result && blockComment_1(builder, level + 1);
    result = result && consumeToken(builder, BLOCK_COMMENT_END);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (BLOCK_COMMENT_BODY | blockComment)*
  private static boolean blockComment_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "blockComment_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!blockComment_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "blockComment_1", pos)) break;
    }
    return true;
  }

  // BLOCK_COMMENT_BODY | blockComment
  private static boolean blockComment_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "blockComment_1_0")) return false;
    boolean result;
    result = consumeToken(builder, BLOCK_COMMENT_BODY);
    if (!result) result = blockComment(builder, level + 1);
    return result;
  }

  /* ********************************************************** */
  // LEFT_BRACKET typedNamedVariable RIGHT_BRACKET
  static boolean bracketedFunctionName(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "bracketedFunctionName")) return false;
    if (!nextTokenIs(builder, LEFT_BRACKET)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, LEFT_BRACKET);
    result = result && typedNamedVariable(builder, level + 1);
    result = result && consumeToken(builder, RIGHT_BRACKET);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // CATCH_KEYWORD symbol? endOfLine
  //  statements
  public static boolean catchClause(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "catchClause")) return false;
    if (!nextTokenIs(builder, CATCH_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, CATCH_CLAUSE, null);
    result = consumeToken(builder, CATCH_KEYWORD);
    pinned = result; // pin = 1
    result = result && report_error_(builder, catchClause_1(builder, level + 1));
    result = pinned && report_error_(builder, endOfLine(builder, level + 1)) && result;
    result = pinned && statements(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // symbol?
  private static boolean catchClause_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "catchClause_1")) return false;
    symbol(builder, level + 1);
    return true;
  }

  /* ********************************************************** */
  // endOfLine COMMA_SYM endOfLine
  static boolean commaSep(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "commaSep")) return false;
    if (!nextTokenIs(builder, "", BLOCK_COMMENT_START, COMMA_SYM, EOL, LINE_COMMENT)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = endOfLine(builder, level + 1);
    result = result && consumeToken(builder, COMMA_SYM);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // blockComment | LINE_COMMENT
  public static boolean comment(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "comment")) return false;
    if (!nextTokenIs(builder, "<comment>", BLOCK_COMMENT_START, LINE_COMMENT)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, COMMENT, "<comment>");
    result = blockComment(builder, level + 1);
    if (!result) result = consumeToken(builder, LINE_COMMENT);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // LESS_THAN_SYM
  //  | LESS_THAN_OR_EQUAL_SYM
  //  | GREATER_THAN_SYM
  //  | GREATER_THAN_OR_EQUAL_SYM
  //  | EQUALS_SYM
  //  | UNEQUAL_SYM
  //  | IS_SYM
  //  | ISNT_SYM
  //  | SUBTYPE_SYM
  //  | SUPERTYPE_SYM
  //  | MISC_COMPARISON_SYM
  public static boolean comparisonLevelOperator(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "comparisonLevelOperator")) return false;
    if (!nextTokenIs(builder, "<comparison level operator>", EQUALS_SYM, GREATER_THAN_OR_EQUAL_SYM,
      GREATER_THAN_SYM, ISNT_SYM, IS_SYM, LESS_THAN_OR_EQUAL_SYM, LESS_THAN_SYM, MISC_COMPARISON_SYM,
      SUBTYPE_SYM, SUPERTYPE_SYM, UNEQUAL_SYM)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, COMPARISON_LEVEL_OPERATOR, "<comparison level operator>");
    result = consumeToken(builder, LESS_THAN_SYM);
    if (!result) result = consumeToken(builder, LESS_THAN_OR_EQUAL_SYM);
    if (!result) result = consumeToken(builder, GREATER_THAN_SYM);
    if (!result) result = consumeToken(builder, GREATER_THAN_OR_EQUAL_SYM);
    if (!result) result = consumeToken(builder, EQUALS_SYM);
    if (!result) result = consumeToken(builder, UNEQUAL_SYM);
    if (!result) result = consumeToken(builder, IS_SYM);
    if (!result) result = consumeToken(builder, ISNT_SYM);
    if (!result) result = consumeToken(builder, SUBTYPE_SYM);
    if (!result) result = consumeToken(builder, SUPERTYPE_SYM);
    if (!result) result = consumeToken(builder, MISC_COMPARISON_SYM);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // endOfLine expr endOfLine
  //  (
  //   FOR_KEYWORD
  //   singleComprehension
  //    (commaSep singleComprehension)?)+
  public static boolean comprehensionElement(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "comprehensionElement")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, COMPREHENSION_ELEMENT, "<comprehension element>");
    result = endOfLine(builder, level + 1);
    result = result && expr(builder, level + 1, -1);
    result = result && endOfLine(builder, level + 1);
    result = result && comprehensionElement_3(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // (
  //   FOR_KEYWORD
  //   singleComprehension
  //    (commaSep singleComprehension)?)+
  private static boolean comprehensionElement_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "comprehensionElement_3")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = comprehensionElement_3_0(builder, level + 1);
    while (result) {
      int pos = current_position_(builder);
      if (!comprehensionElement_3_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "comprehensionElement_3", pos)) break;
    }
    exit_section_(builder, marker, null, result);
    return result;
  }

  // FOR_KEYWORD
  //   singleComprehension
  //    (commaSep singleComprehension)?
  private static boolean comprehensionElement_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "comprehensionElement_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FOR_KEYWORD);
    result = result && singleComprehension(builder, level + 1);
    result = result && comprehensionElement_3_0_2(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (commaSep singleComprehension)?
  private static boolean comprehensionElement_3_0_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "comprehensionElement_3_0_2")) return false;
    comprehensionElement_3_0_2_0(builder, level + 1);
    return true;
  }

  // commaSep singleComprehension
  private static boolean comprehensionElement_3_0_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "comprehensionElement_3_0_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = commaSep(builder, level + 1);
    result = result && singleComprehension(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // DO_KEYWORD <<lazyBlockNotParseEndImpl>> END_KEYWORD
  public static boolean doBlock(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "doBlock")) return false;
    if (!nextTokenIs(builder, DO_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, DO_BLOCK, null);
    result = consumeToken(builder, DO_KEYWORD);
    pinned = result; // pin = 1
    result = result && report_error_(builder, lazyBlockNotParseEndImpl(builder, level + 1));
    result = pinned && consumeToken(builder, END_KEYWORD) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // ELSE_KEYWORD statements
  public static boolean elseClause(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "elseClause")) return false;
    if (!nextTokenIs(builder, ELSE_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, ELSE_CLAUSE, null);
    result = consumeToken(builder, ELSE_KEYWORD);
    pinned = result; // pin = 1
    result = result && statements(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // ELSEIF_KEYWORD expr endOfLine
  //  statements
  public static boolean elseIfClause(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "elseIfClause")) return false;
    if (!nextTokenIs(builder, ELSEIF_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, ELSE_IF_CLAUSE, null);
    result = consumeToken(builder, ELSEIF_KEYWORD);
    pinned = result; // pin = 1
    result = result && report_error_(builder, expr(builder, level + 1, -1));
    result = pinned && report_error_(builder, endOfLine(builder, level + 1)) && result;
    result = pinned && statements(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // endOfLineImpl*
  static boolean endOfLine(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "endOfLine")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!endOfLineImpl(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "endOfLine", pos)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // EOL | comment
  static boolean endOfLineImpl(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "endOfLineImpl")) return false;
    if (!nextTokenIs(builder, "", BLOCK_COMMENT_START, EOL, LINE_COMMENT)) return false;
    boolean result;
    result = consumeToken(builder, EOL);
    if (!result) result = comment(builder, level + 1);
    return result;
  }

  /* ********************************************************** */
  // expr (commaSep expr)*
  static boolean expressionList(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "expressionList")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = expr(builder, level + 1, -1);
    result = result && expressionList_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (commaSep expr)*
  private static boolean expressionList_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "expressionList_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!expressionList_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "expressionList_1", pos)) break;
    }
    return true;
  }

  // commaSep expr
  private static boolean expressionList_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "expressionList_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = commaSep(builder, level + 1);
    result = result && expr(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // FINALLY_KEYWORD <<lazyBlockNotParseEndImpl>>
  public static boolean finallyClause(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "finallyClause")) return false;
    if (!nextTokenIs(builder, FINALLY_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FINALLY_CLAUSE, null);
    result = consumeToken(builder, FINALLY_KEYWORD);
    pinned = result; // pin = 1
    result = result && lazyBlockNotParseEndImpl(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // ISA_KEYWORD | IN_KEYWORD | UNION_KEYWORD
  static boolean functionNameLikeKeywords(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "functionNameLikeKeywords")) return false;
    if (!nextTokenIs(builder, "", IN_KEYWORD, ISA_KEYWORD, UNION_KEYWORD)) return false;
    boolean result;
    result = consumeToken(builder, ISA_KEYWORD);
    if (!result) result = consumeToken(builder, IN_KEYWORD);
    if (!result) result = consumeToken(builder, UNION_KEYWORD);
    return result;
  }

  /* ********************************************************** */
  // LEFT_BRACKET endOfLine
  //   (typedNamedVariable (commaSep typedNamedVariable)*)?
  //   (SEMICOLON_SYM endOfLine
  //    (typedNamedVariable (commaSep typedNamedVariable)*)?)?
  //   commaSep?
  //  RIGHT_BRACKET
  //  typeAnnotation?
  public static boolean functionSignature(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "functionSignature")) return false;
    if (!nextTokenIs(builder, LEFT_BRACKET)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FUNCTION_SIGNATURE, null);
    result = consumeToken(builder, LEFT_BRACKET);
    pinned = result; // pin = 1
    result = result && report_error_(builder, endOfLine(builder, level + 1));
    result = pinned && report_error_(builder, functionSignature_2(builder, level + 1)) && result;
    result = pinned && report_error_(builder, functionSignature_3(builder, level + 1)) && result;
    result = pinned && report_error_(builder, functionSignature_4(builder, level + 1)) && result;
    result = pinned && report_error_(builder, consumeToken(builder, RIGHT_BRACKET)) && result;
    result = pinned && functionSignature_6(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // (typedNamedVariable (commaSep typedNamedVariable)*)?
  private static boolean functionSignature_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "functionSignature_2")) return false;
    functionSignature_2_0(builder, level + 1);
    return true;
  }

  // typedNamedVariable (commaSep typedNamedVariable)*
  private static boolean functionSignature_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "functionSignature_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = typedNamedVariable(builder, level + 1);
    result = result && functionSignature_2_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (commaSep typedNamedVariable)*
  private static boolean functionSignature_2_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "functionSignature_2_0_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!functionSignature_2_0_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "functionSignature_2_0_1", pos)) break;
    }
    return true;
  }

  // commaSep typedNamedVariable
  private static boolean functionSignature_2_0_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "functionSignature_2_0_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = commaSep(builder, level + 1);
    result = result && typedNamedVariable(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (SEMICOLON_SYM endOfLine
  //    (typedNamedVariable (commaSep typedNamedVariable)*)?)?
  private static boolean functionSignature_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "functionSignature_3")) return false;
    functionSignature_3_0(builder, level + 1);
    return true;
  }

  // SEMICOLON_SYM endOfLine
  //    (typedNamedVariable (commaSep typedNamedVariable)*)?
  private static boolean functionSignature_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "functionSignature_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, SEMICOLON_SYM);
    result = result && endOfLine(builder, level + 1);
    result = result && functionSignature_3_0_2(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (typedNamedVariable (commaSep typedNamedVariable)*)?
  private static boolean functionSignature_3_0_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "functionSignature_3_0_2")) return false;
    functionSignature_3_0_2_0(builder, level + 1);
    return true;
  }

  // typedNamedVariable (commaSep typedNamedVariable)*
  private static boolean functionSignature_3_0_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "functionSignature_3_0_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = typedNamedVariable(builder, level + 1);
    result = result && functionSignature_3_0_2_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (commaSep typedNamedVariable)*
  private static boolean functionSignature_3_0_2_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "functionSignature_3_0_2_0_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!functionSignature_3_0_2_0_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "functionSignature_3_0_2_0_1", pos)) break;
    }
    return true;
  }

  // commaSep typedNamedVariable
  private static boolean functionSignature_3_0_2_0_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "functionSignature_3_0_2_0_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = commaSep(builder, level + 1);
    result = result && typedNamedVariable(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // commaSep?
  private static boolean functionSignature_4(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "functionSignature_4")) return false;
    commaSep(builder, level + 1);
    return true;
  }

  // typeAnnotation?
  private static boolean functionSignature_6(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "functionSignature_6")) return false;
    typeAnnotation(builder, level + 1);
    return true;
  }

  /* ********************************************************** */
  // access (COLON_SYM endOfLine access)? (commaSep access)*
  static boolean imported(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "imported")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = access(builder, level + 1);
    result = result && imported_1(builder, level + 1);
    result = result && imported_2(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (COLON_SYM endOfLine access)?
  private static boolean imported_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "imported_1")) return false;
    imported_1_0(builder, level + 1);
    return true;
  }

  // COLON_SYM endOfLine access
  private static boolean imported_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "imported_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, COLON_SYM);
    result = result && endOfLine(builder, level + 1);
    result = result && access(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (commaSep access)*
  private static boolean imported_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "imported_2")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!imported_2_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "imported_2", pos)) break;
    }
    return true;
  }

  // commaSep access
  private static boolean imported_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "imported_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = commaSep(builder, level + 1);
    result = result && access(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // multiIndexer | singleIndexer
  static boolean indexer(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "indexer")) return false;
    boolean result;
    result = multiIndexer(builder, level + 1);
    if (!result) result = singleIndexer(builder, level + 1);
    return result;
  }

  /* ********************************************************** */
  // IN_KEYWORD | IN_SYM | EQ_SYM
  static boolean infixIndexer(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "infixIndexer")) return false;
    if (!nextTokenIs(builder, "", EQ_SYM, IN_KEYWORD, IN_SYM)) return false;
    boolean result;
    result = consumeToken(builder, IN_KEYWORD);
    if (!result) result = consumeToken(builder, IN_SYM);
    if (!result) result = consumeToken(builder, EQ_SYM);
    return result;
  }

  /* ********************************************************** */
  // LEFT_BRACKET interpolateSymbolAsOp RIGHT_BRACKET
  static boolean interpolateAsOp(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "interpolateAsOp")) return false;
    if (!nextTokenIs(builder, LEFT_BRACKET)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, LEFT_BRACKET);
    result = result && interpolateSymbolAsOp(builder, level + 1);
    result = result && consumeToken(builder, RIGHT_BRACKET);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // INTERPOLATE_SYM (SYM SLICE_SYM?)?
  static boolean interpolateSymbolAsOp(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "interpolateSymbolAsOp")) return false;
    if (!nextTokenIs(builder, INTERPOLATE_SYM)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = consumeToken(builder, INTERPOLATE_SYM);
    pinned = result; // pin = 1
    result = result && interpolateSymbolAsOp_1(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // (SYM SLICE_SYM?)?
  private static boolean interpolateSymbolAsOp_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "interpolateSymbolAsOp_1")) return false;
    interpolateSymbolAsOp_1_0(builder, level + 1);
    return true;
  }

  // SYM SLICE_SYM?
  private static boolean interpolateSymbolAsOp_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "interpolateSymbolAsOp_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, SYM);
    result = result && interpolateSymbolAsOp_1_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // SLICE_SYM?
  private static boolean interpolateSymbolAsOp_1_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "interpolateSymbolAsOp_1_0_1")) return false;
    consumeToken(builder, SLICE_SYM);
    return true;
  }

  /* ********************************************************** */
  // endOfLine statements
  static boolean juliaFile(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "juliaFile")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = endOfLine(builder, level + 1);
    result = result && statements(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // MODULE_KEYWORD
  //   | WHERE_KEYWORD
  //   // TODO: do we really need this?
  //   | ABSTRACT_KEYWORD
  //   | MUTABLE_KEYWORD
  //   | IMMUTABLE_KEYWORD // deprecated
  //   | functionNameLikeKeywords
  static boolean keywordsAsOp(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "keywordsAsOp")) return false;
    if (!nextTokenIs(builder, "", ABSTRACT_KEYWORD, IMMUTABLE_KEYWORD,
      IN_KEYWORD, ISA_KEYWORD, MODULE_KEYWORD, MUTABLE_KEYWORD, UNION_KEYWORD, WHERE_KEYWORD)) return false;
    boolean result;
    result = consumeToken(builder, MODULE_KEYWORD);
    if (!result) result = consumeToken(builder, WHERE_KEYWORD);
    if (!result) result = consumeToken(builder, ABSTRACT_KEYWORD);
    if (!result) result = consumeToken(builder, MUTABLE_KEYWORD);
    if (!result) result = consumeToken(builder, IMMUTABLE_KEYWORD);
    if (!result) result = functionNameLikeKeywords(builder, level + 1);
    return result;
  }

  /* ********************************************************** */
  // IN_KEYWORD
  //  | UNION_KEYWORD
  //  | exprInterpolateOp
  //  | unaryInterpolateOp
  //  | opAsSymbol
  //  | (LEFT_BRACKET opAsSymbol RIGHT_BRACKET)
  //  | FLOAT_CONSTANT
  //  | symbolAndMacroSymbol (DOT_SYM symbolAndMacroSymbol)*
  public static boolean memberAccess(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "memberAccess")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, MEMBER_ACCESS, "<member access>");
    result = consumeToken(builder, IN_KEYWORD);
    if (!result) result = consumeToken(builder, UNION_KEYWORD);
    if (!result) result = exprInterpolateOp(builder, level + 1);
    if (!result) result = unaryInterpolateOp(builder, level + 1);
    if (!result) result = opAsSymbol(builder, level + 1);
    if (!result) result = memberAccess_5(builder, level + 1);
    if (!result) result = consumeToken(builder, FLOAT_CONSTANT);
    if (!result) result = memberAccess_7(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // LEFT_BRACKET opAsSymbol RIGHT_BRACKET
  private static boolean memberAccess_5(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "memberAccess_5")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, LEFT_BRACKET);
    result = result && opAsSymbol(builder, level + 1);
    result = result && consumeToken(builder, RIGHT_BRACKET);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // symbolAndMacroSymbol (DOT_SYM symbolAndMacroSymbol)*
  private static boolean memberAccess_7(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "memberAccess_7")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = symbolAndMacroSymbol(builder, level + 1);
    result = result && memberAccess_7_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (DOT_SYM symbolAndMacroSymbol)*
  private static boolean memberAccess_7_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "memberAccess_7_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!memberAccess_7_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "memberAccess_7_1", pos)) break;
    }
    return true;
  }

  // DOT_SYM symbolAndMacroSymbol
  private static boolean memberAccess_7_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "memberAccess_7_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, DOT_SYM);
    result = result && symbolAndMacroSymbol(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // (BAREMODULE_KEYWORD | MODULE_KEYWORD) symbol endOfLine
  //   statements
  //  END_KEYWORD
  public static boolean moduleDeclaration(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "moduleDeclaration")) return false;
    if (!nextTokenIs(builder, "<module declaration>", BAREMODULE_KEYWORD, MODULE_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, MODULE_DECLARATION, "<module declaration>");
    result = moduleDeclaration_0(builder, level + 1);
    pinned = result; // pin = 1
    result = result && report_error_(builder, symbol(builder, level + 1));
    result = pinned && report_error_(builder, endOfLine(builder, level + 1)) && result;
    result = pinned && report_error_(builder, statements(builder, level + 1)) && result;
    result = pinned && consumeToken(builder, END_KEYWORD) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // BAREMODULE_KEYWORD | MODULE_KEYWORD
  private static boolean moduleDeclaration_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "moduleDeclaration_0")) return false;
    boolean result;
    result = consumeToken(builder, BAREMODULE_KEYWORD);
    if (!result) result = consumeToken(builder, MODULE_KEYWORD);
    return result;
  }

  /* ********************************************************** */
  // strictExpressionList commaSep? (EQ_SYM | ASSIGN_SYM) endOfLine expressionList
  public static boolean multiAssignOp(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "multiAssignOp")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _COLLAPSE_, MULTI_ASSIGN_OP, "<multi assign op>");
    result = strictExpressionList(builder, level + 1);
    result = result && multiAssignOp_1(builder, level + 1);
    result = result && multiAssignOp_2(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && expressionList(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // commaSep?
  private static boolean multiAssignOp_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "multiAssignOp_1")) return false;
    commaSep(builder, level + 1);
    return true;
  }

  // EQ_SYM | ASSIGN_SYM
  private static boolean multiAssignOp_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "multiAssignOp_2")) return false;
    boolean result;
    result = consumeToken(builder, EQ_SYM);
    if (!result) result = consumeToken(builder, ASSIGN_SYM);
    return result;
  }

  /* ********************************************************** */
  // tuple infixIndexer endOfLine expr
  public static boolean multiIndexer(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "multiIndexer")) return false;
    if (!nextTokenIs(builder, LEFT_BRACKET)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = tuple(builder, level + 1);
    result = result && infixIndexer(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && expr(builder, level + 1, -1);
    exit_section_(builder, marker, MULTI_INDEXER, result);
    return result;
  }

  /* ********************************************************** */
  // MULTIPLY_SYM
  //  | DIVIDE_SYM
  //  | REMAINDER_SYM
  //  | INVERSE_DIV_SYM
  //  | FACTORISE_SYM
  //  | BITWISE_AND_SYM
  //  | MISC_MULTIPLY_SYM
  public static boolean multiplyLevelOperator(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "multiplyLevelOperator")) return false;
    if (!nextTokenIs(builder, "<multiply level operator>", BITWISE_AND_SYM, DIVIDE_SYM,
      FACTORISE_SYM, INVERSE_DIV_SYM, MISC_MULTIPLY_SYM, MULTIPLY_SYM, REMAINDER_SYM)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, MULTIPLY_LEVEL_OPERATOR, "<multiply level operator>");
    result = consumeToken(builder, MULTIPLY_SYM);
    if (!result) result = consumeToken(builder, DIVIDE_SYM);
    if (!result) result = consumeToken(builder, REMAINDER_SYM);
    if (!result) result = consumeToken(builder, INVERSE_DIV_SYM);
    if (!result) result = consumeToken(builder, FACTORISE_SYM);
    if (!result) result = consumeToken(builder, BITWISE_AND_SYM);
    if (!result) result = consumeToken(builder, MISC_MULTIPLY_SYM);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // symbol | memberAccess | tuple
  static boolean namedLeftValue(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "namedLeftValue")) return false;
    boolean result;
    result = symbol(builder, level + 1);
    if (!result) result = memberAccess(builder, level + 1);
    if (!result) result = tuple(builder, level + 1);
    return result;
  }

  /* ********************************************************** */
  // privateOpAsSymbol
  public static boolean opAsSymbol(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "opAsSymbol")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _COLLAPSE_, OP_AS_SYMBOL, "<op as symbol>");
    result = privateOpAsSymbol(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // PIPE_SYM | INVERSE_PIPE_SYM
  public static boolean pipeLevelOperator(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "pipeLevelOperator")) return false;
    if (!nextTokenIs(builder, "<pipe level operator>", INVERSE_PIPE_SYM, PIPE_SYM)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, PIPE_LEVEL_OPERATOR, "<pipe level operator>");
    result = consumeToken(builder, PIPE_SYM);
    if (!result) result = consumeToken(builder, INVERSE_PIPE_SYM);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // INTERPOLATE_SYM
  //  | BITWISE_XOR_SYM
  //  | BITWISE_OR_SYM
  //  | PLUS_SYM
  //  | MINUS_SYM
  //  | MISC_PLUS_SYM
  public static boolean plusLevelOperator(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "plusLevelOperator")) return false;
    if (!nextTokenIs(builder, "<plus level operator>", BITWISE_OR_SYM, BITWISE_XOR_SYM,
      INTERPOLATE_SYM, MINUS_SYM, MISC_PLUS_SYM, PLUS_SYM)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, PLUS_LEVEL_OPERATOR, "<plus level operator>");
    result = consumeToken(builder, INTERPOLATE_SYM);
    if (!result) result = consumeToken(builder, BITWISE_XOR_SYM);
    if (!result) result = consumeToken(builder, BITWISE_OR_SYM);
    if (!result) result = consumeToken(builder, PLUS_SYM);
    if (!result) result = consumeToken(builder, MINUS_SYM);
    if (!result) result = consumeToken(builder, MISC_PLUS_SYM);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // EXPR_INTERPOLATE_START endOfLine expr typeAnnotation? SLICE_SYM? RIGHT_BRACKET
  static boolean privateExprInterpolateOp(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "privateExprInterpolateOp")) return false;
    if (!nextTokenIs(builder, EXPR_INTERPOLATE_START)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, EXPR_INTERPOLATE_START);
    result = result && endOfLine(builder, level + 1);
    result = result && expr(builder, level + 1, -1);
    result = result && privateExprInterpolateOp_3(builder, level + 1);
    result = result && privateExprInterpolateOp_4(builder, level + 1);
    result = result && consumeToken(builder, RIGHT_BRACKET);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // typeAnnotation?
  private static boolean privateExprInterpolateOp_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "privateExprInterpolateOp_3")) return false;
    typeAnnotation(builder, level + 1);
    return true;
  }

  // SLICE_SYM?
  private static boolean privateExprInterpolateOp_4(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "privateExprInterpolateOp_4")) return false;
    consumeToken(builder, SLICE_SYM);
    return true;
  }

  /* ********************************************************** */
  // privateOpSymbols
  //  | keywordsAsOp
  //  | typeAnnotationAsOp
  //  | interpolateAsOp
  //  | interpolateSymbolAsOp
  //  | privateExprInterpolateOp
  static boolean privateOpAsSymbol(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "privateOpAsSymbol")) return false;
    boolean result;
    result = privateOpSymbols(builder, level + 1);
    if (!result) result = keywordsAsOp(builder, level + 1);
    if (!result) result = typeAnnotationAsOp(builder, level + 1);
    if (!result) result = interpolateAsOp(builder, level + 1);
    if (!result) result = interpolateSymbolAsOp(builder, level + 1);
    if (!result) result = privateExprInterpolateOp(builder, level + 1);
    return result;
  }

  /* ********************************************************** */
  // privateUnaryOpAsSymbol | privateOperaSymbols
  static boolean privateOpSymbols(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "privateOpSymbols")) return false;
    if (!nextTokenIs(builder, "", AND_SYM, ARROW_SYM,
      ASSIGN_SYM, BITWISE_AND_SYM, BITWISE_NOT_SYM, BITWISE_OR_SYM, BITWISE_XOR_SYM, COLON_SYM,
      DIVIDE_SYM, EQUALS_SYM, EQ_SYM, EXPONENT_SYM, FACTORISE_SYM, FRACTION_SYM,
      GREATER_THAN_OR_EQUAL_SYM, GREATER_THAN_SYM, INTERPOLATE_SYM, INVERSE_DIV_SYM, INVRESE_PIPE_SYM, IN_SYM,
      ISNT_SYM, IS_SYM, LAMBDA_ABSTRACTION, LESS_THAN_OR_EQUAL_SYM, LESS_THAN_SYM, MINUS_SYM,
      MISC_ARROW_SYM, MISC_COMPARISON_SYM, MISC_EXPONENT_SYM, MISC_MULTIPLY_SYM, MISC_PLUS_SYM, MULTIPLY_SYM,
      NOT_SYM, OR_SYM, PIPE_SYM, PLUS_SYM, QUESTION_SYM, REMAINDER_SYM,
      SHL_SYM, SHR_SYM, SLICE_SYM, SPECIAL_ARROW_SYM, SUBTYPE_SYM, SUPERTYPE_SYM,
      TRANSPOSE_SYM, UNEQUAL_SYM, USHR_SYM)) return false;
    boolean result;
    result = privateUnaryOpAsSymbol(builder, level + 1);
    if (!result) result = privateOperaSymbols(builder, level + 1);
    return result;
  }

  /* ********************************************************** */
  // QUESTION_SYM
  //  | SUBTYPE_SYM
  //  | SUPERTYPE_SYM
  //  | INVERSE_DIV_SYM
  //  | IS_SYM
  //  | ISNT_SYM
  //  | EQ_SYM
  //  | LAMBDA_ABSTRACTION
  //  | ARROW_SYM
  //  | SLICE_SYM
  //  | LESS_THAN_SYM
  //  | LESS_THAN_OR_EQUAL_SYM
  //  | AND_SYM
  //  | OR_SYM
  //  | PIPE_SYM
  //  | INVRESE_PIPE_SYM
  //  | SHL_SYM
  //  | SHR_SYM
  //  | USHR_SYM
  //  | FRACTION_SYM
  //  | DIVIDE_SYM
  //  | REMAINDER_SYM
  //  | EXPONENT_SYM
  //  | MULTIPLY_SYM
  //  | EQUALS_SYM
  //  | UNEQUAL_SYM
  //  | GREATER_THAN_SYM
  //  | GREATER_THAN_OR_EQUAL_SYM
  //  | TRANSPOSE_SYM
  //  | IN_SYM
  //  | MISC_COMPARISON_SYM
  //  | MISC_PLUS_SYM
  //  | MISC_MULTIPLY_SYM
  //  | MISC_EXPONENT_SYM
  //  | FACTORISE_SYM
  //  | BITWISE_AND_SYM
  //  | BITWISE_OR_SYM
  //  | BITWISE_XOR_SYM
  //  | SPECIAL_ARROW_SYM
  //  | MISC_ARROW_SYM
  //  | BITWISE_NOT_SYM
  //  | ASSIGN_SYM
  //  | COLON_SYM
  static boolean privateOperaSymbols(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "privateOperaSymbols")) return false;
    if (!nextTokenIs(builder, "", AND_SYM, ARROW_SYM,
      ASSIGN_SYM, BITWISE_AND_SYM, BITWISE_NOT_SYM, BITWISE_OR_SYM, BITWISE_XOR_SYM, COLON_SYM,
      DIVIDE_SYM, EQUALS_SYM, EQ_SYM, EXPONENT_SYM, FACTORISE_SYM, FRACTION_SYM,
      GREATER_THAN_OR_EQUAL_SYM, GREATER_THAN_SYM, INVERSE_DIV_SYM, INVRESE_PIPE_SYM, IN_SYM, ISNT_SYM,
      IS_SYM, LAMBDA_ABSTRACTION, LESS_THAN_OR_EQUAL_SYM, LESS_THAN_SYM, MISC_ARROW_SYM, MISC_COMPARISON_SYM,
      MISC_EXPONENT_SYM, MISC_MULTIPLY_SYM, MISC_PLUS_SYM, MULTIPLY_SYM, OR_SYM, PIPE_SYM,
      QUESTION_SYM, REMAINDER_SYM, SHL_SYM, SHR_SYM, SLICE_SYM, SPECIAL_ARROW_SYM,
      SUBTYPE_SYM, SUPERTYPE_SYM, TRANSPOSE_SYM, UNEQUAL_SYM, USHR_SYM)) return false;
    boolean result;
    result = consumeToken(builder, QUESTION_SYM);
    if (!result) result = consumeToken(builder, SUBTYPE_SYM);
    if (!result) result = consumeToken(builder, SUPERTYPE_SYM);
    if (!result) result = consumeToken(builder, INVERSE_DIV_SYM);
    if (!result) result = consumeToken(builder, IS_SYM);
    if (!result) result = consumeToken(builder, ISNT_SYM);
    if (!result) result = consumeToken(builder, EQ_SYM);
    if (!result) result = consumeToken(builder, LAMBDA_ABSTRACTION);
    if (!result) result = consumeToken(builder, ARROW_SYM);
    if (!result) result = consumeToken(builder, SLICE_SYM);
    if (!result) result = consumeToken(builder, LESS_THAN_SYM);
    if (!result) result = consumeToken(builder, LESS_THAN_OR_EQUAL_SYM);
    if (!result) result = consumeToken(builder, AND_SYM);
    if (!result) result = consumeToken(builder, OR_SYM);
    if (!result) result = consumeToken(builder, PIPE_SYM);
    if (!result) result = consumeToken(builder, INVRESE_PIPE_SYM);
    if (!result) result = consumeToken(builder, SHL_SYM);
    if (!result) result = consumeToken(builder, SHR_SYM);
    if (!result) result = consumeToken(builder, USHR_SYM);
    if (!result) result = consumeToken(builder, FRACTION_SYM);
    if (!result) result = consumeToken(builder, DIVIDE_SYM);
    if (!result) result = consumeToken(builder, REMAINDER_SYM);
    if (!result) result = consumeToken(builder, EXPONENT_SYM);
    if (!result) result = consumeToken(builder, MULTIPLY_SYM);
    if (!result) result = consumeToken(builder, EQUALS_SYM);
    if (!result) result = consumeToken(builder, UNEQUAL_SYM);
    if (!result) result = consumeToken(builder, GREATER_THAN_SYM);
    if (!result) result = consumeToken(builder, GREATER_THAN_OR_EQUAL_SYM);
    if (!result) result = consumeToken(builder, TRANSPOSE_SYM);
    if (!result) result = consumeToken(builder, IN_SYM);
    if (!result) result = consumeToken(builder, MISC_COMPARISON_SYM);
    if (!result) result = consumeToken(builder, MISC_PLUS_SYM);
    if (!result) result = consumeToken(builder, MISC_MULTIPLY_SYM);
    if (!result) result = consumeToken(builder, MISC_EXPONENT_SYM);
    if (!result) result = consumeToken(builder, FACTORISE_SYM);
    if (!result) result = consumeToken(builder, BITWISE_AND_SYM);
    if (!result) result = consumeToken(builder, BITWISE_OR_SYM);
    if (!result) result = consumeToken(builder, BITWISE_XOR_SYM);
    if (!result) result = consumeToken(builder, SPECIAL_ARROW_SYM);
    if (!result) result = consumeToken(builder, MISC_ARROW_SYM);
    if (!result) result = consumeToken(builder, BITWISE_NOT_SYM);
    if (!result) result = consumeToken(builder, ASSIGN_SYM);
    if (!result) result = consumeToken(builder, COLON_SYM);
    return result;
  }

  /* ********************************************************** */
  // MINUS_SYM
  //  | PLUS_SYM
  //  | NOT_SYM
  //  | INTERPOLATE_SYM
  static boolean privateUnaryOpAsSymbol(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "privateUnaryOpAsSymbol")) return false;
    if (!nextTokenIs(builder, "", INTERPOLATE_SYM, MINUS_SYM, NOT_SYM, PLUS_SYM)) return false;
    boolean result;
    result = consumeToken(builder, MINUS_SYM);
    if (!result) result = consumeToken(builder, PLUS_SYM);
    if (!result) result = consumeToken(builder, NOT_SYM);
    if (!result) result = consumeToken(builder, INTERPOLATE_SYM);
    return result;
  }

  /* ********************************************************** */
  // unaryOpAsSymbol
  //  | statementCanBeAsExpression
  //  | interpolateAsOp // unbracketed is useless, and idk why
  //  | allowQuoteSymbols
  //  | opAsSymbol
  //  | QUOTE_KEYWORD
  //  | (LEFT_BRACKET quotable RIGHT_BRACKET)
  //  | primaryExpr
  static boolean quotable(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "quotable")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = unaryOpAsSymbol(builder, level + 1);
    if (!result) result = statementCanBeAsExpression(builder, level + 1);
    if (!result) result = interpolateAsOp(builder, level + 1);
    if (!result) result = allowQuoteSymbols(builder, level + 1);
    if (!result) result = opAsSymbol(builder, level + 1);
    if (!result) result = consumeToken(builder, QUOTE_KEYWORD);
    if (!result) result = quotable_6(builder, level + 1);
    if (!result) result = expr(builder, level + 1, 21);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // LEFT_BRACKET quotable RIGHT_BRACKET
  private static boolean quotable_6(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "quotable_6")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, LEFT_BRACKET);
    result = result && quotable(builder, level + 1);
    result = result && consumeToken(builder, RIGHT_BRACKET);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // endOfLineImpl | SEMICOLON_SYM
  static boolean semi(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "semi")) return false;
    if (!nextTokenIs(builder, "", BLOCK_COMMENT_START, EOL, LINE_COMMENT, SEMICOLON_SYM)) return false;
    boolean result;
    result = endOfLineImpl(builder, level + 1);
    if (!result) result = consumeToken(builder, SEMICOLON_SYM);
    return result;
  }

  /* ********************************************************** */
  // indexer (IF_KEYWORD expr)?
  public static boolean singleComprehension(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "singleComprehension")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, SINGLE_COMPREHENSION, "<single comprehension>");
    result = indexer(builder, level + 1);
    result = result && singleComprehension_1(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // (IF_KEYWORD expr)?
  private static boolean singleComprehension_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "singleComprehension_1")) return false;
    singleComprehension_1_0(builder, level + 1);
    return true;
  }

  // IF_KEYWORD expr
  private static boolean singleComprehension_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "singleComprehension_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, IF_KEYWORD);
    result = result && expr(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // symbol infixIndexer endOfLine expr
  public static boolean singleIndexer(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "singleIndexer")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, SINGLE_INDEXER, "<single indexer>");
    result = symbol(builder, level + 1);
    result = result && infixIndexer(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && expr(builder, level + 1, -1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // QUOTE_START stringTemplateElement* QUOTE_END
  static boolean singleQuoteString(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "singleQuoteString")) return false;
    if (!nextTokenIs(builder, QUOTE_START)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = consumeToken(builder, QUOTE_START);
    pinned = result; // pin = 1
    result = result && report_error_(builder, singleQuoteString_1(builder, level + 1));
    result = pinned && consumeToken(builder, QUOTE_END) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // stringTemplateElement*
  private static boolean singleQuoteString_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "singleQuoteString_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!stringTemplateElement(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "singleQuoteString_1", pos)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // integer | floatLit | symbol | string | regex | bracketedExpr
  static boolean specialLhs(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "specialLhs")) return false;
    boolean result;
    result = integer(builder, level + 1);
    if (!result) result = floatLit(builder, level + 1);
    if (!result) result = symbol(builder, level + 1);
    if (!result) result = string(builder, level + 1);
    if (!result) result = regex(builder, level + 1);
    if (!result) result = bracketedExpr(builder, level + 1);
    return result;
  }

  /* ********************************************************** */
  // symbol | string | charLit | integer | floatLit
  static boolean specialRhs(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "specialRhs")) return false;
    boolean result;
    result = symbol(builder, level + 1);
    if (!result) result = string(builder, level + 1);
    if (!result) result = charLit(builder, level + 1);
    if (!result) result = integer(builder, level + 1);
    if (!result) result = floatLit(builder, level + 1);
    return result;
  }

  /* ********************************************************** */
  // statementCanBeAsExpression
  //  | multiAssignOp // in the situation it won't be regarded as `typedNamedVariable`
  //  | expressionList
  //  | expr
  static boolean statement(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "statement")) return false;
    boolean result;
    result = statementCanBeAsExpression(builder, level + 1);
    if (!result) result = multiAssignOp(builder, level + 1);
    if (!result) result = expressionList(builder, level + 1);
    if (!result) result = expr(builder, level + 1, -1);
    return result;
  }

  /* ********************************************************** */
  // moduleDeclaration
  //  | importExpr
  //  | importAllExpr
  //  | export
  //  | typeDeclaration
  //  | abstractTypeDeclaration
  static boolean statementCanBeAsExpression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "statementCanBeAsExpression")) return false;
    boolean result;
    result = moduleDeclaration(builder, level + 1);
    if (!result) result = importExpr(builder, level + 1);
    if (!result) result = importAllExpr(builder, level + 1);
    if (!result) result = export(builder, level + 1);
    if (!result) result = typeDeclaration(builder, level + 1);
    if (!result) result = abstractTypeDeclaration(builder, level + 1);
    return result;
  }

  /* ********************************************************** */
  // statementCanBeAsExpression | expr
  static boolean statementEvenStupid(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "statementEvenStupid")) return false;
    boolean result;
    result = statementCanBeAsExpression(builder, level + 1);
    if (!result) result = expr(builder, level + 1, -1);
    return result;
  }

  /* ********************************************************** */
  // semi* (statement (semi+ statement)*)? semi*
  public static boolean statements(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "statements")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, STATEMENTS, "<statements>");
    result = statements_0(builder, level + 1);
    result = result && statements_1(builder, level + 1);
    result = result && statements_2(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // semi*
  private static boolean statements_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "statements_0")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!semi(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "statements_0", pos)) break;
    }
    return true;
  }

  // (statement (semi+ statement)*)?
  private static boolean statements_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "statements_1")) return false;
    statements_1_0(builder, level + 1);
    return true;
  }

  // statement (semi+ statement)*
  private static boolean statements_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "statements_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = statement(builder, level + 1);
    result = result && statements_1_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (semi+ statement)*
  private static boolean statements_1_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "statements_1_0_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!statements_1_0_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "statements_1_0_1", pos)) break;
    }
    return true;
  }

  // semi+ statement
  private static boolean statements_1_0_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "statements_1_0_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = statements_1_0_1_0_0(builder, level + 1);
    result = result && statement(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // semi+
  private static boolean statements_1_0_1_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "statements_1_0_1_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = semi(builder, level + 1);
    while (result) {
      int pos = current_position_(builder);
      if (!semi(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "statements_1_0_1_0_0", pos)) break;
    }
    exit_section_(builder, marker, null, result);
    return result;
  }

  // semi*
  private static boolean statements_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "statements_2")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!semi(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "statements_2", pos)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // namedLeftValue (commaSep namedLeftValue)+
  static boolean strictExpressionList(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "strictExpressionList")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = namedLeftValue(builder, level + 1);
    result = result && strictExpressionList_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (commaSep namedLeftValue)+
  private static boolean strictExpressionList_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "strictExpressionList_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = strictExpressionList_1_0(builder, level + 1);
    while (result) {
      int pos = current_position_(builder);
      if (!strictExpressionList_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "strictExpressionList_1", pos)) break;
    }
    exit_section_(builder, marker, null, result);
    return result;
  }

  // commaSep namedLeftValue
  private static boolean strictExpressionList_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "strictExpressionList_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = commaSep(builder, level + 1);
    result = result && namedLeftValue(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // REGULAR_STRING_PART_LITERAL
  public static boolean stringContent(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "stringContent")) return false;
    if (!nextTokenIs(builder, REGULAR_STRING_PART_LITERAL)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, REGULAR_STRING_PART_LITERAL);
    exit_section_(builder, marker, STRING_CONTENT, result);
    return result;
  }

  /* ********************************************************** */
  // stringTrivialElement
  //  | template
  static boolean stringTemplateElement(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "stringTemplateElement")) return false;
    if (!nextTokenIs(builder, "", REGULAR_STRING_PART_LITERAL, SHORT_INTERPOLATE_SYM,
      STRING_ESCAPE, STRING_INTERPOLATE_START, STRING_UNICODE)) return false;
    boolean result;
    result = stringTrivialElement(builder, level + 1);
    if (!result) result = template(builder, level + 1);
    return result;
  }

  /* ********************************************************** */
  // STRING_UNICODE
  //  | STRING_ESCAPE
  //  | stringContent
  static boolean stringTrivialElement(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "stringTrivialElement")) return false;
    if (!nextTokenIs(builder, "", REGULAR_STRING_PART_LITERAL, STRING_ESCAPE, STRING_UNICODE)) return false;
    boolean result;
    result = consumeToken(builder, STRING_UNICODE);
    if (!result) result = consumeToken(builder, STRING_ESCAPE);
    if (!result) result = stringContent(builder, level + 1);
    return result;
  }

  /* ********************************************************** */
  // string | memberAccess
  static boolean stringValue(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "stringValue")) return false;
    boolean result;
    result = string(builder, level + 1);
    if (!result) result = memberAccess(builder, level + 1);
    return result;
  }

  /* ********************************************************** */
  // symbol | macroSymbol
  static boolean symbolAndMacroSymbol(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "symbolAndMacroSymbol")) return false;
    boolean result;
    result = symbol(builder, level + 1);
    if (!result) result = macroSymbol(builder, level + 1);
    return result;
  }

  /* ********************************************************** */
  // interpolateSymbolAsOp | symbol | LEFT_BRACKET symbol RIGHT_BRACKET
  static boolean symbolLhsInternal(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "symbolLhsInternal")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = interpolateSymbolAsOp(builder, level + 1);
    if (!result) result = symbol(builder, level + 1);
    if (!result) result = symbolLhsInternal_2(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // LEFT_BRACKET symbol RIGHT_BRACKET
  private static boolean symbolLhsInternal_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "symbolLhsInternal_2")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, LEFT_BRACKET);
    result = result && symbol(builder, level + 1);
    result = result && consumeToken(builder, RIGHT_BRACKET);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // SHORT_INTERPOLATE_SYM symbol | STRING_INTERPOLATE_START expr STRING_INTERPOLATE_END
  public static boolean template(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "template")) return false;
    if (!nextTokenIs(builder, "<template>", SHORT_INTERPOLATE_SYM, STRING_INTERPOLATE_START)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, TEMPLATE, "<template>");
    result = template_0(builder, level + 1);
    if (!result) result = template_1(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // SHORT_INTERPOLATE_SYM symbol
  private static boolean template_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "template_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, SHORT_INTERPOLATE_SYM);
    result = result && symbol(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // STRING_INTERPOLATE_START expr STRING_INTERPOLATE_END
  private static boolean template_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "template_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, STRING_INTERPOLATE_START);
    result = result && expr(builder, level + 1, -1);
    result = result && consumeToken(builder, STRING_INTERPOLATE_END);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // TRIPLE_QUOTE_START stringTemplateElement* TRIPLE_QUOTE_END
  static boolean tripleQuoteString(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "tripleQuoteString")) return false;
    if (!nextTokenIs(builder, TRIPLE_QUOTE_START)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = consumeToken(builder, TRIPLE_QUOTE_START);
    pinned = result; // pin = 1
    result = result && report_error_(builder, tripleQuoteString_1(builder, level + 1));
    result = pinned && consumeToken(builder, TRIPLE_QUOTE_END) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // stringTemplateElement*
  private static boolean tripleQuoteString_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "tripleQuoteString_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!stringTemplateElement(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "tripleQuoteString_1", pos)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // unaryOpAsSymbol | expr
  static boolean tupleExprs(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "tupleExprs")) return false;
    boolean result;
    result = unaryOpAsSymbol(builder, level + 1);
    if (!result) result = expr(builder, level + 1, -1);
    return result;
  }

  /* ********************************************************** */
  // DOUBLE_COLON endOfLine
  //  symbol (DOT_SYM symbol)* endOfLine
  //  typeParameters?
  public static boolean typeAnnotation(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeAnnotation")) return false;
    if (!nextTokenIs(builder, DOUBLE_COLON)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, DOUBLE_COLON);
    result = result && endOfLine(builder, level + 1);
    result = result && symbol(builder, level + 1);
    result = result && typeAnnotation_3(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && typeAnnotation_5(builder, level + 1);
    exit_section_(builder, marker, TYPE_ANNOTATION, result);
    return result;
  }

  // (DOT_SYM symbol)*
  private static boolean typeAnnotation_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeAnnotation_3")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!typeAnnotation_3_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "typeAnnotation_3", pos)) break;
    }
    return true;
  }

  // DOT_SYM symbol
  private static boolean typeAnnotation_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeAnnotation_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, DOT_SYM);
    result = result && symbol(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // typeParameters?
  private static boolean typeAnnotation_5(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeAnnotation_5")) return false;
    typeParameters(builder, level + 1);
    return true;
  }

  /* ********************************************************** */
  // LEFT_BRACKET typeAnnotation RIGHT_BRACKET
  static boolean typeAnnotationAsOp(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeAnnotationAsOp")) return false;
    if (!nextTokenIs(builder, LEFT_BRACKET)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, LEFT_BRACKET);
    result = result && typeAnnotation(builder, level + 1);
    result = result && consumeToken(builder, RIGHT_BRACKET);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // (typeModifiers? typeKeywords | typeModifiers) endOfLine
  //   symbol typeParameters? (SUBTYPE_SYM endOfLine expr)? SEMICOLON_SYM? endOfLine
  //   <<lazyBlockNotParseEndImpl>>
  //  END_KEYWORD
  public static boolean typeDeclaration(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeDeclaration")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, TYPE_DECLARATION, "<type declaration>");
    result = typeDeclaration_0(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && symbol(builder, level + 1);
    result = result && typeDeclaration_3(builder, level + 1);
    result = result && typeDeclaration_4(builder, level + 1);
    result = result && typeDeclaration_5(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && lazyBlockNotParseEndImpl(builder, level + 1);
    result = result && consumeToken(builder, END_KEYWORD);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // typeModifiers? typeKeywords | typeModifiers
  private static boolean typeDeclaration_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeDeclaration_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = typeDeclaration_0_0(builder, level + 1);
    if (!result) result = typeModifiers(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // typeModifiers? typeKeywords
  private static boolean typeDeclaration_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeDeclaration_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = typeDeclaration_0_0_0(builder, level + 1);
    result = result && typeKeywords(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // typeModifiers?
  private static boolean typeDeclaration_0_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeDeclaration_0_0_0")) return false;
    typeModifiers(builder, level + 1);
    return true;
  }

  // typeParameters?
  private static boolean typeDeclaration_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeDeclaration_3")) return false;
    typeParameters(builder, level + 1);
    return true;
  }

  // (SUBTYPE_SYM endOfLine expr)?
  private static boolean typeDeclaration_4(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeDeclaration_4")) return false;
    typeDeclaration_4_0(builder, level + 1);
    return true;
  }

  // SUBTYPE_SYM endOfLine expr
  private static boolean typeDeclaration_4_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeDeclaration_4_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, SUBTYPE_SYM);
    result = result && endOfLine(builder, level + 1);
    result = result && expr(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // SEMICOLON_SYM?
  private static boolean typeDeclaration_5(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeDeclaration_5")) return false;
    consumeToken(builder, SEMICOLON_SYM);
    return true;
  }

  /* ********************************************************** */
  // "type" | STRUCT_KEYWORD
  static boolean typeKeywords(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeKeywords")) return false;
    boolean result;
    result = consumeToken(builder, "type");
    if (!result) result = consumeToken(builder, STRUCT_KEYWORD);
    return result;
  }

  /* ********************************************************** */
  // IMMUTABLE_KEYWORD | MUTABLE_KEYWORD
  static boolean typeModifiers(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeModifiers")) return false;
    if (!nextTokenIs(builder, "", IMMUTABLE_KEYWORD, MUTABLE_KEYWORD)) return false;
    boolean result;
    result = consumeToken(builder, IMMUTABLE_KEYWORD);
    if (!result) result = consumeToken(builder, MUTABLE_KEYWORD);
    return result;
  }

  /* ********************************************************** */
  // unarySubtypeOp
  // | quoteOp
  // | interpolateSymbolAsOp
  // | exprInterpolateOp
  // | opAsSymbol
  // | unaryPlusOp
  // | expr
  static boolean typeParameterSymbol(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeParameterSymbol")) return false;
    boolean result;
    result = unarySubtypeOp(builder, level + 1);
    if (!result) result = quoteOp(builder, level + 1);
    if (!result) result = interpolateSymbolAsOp(builder, level + 1);
    if (!result) result = exprInterpolateOp(builder, level + 1);
    if (!result) result = opAsSymbol(builder, level + 1);
    if (!result) result = unaryPlusOp(builder, level + 1);
    if (!result) result = expr(builder, level + 1, -1);
    return result;
  }

  /* ********************************************************** */
  // LEFT_B_BRACKET endOfLine
  //   ((typeParameterSymbol whereClause?)? (commaSep (typeParameterSymbol whereClause?)?)* endOfLine)
  //  RIGHT_B_BRACKET
  public static boolean typeParameters(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeParameters")) return false;
    if (!nextTokenIs(builder, LEFT_B_BRACKET)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, TYPE_PARAMETERS, null);
    result = consumeToken(builder, LEFT_B_BRACKET);
    pinned = result; // pin = 1
    result = result && report_error_(builder, endOfLine(builder, level + 1));
    result = pinned && report_error_(builder, typeParameters_2(builder, level + 1)) && result;
    result = pinned && consumeToken(builder, RIGHT_B_BRACKET) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // (typeParameterSymbol whereClause?)? (commaSep (typeParameterSymbol whereClause?)?)* endOfLine
  private static boolean typeParameters_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeParameters_2")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = typeParameters_2_0(builder, level + 1);
    result = result && typeParameters_2_1(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (typeParameterSymbol whereClause?)?
  private static boolean typeParameters_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeParameters_2_0")) return false;
    typeParameters_2_0_0(builder, level + 1);
    return true;
  }

  // typeParameterSymbol whereClause?
  private static boolean typeParameters_2_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeParameters_2_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = typeParameterSymbol(builder, level + 1);
    result = result && typeParameters_2_0_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // whereClause?
  private static boolean typeParameters_2_0_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeParameters_2_0_0_1")) return false;
    whereClause(builder, level + 1);
    return true;
  }

  // (commaSep (typeParameterSymbol whereClause?)?)*
  private static boolean typeParameters_2_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeParameters_2_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!typeParameters_2_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "typeParameters_2_1", pos)) break;
    }
    return true;
  }

  // commaSep (typeParameterSymbol whereClause?)?
  private static boolean typeParameters_2_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeParameters_2_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = commaSep(builder, level + 1);
    result = result && typeParameters_2_1_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (typeParameterSymbol whereClause?)?
  private static boolean typeParameters_2_1_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeParameters_2_1_0_1")) return false;
    typeParameters_2_1_0_1_0(builder, level + 1);
    return true;
  }

  // typeParameterSymbol whereClause?
  private static boolean typeParameters_2_1_0_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeParameters_2_1_0_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = typeParameterSymbol(builder, level + 1);
    result = result && typeParameters_2_1_0_1_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // whereClause?
  private static boolean typeParameters_2_1_0_1_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeParameters_2_1_0_1_0_1")) return false;
    whereClause(builder, level + 1);
    return true;
  }

  /* ********************************************************** */
  // (
  //    symbol typeAnnotation?
  //  | symbol? typeAnnotation
  //  | applyMacroOp
  //  ) (SLICE_SYM|(endOfLine EQ_SYM endOfLine expr))?
  public static boolean typedNamedVariable(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typedNamedVariable")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, TYPED_NAMED_VARIABLE, "<typed named variable>");
    result = typedNamedVariable_0(builder, level + 1);
    result = result && typedNamedVariable_1(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // symbol typeAnnotation?
  //  | symbol? typeAnnotation
  //  | applyMacroOp
  private static boolean typedNamedVariable_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typedNamedVariable_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = typedNamedVariable_0_0(builder, level + 1);
    if (!result) result = typedNamedVariable_0_1(builder, level + 1);
    if (!result) result = applyMacroOp(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // symbol typeAnnotation?
  private static boolean typedNamedVariable_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typedNamedVariable_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = symbol(builder, level + 1);
    result = result && typedNamedVariable_0_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // typeAnnotation?
  private static boolean typedNamedVariable_0_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typedNamedVariable_0_0_1")) return false;
    typeAnnotation(builder, level + 1);
    return true;
  }

  // symbol? typeAnnotation
  private static boolean typedNamedVariable_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typedNamedVariable_0_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = typedNamedVariable_0_1_0(builder, level + 1);
    result = result && typeAnnotation(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // symbol?
  private static boolean typedNamedVariable_0_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typedNamedVariable_0_1_0")) return false;
    symbol(builder, level + 1);
    return true;
  }

  // (SLICE_SYM|(endOfLine EQ_SYM endOfLine expr))?
  private static boolean typedNamedVariable_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typedNamedVariable_1")) return false;
    typedNamedVariable_1_0(builder, level + 1);
    return true;
  }

  // SLICE_SYM|(endOfLine EQ_SYM endOfLine expr)
  private static boolean typedNamedVariable_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typedNamedVariable_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, SLICE_SYM);
    if (!result) result = typedNamedVariable_1_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // endOfLine EQ_SYM endOfLine expr
  private static boolean typedNamedVariable_1_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typedNamedVariable_1_0_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = endOfLine(builder, level + 1);
    result = result && consumeToken(builder, EQ_SYM);
    result = result && endOfLine(builder, level + 1);
    result = result && expr(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // privateUnaryOpAsSymbol
  public static boolean unaryOpAsSymbol(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "unaryOpAsSymbol")) return false;
    if (!nextTokenIs(builder, "<unary op as symbol>", INTERPOLATE_SYM, MINUS_SYM, NOT_SYM, PLUS_SYM)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, UNARY_OP_AS_SYMBOL, "<unary op as symbol>");
    result = privateUnaryOpAsSymbol(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // LEFT_BRACKET endOfLine
  //   (expr (commaSep expr)*)? endOfLine
  //  RIGHT_BRACKET
  public static boolean untypedVariables(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "untypedVariables")) return false;
    if (!nextTokenIs(builder, LEFT_BRACKET)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, LEFT_BRACKET);
    result = result && endOfLine(builder, level + 1);
    result = result && untypedVariables_2(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && consumeToken(builder, RIGHT_BRACKET);
    exit_section_(builder, marker, UNTYPED_VARIABLES, result);
    return result;
  }

  // (expr (commaSep expr)*)?
  private static boolean untypedVariables_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "untypedVariables_2")) return false;
    untypedVariables_2_0(builder, level + 1);
    return true;
  }

  // expr (commaSep expr)*
  private static boolean untypedVariables_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "untypedVariables_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = expr(builder, level + 1, -1);
    result = result && untypedVariables_2_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (commaSep expr)*
  private static boolean untypedVariables_2_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "untypedVariables_2_0_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!untypedVariables_2_0_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "untypedVariables_2_0_1", pos)) break;
    }
    return true;
  }

  // commaSep expr
  private static boolean untypedVariables_2_0_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "untypedVariables_2_0_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = commaSep(builder, level + 1);
    result = result && expr(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // primaryExpr typeParameters?
  public static boolean userType(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "userType")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, USER_TYPE, "<user type>");
    result = expr(builder, level + 1, 21);
    result = result && userType_1(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // typeParameters?
  private static boolean userType_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "userType_1")) return false;
    typeParameters(builder, level + 1);
    return true;
  }

  /* ********************************************************** */
  // WHERE_KEYWORD afterWhere
  public static boolean whereClause(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "whereClause")) return false;
    if (!nextTokenIs(builder, WHERE_KEYWORD)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, WHERE_KEYWORD);
    result = result && afterWhere(builder, level + 1);
    exit_section_(builder, marker, WHERE_CLAUSE, result);
    return result;
  }

  /* ********************************************************** */
  // <<lazyBlockNotParseEndImpl>>
  static boolean whileLazyBlock(PsiBuilder builder, int level) {
    return lazyBlockNotParseEndImpl(builder, level + 1);
  }

  /* ********************************************************** */
  // Expression root: expr
  // Operator priority table:
  // 0: ATOM(compactFunction)
  // 1: ATOM(globalStatement)
  // 2: ATOM(applyMacroOp)
  // 3: POSTFIX(assignOp) POSTFIX(assignLevelOp)
  // 4: BINARY(arrowOp)
  // 5: BINARY(ternaryOp)
  // 6: POSTFIX(spliceOp) ATOM(quoteOp) ATOM(compoundQuoteOp)
  // 7: ATOM(lambda)
  // 8: BINARY(miscArrowsOp)
  // 9: BINARY(orOp)
  // 10: BINARY(andOp)
  // 11: BINARY(comparisonLevelOp)
  // 12: BINARY(pipeLevelOp)
  // 13: BINARY(plusLevelOp)
  // 14: BINARY(bitwiseLevelOp) BINARY(rangeOp) POSTFIX(transposeOp)
  // 15: BINARY(multiplyLevelOp) ATOM(implicitMultiplyOp) ATOM(stringLikeMultiplyOp) BINARY(dotApplyFunctionOp)
  // 16: BINARY(fractionOp) BINARY(inOp) BINARY(isaOp)
  // 17: BINARY(miscExponentOp) BINARY(exponentOp)
  // 18: BINARY(typeOp)
  // 19: PREFIX(unaryPlusOp) PREFIX(unaryMinusOp) PREFIX(unaryTypeOp) PREFIX(unarySubtypeOp)
  //    ATOM(exprInterpolateOp) PREFIX(unaryInterpolateOp) PREFIX(notOp) PREFIX(bitWiseNotOp)
  // 20: ATOM(array)
  // 21: POSTFIX(applyFunctionOp) POSTFIX(applyIndexOp) BINARY(memberAccessOp) POSTFIX(type)
  // 22: ATOM(string) ATOM(command) ATOM(regex) ATOM(rawString)
  //    ATOM(versionNumber) ATOM(byteArray) ATOM(charLit) ATOM(integer)
  //    ATOM(floatLit) ATOM(booleanLit) ATOM(ifExpr) ATOM(forExpr)
  //    ATOM(forComprehension) ATOM(whileExpr) ATOM(function) ATOM(returnExpr)
  //    ATOM(breakExpr) ATOM(tryCatch) ATOM(continueExpr) ATOM(tuple)
  //    ATOM(macro) ATOM(let) ATOM(beginBlock) ATOM(colonBlock)
  //    ATOM(export) ATOM(importExpr) ATOM(importAllExpr) ATOM(using)
  //    ATOM(primitiveTypeDeclaration) PREFIX(typeAlias) ATOM(symbolLhs) ATOM(symbol)
  //    ATOM(macroSymbol) ATOM(bracketedExpr) ATOM(bracketedComprehensionExpr)
  public static boolean expr(PsiBuilder builder, int level, int priority) {
    if (!recursion_guard_(builder, level, "expr")) return false;
    addVariant(builder, "<expr>");
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, "<expr>");
    result = compactFunction(builder, level + 1);
    if (!result) result = globalStatement(builder, level + 1);
    if (!result) result = applyMacroOp(builder, level + 1);
    if (!result) result = quoteOp(builder, level + 1);
    if (!result) result = compoundQuoteOp(builder, level + 1);
    if (!result) result = lambda(builder, level + 1);
    if (!result) result = implicitMultiplyOp(builder, level + 1);
    if (!result) result = stringLikeMultiplyOp(builder, level + 1);
    if (!result) result = unaryPlusOp(builder, level + 1);
    if (!result) result = unaryMinusOp(builder, level + 1);
    if (!result) result = unaryTypeOp(builder, level + 1);
    if (!result) result = unarySubtypeOp(builder, level + 1);
    if (!result) result = exprInterpolateOp(builder, level + 1);
    if (!result) result = unaryInterpolateOp(builder, level + 1);
    if (!result) result = notOp(builder, level + 1);
    if (!result) result = bitWiseNotOp(builder, level + 1);
    if (!result) result = array(builder, level + 1);
    if (!result) result = string(builder, level + 1);
    if (!result) result = command(builder, level + 1);
    if (!result) result = regex(builder, level + 1);
    if (!result) result = rawString(builder, level + 1);
    if (!result) result = versionNumber(builder, level + 1);
    if (!result) result = byteArray(builder, level + 1);
    if (!result) result = charLit(builder, level + 1);
    if (!result) result = integer(builder, level + 1);
    if (!result) result = floatLit(builder, level + 1);
    if (!result) result = booleanLit(builder, level + 1);
    if (!result) result = ifExpr(builder, level + 1);
    if (!result) result = forExpr(builder, level + 1);
    if (!result) result = forComprehension(builder, level + 1);
    if (!result) result = whileExpr(builder, level + 1);
    if (!result) result = function(builder, level + 1);
    if (!result) result = returnExpr(builder, level + 1);
    if (!result) result = breakExpr(builder, level + 1);
    if (!result) result = tryCatch(builder, level + 1);
    if (!result) result = continueExpr(builder, level + 1);
    if (!result) result = tuple(builder, level + 1);
    if (!result) result = macro(builder, level + 1);
    if (!result) result = let(builder, level + 1);
    if (!result) result = beginBlock(builder, level + 1);
    if (!result) result = colonBlock(builder, level + 1);
    if (!result) result = export(builder, level + 1);
    if (!result) result = importExpr(builder, level + 1);
    if (!result) result = importAllExpr(builder, level + 1);
    if (!result) result = using(builder, level + 1);
    if (!result) result = primitiveTypeDeclaration(builder, level + 1);
    if (!result) result = typeAlias(builder, level + 1);
    if (!result) result = symbolLhs(builder, level + 1);
    if (!result) result = symbol(builder, level + 1);
    if (!result) result = macroSymbol(builder, level + 1);
    if (!result) result = bracketedExpr(builder, level + 1);
    if (!result) result = bracketedComprehensionExpr(builder, level + 1);
    pinned = result;
    result = result && expr_0(builder, level + 1, priority);
    exit_section_(builder, level, marker, null, result, pinned, null);
    return result || pinned;
  }

  public static boolean expr_0(PsiBuilder builder, int level, int priority) {
    if (!recursion_guard_(builder, level, "expr_0")) return false;
    boolean result = true;
    while (true) {
      Marker marker = enter_section_(builder, level, _LEFT_, null);
      if (priority < 3 && assignOp_0(builder, level + 1)) {
        result = true;
        exit_section_(builder, level, marker, ASSIGN_OP, result, true, null);
      }
      else if (priority < 3 && assignLevelOp_0(builder, level + 1)) {
        result = true;
        exit_section_(builder, level, marker, ASSIGN_LEVEL_OP, result, true, null);
      }
      else if (priority < 4 && arrowOp_0(builder, level + 1)) {
        result = expr(builder, level, 3);
        exit_section_(builder, level, marker, ARROW_OP, result, true, null);
      }
      else if (priority < 5 && ternaryOp_0(builder, level + 1)) {
        result = expr(builder, level, 13);
        exit_section_(builder, level, marker, TERNARY_OP, result, true, null);
      }
      else if (priority < 6 && consumeTokenSmart(builder, SLICE_SYM)) {
        result = true;
        exit_section_(builder, level, marker, SPLICE_OP, result, true, null);
      }
      else if (priority < 8 && miscArrowsOp_0(builder, level + 1)) {
        result = expr(builder, level, 8);
        exit_section_(builder, level, marker, MISC_ARROWS_OP, result, true, null);
      }
      else if (priority < 9 && orOp_0(builder, level + 1)) {
        result = expr(builder, level, 9);
        exit_section_(builder, level, marker, OR_OP, result, true, null);
      }
      else if (priority < 10 && andOp_0(builder, level + 1)) {
        result = expr(builder, level, 10);
        exit_section_(builder, level, marker, AND_OP, result, true, null);
      }
      else if (priority < 11 && comparisonLevelOp_0(builder, level + 1)) {
        result = expr(builder, level, 11);
        exit_section_(builder, level, marker, COMPARISON_LEVEL_OP, result, true, null);
      }
      else if (priority < 12 && pipeLevelOp_0(builder, level + 1)) {
        result = expr(builder, level, 12);
        exit_section_(builder, level, marker, PIPE_LEVEL_OP, result, true, null);
      }
      else if (priority < 13 && plusLevelOp_0(builder, level + 1)) {
        result = expr(builder, level, 13);
        exit_section_(builder, level, marker, PLUS_LEVEL_OP, result, true, null);
      }
      else if (priority < 14 && bitwiseLevelOp_0(builder, level + 1)) {
        result = expr(builder, level, 14);
        exit_section_(builder, level, marker, BITWISE_LEVEL_OP, result, true, null);
      }
      else if (priority < 14 && rangeOp_0(builder, level + 1)) {
        result = expr(builder, level, 14);
        exit_section_(builder, level, marker, RANGE_OP, result, true, null);
      }
      else if (priority < 14 && consumeTokenSmart(builder, TRANSPOSE_SYM)) {
        result = true;
        exit_section_(builder, level, marker, TRANSPOSE_OP, result, true, null);
      }
      else if (priority < 15 && multiplyLevelOp_0(builder, level + 1)) {
        result = expr(builder, level, 15);
        exit_section_(builder, level, marker, MULTIPLY_LEVEL_OP, result, true, null);
      }
      else if (priority < 15 && dotApplyFunctionOp_0(builder, level + 1)) {
        result = expr(builder, level, 14);
        exit_section_(builder, level, marker, DOT_APPLY_FUNCTION_OP, result, true, null);
      }
      else if (priority < 16 && fractionOp_0(builder, level + 1)) {
        result = expr(builder, level, 16);
        exit_section_(builder, level, marker, FRACTION_OP, result, true, null);
      }
      else if (priority < 16 && inOp_0(builder, level + 1)) {
        result = expr(builder, level, 16);
        exit_section_(builder, level, marker, IN_OP, result, true, null);
      }
      else if (priority < 16 && consumeTokenSmart(builder, ISA_KEYWORD)) {
        result = expr(builder, level, 16);
        exit_section_(builder, level, marker, ISA_OP, result, true, null);
      }
      else if (priority < 17 && miscExponentOp_0(builder, level + 1)) {
        result = expr(builder, level, 17);
        exit_section_(builder, level, marker, MISC_EXPONENT_OP, result, true, null);
      }
      else if (priority < 17 && exponentOp_0(builder, level + 1)) {
        result = expr(builder, level, 17);
        exit_section_(builder, level, marker, EXPONENT_OP, result, true, null);
      }
      else if (priority < 18 && typeOp_0(builder, level + 1)) {
        result = expr(builder, level, 20);
        exit_section_(builder, level, marker, TYPE_OP, result, true, null);
      }
      else if (priority < 21 && applyFunctionOp_0(builder, level + 1)) {
        result = true;
        exit_section_(builder, level, marker, APPLY_FUNCTION_OP, result, true, null);
      }
      else if (priority < 21 && applyIndexOp_0(builder, level + 1)) {
        result = true;
        exit_section_(builder, level, marker, APPLY_INDEX_OP, result, true, null);
      }
      else if (priority < 21 && consumeTokenSmart(builder, DOT_SYM)) {
        result = expr(builder, level, 20);
        exit_section_(builder, level, marker, MEMBER_ACCESS_OP, result, true, null);
      }
      else if (priority < 21 && type_0(builder, level + 1)) {
        result = true;
        exit_section_(builder, level, marker, TYPE, result, true, null);
      }
      else {
        exit_section_(builder, level, marker, null, false, false, null);
        break;
      }
    }
    return result;
  }

  // (symbol)
  //  (typeParameters endOfLine)?
  //  (functionSignature endOfLine)
  //  (whereClause endOfLine)*
  //  EQ_SYM endOfLine expressionList
  public static boolean compactFunction(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "compactFunction")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, COMPACT_FUNCTION, "<compact function>");
    result = compactFunction_0(builder, level + 1);
    result = result && compactFunction_1(builder, level + 1);
    result = result && compactFunction_2(builder, level + 1);
    result = result && compactFunction_3(builder, level + 1);
    result = result && consumeToken(builder, EQ_SYM);
    pinned = result; // pin = 5
    result = result && report_error_(builder, endOfLine(builder, level + 1));
    result = pinned && expressionList(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // (symbol)
  private static boolean compactFunction_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "compactFunction_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = symbol(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (typeParameters endOfLine)?
  private static boolean compactFunction_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "compactFunction_1")) return false;
    compactFunction_1_0(builder, level + 1);
    return true;
  }

  // typeParameters endOfLine
  private static boolean compactFunction_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "compactFunction_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = typeParameters(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // functionSignature endOfLine
  private static boolean compactFunction_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "compactFunction_2")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = functionSignature(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (whereClause endOfLine)*
  private static boolean compactFunction_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "compactFunction_3")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!compactFunction_3_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "compactFunction_3", pos)) break;
    }
    return true;
  }

  // whereClause endOfLine
  private static boolean compactFunction_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "compactFunction_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = whereClause(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // GLOBAL_KEYWORD endOfLine expressionList
  public static boolean globalStatement(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "globalStatement")) return false;
    if (!nextTokenIsSmart(builder, GLOBAL_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, GLOBAL_STATEMENT, null);
    result = consumeTokenSmart(builder, GLOBAL_KEYWORD);
    pinned = result; // pin = 1
    result = result && report_error_(builder, endOfLine(builder, level + 1));
    result = pinned && expressionList(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // macroSymbol expr expr*
  public static boolean applyMacroOp(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyMacroOp")) return false;
    if (!nextTokenIsSmart(builder, MACRO_SYM)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _COLLAPSE_, APPLY_MACRO_OP, null);
    result = macroSymbol(builder, level + 1);
    result = result && expr(builder, level + 1, -1);
    result = result && applyMacroOp_2(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // expr*
  private static boolean applyMacroOp_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyMacroOp_2")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!expr(builder, level + 1, -1)) break;
      if (!empty_element_parsed_guard_(builder, "applyMacroOp_2", pos)) break;
    }
    return true;
  }

  // commaSep? (EQ_SYM | ASSIGN_SYM) endOfLine statementEvenStupid
  private static boolean assignOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "assignOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = assignOp_0_0(builder, level + 1);
    result = result && assignOp_0_1(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && statementEvenStupid(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // commaSep?
  private static boolean assignOp_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "assignOp_0_0")) return false;
    commaSep(builder, level + 1);
    return true;
  }

  // EQ_SYM | ASSIGN_SYM
  private static boolean assignOp_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "assignOp_0_1")) return false;
    boolean result;
    result = consumeTokenSmart(builder, EQ_SYM);
    if (!result) result = consumeTokenSmart(builder, ASSIGN_SYM);
    return result;
  }

  // assignLevelOperator endOfLine expressionList
  private static boolean assignLevelOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "assignLevelOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = assignLevelOperator(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && expressionList(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ARROW_SYM endOfLine
  private static boolean arrowOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "arrowOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, ARROW_SYM);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // QUESTION_SYM endOfLine
  private static boolean ternaryOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "ternaryOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, QUESTION_SYM);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // COLON_SYM quotable
  public static boolean quoteOp(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "quoteOp")) return false;
    if (!nextTokenIsSmart(builder, COLON_SYM)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, COLON_SYM);
    result = result && quotable(builder, level + 1);
    exit_section_(builder, marker, QUOTE_OP, result);
    return result;
  }

  // QUOTE_KEYWORD endOfLine (statementEvenStupid endOfLine)* END_KEYWORD
  public static boolean compoundQuoteOp(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "compoundQuoteOp")) return false;
    if (!nextTokenIsSmart(builder, QUOTE_KEYWORD)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, QUOTE_KEYWORD);
    result = result && endOfLine(builder, level + 1);
    result = result && compoundQuoteOp_2(builder, level + 1);
    result = result && consumeToken(builder, END_KEYWORD);
    exit_section_(builder, marker, COMPOUND_QUOTE_OP, result);
    return result;
  }

  // (statementEvenStupid endOfLine)*
  private static boolean compoundQuoteOp_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "compoundQuoteOp_2")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!compoundQuoteOp_2_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "compoundQuoteOp_2", pos)) break;
    }
    return true;
  }

  // statementEvenStupid endOfLine
  private static boolean compoundQuoteOp_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "compoundQuoteOp_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = statementEvenStupid(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (symbol typeAnnotation? | array | functionSignature ) endOfLine
  //   LAMBDA_ABSTRACTION endOfLine expr
  public static boolean lambda(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "lambda")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _COLLAPSE_, LAMBDA, "<lambda>");
    result = lambda_0(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && consumeToken(builder, LAMBDA_ABSTRACTION);
    pinned = result; // pin = 3
    result = result && report_error_(builder, endOfLine(builder, level + 1));
    result = pinned && expr(builder, level + 1, -1) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // symbol typeAnnotation? | array | functionSignature
  private static boolean lambda_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "lambda_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = lambda_0_0(builder, level + 1);
    if (!result) result = array(builder, level + 1);
    if (!result) result = functionSignature(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // symbol typeAnnotation?
  private static boolean lambda_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "lambda_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = symbol(builder, level + 1);
    result = result && lambda_0_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // typeAnnotation?
  private static boolean lambda_0_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "lambda_0_0_1")) return false;
    typeAnnotation(builder, level + 1);
    return true;
  }

  // (SPECIAL_ARROW_SYM | MISC_ARROW_SYM) endOfLine
  private static boolean miscArrowsOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "miscArrowsOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = miscArrowsOp_0_0(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // SPECIAL_ARROW_SYM | MISC_ARROW_SYM
  private static boolean miscArrowsOp_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "miscArrowsOp_0_0")) return false;
    boolean result;
    result = consumeTokenSmart(builder, SPECIAL_ARROW_SYM);
    if (!result) result = consumeTokenSmart(builder, MISC_ARROW_SYM);
    return result;
  }

  // endOfLine OR_SYM endOfLine
  private static boolean orOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "orOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = endOfLine(builder, level + 1);
    result = result && consumeToken(builder, OR_SYM);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // endOfLine AND_SYM endOfLine
  private static boolean andOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "andOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = endOfLine(builder, level + 1);
    result = result && consumeToken(builder, AND_SYM);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // comparisonLevelOperator endOfLine
  private static boolean comparisonLevelOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "comparisonLevelOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = comparisonLevelOperator(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // pipeLevelOperator endOfLine
  private static boolean pipeLevelOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "pipeLevelOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = pipeLevelOperator(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // plusLevelOperator endOfLine
  private static boolean plusLevelOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "plusLevelOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = plusLevelOperator(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // bitwiseLevelOperator endOfLine
  private static boolean bitwiseLevelOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "bitwiseLevelOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = bitwiseLevelOperator(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // COLON_SYM endOfLine
  private static boolean rangeOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rangeOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, COLON_SYM);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // multiplyLevelOperator endOfLine
  private static boolean multiplyLevelOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "multiplyLevelOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = multiplyLevelOperator(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // specialLhs blockComment? IMPLICIT_MULTIPLY_SYM specialRhs
  public static boolean implicitMultiplyOp(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "implicitMultiplyOp")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _COLLAPSE_, IMPLICIT_MULTIPLY_OP, "<implicit multiply op>");
    result = specialLhs(builder, level + 1);
    result = result && implicitMultiplyOp_1(builder, level + 1);
    result = result && consumeToken(builder, IMPLICIT_MULTIPLY_SYM);
    result = result && specialRhs(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // blockComment?
  private static boolean implicitMultiplyOp_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "implicitMultiplyOp_1")) return false;
    blockComment(builder, level + 1);
    return true;
  }

  // stringValue (endOfLine MULTIPLY_SYM endOfLine stringValue)+
  public static boolean stringLikeMultiplyOp(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "stringLikeMultiplyOp")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _COLLAPSE_, STRING_LIKE_MULTIPLY_OP, "<string like multiply op>");
    result = stringValue(builder, level + 1);
    result = result && stringLikeMultiplyOp_1(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // (endOfLine MULTIPLY_SYM endOfLine stringValue)+
  private static boolean stringLikeMultiplyOp_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "stringLikeMultiplyOp_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = stringLikeMultiplyOp_1_0(builder, level + 1);
    while (result) {
      int pos = current_position_(builder);
      if (!stringLikeMultiplyOp_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "stringLikeMultiplyOp_1", pos)) break;
    }
    exit_section_(builder, marker, null, result);
    return result;
  }

  // endOfLine MULTIPLY_SYM endOfLine stringValue
  private static boolean stringLikeMultiplyOp_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "stringLikeMultiplyOp_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = endOfLine(builder, level + 1);
    result = result && consumeToken(builder, MULTIPLY_SYM);
    result = result && endOfLine(builder, level + 1);
    result = result && stringValue(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // DOT_SYM privateOpSymbols
  private static boolean dotApplyFunctionOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "dotApplyFunctionOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, DOT_SYM);
    result = result && privateOpSymbols(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // FRACTION_SYM endOfLine
  private static boolean fractionOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "fractionOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, FRACTION_SYM);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (IN_KEYWORD | IN_SYM) endOfLine
  private static boolean inOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "inOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = inOp_0_0(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // IN_KEYWORD | IN_SYM
  private static boolean inOp_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "inOp_0_0")) return false;
    boolean result;
    result = consumeTokenSmart(builder, IN_KEYWORD);
    if (!result) result = consumeTokenSmart(builder, IN_SYM);
    return result;
  }

  // MISC_EXPONENT_SYM endOfLine
  private static boolean miscExponentOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "miscExponentOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, MISC_EXPONENT_SYM);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // EXPONENT_SYM endOfLine
  private static boolean exponentOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "exponentOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, EXPONENT_SYM);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // DOUBLE_COLON endOfLine
  private static boolean typeOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, DOUBLE_COLON);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  public static boolean unaryPlusOp(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "unaryPlusOp")) return false;
    if (!nextTokenIsSmart(builder, PLUS_SYM)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, null);
    result = unaryPlusOp_0(builder, level + 1);
    pinned = result;
    result = pinned && expr(builder, level, 19);
    exit_section_(builder, level, marker, UNARY_PLUS_OP, result, pinned, null);
    return result || pinned;
  }

  // PLUS_SYM endOfLine
  private static boolean unaryPlusOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "unaryPlusOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, PLUS_SYM);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  public static boolean unaryMinusOp(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "unaryMinusOp")) return false;
    if (!nextTokenIsSmart(builder, MINUS_SYM)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, null);
    result = unaryMinusOp_0(builder, level + 1);
    pinned = result;
    result = pinned && expr(builder, level, 19);
    exit_section_(builder, level, marker, UNARY_MINUS_OP, result, pinned, null);
    return result || pinned;
  }

  // MINUS_SYM endOfLine
  private static boolean unaryMinusOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "unaryMinusOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, MINUS_SYM);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  public static boolean unaryTypeOp(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "unaryTypeOp")) return false;
    if (!nextTokenIsSmart(builder, DOUBLE_COLON)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, null);
    result = unaryTypeOp_0(builder, level + 1);
    pinned = result;
    result = pinned && expr(builder, level, 19);
    exit_section_(builder, level, marker, UNARY_TYPE_OP, result, pinned, null);
    return result || pinned;
  }

  // DOUBLE_COLON endOfLine
  private static boolean unaryTypeOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "unaryTypeOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, DOUBLE_COLON);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  public static boolean unarySubtypeOp(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "unarySubtypeOp")) return false;
    if (!nextTokenIsSmart(builder, SUBTYPE_SYM)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, null);
    result = unarySubtypeOp_0(builder, level + 1);
    pinned = result;
    result = pinned && expr(builder, level, 19);
    exit_section_(builder, level, marker, UNARY_SUBTYPE_OP, result, pinned, null);
    return result || pinned;
  }

  // SUBTYPE_SYM endOfLine
  private static boolean unarySubtypeOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "unarySubtypeOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, SUBTYPE_SYM);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // privateExprInterpolateOp
  public static boolean exprInterpolateOp(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "exprInterpolateOp")) return false;
    if (!nextTokenIsSmart(builder, EXPR_INTERPOLATE_START)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = privateExprInterpolateOp(builder, level + 1);
    exit_section_(builder, marker, EXPR_INTERPOLATE_OP, result);
    return result;
  }

  public static boolean unaryInterpolateOp(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "unaryInterpolateOp")) return false;
    if (!nextTokenIsSmart(builder, INTERPOLATE_SYM)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, null);
    result = unaryInterpolateOp_0(builder, level + 1);
    pinned = result;
    result = pinned && expr(builder, level, 19);
    exit_section_(builder, level, marker, UNARY_INTERPOLATE_OP, result, pinned, null);
    return result || pinned;
  }

  // INTERPOLATE_SYM endOfLine
  private static boolean unaryInterpolateOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "unaryInterpolateOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, INTERPOLATE_SYM);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  public static boolean notOp(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "notOp")) return false;
    if (!nextTokenIsSmart(builder, NOT_SYM)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, null);
    result = notOp_0(builder, level + 1);
    pinned = result;
    result = pinned && expr(builder, level, 19);
    exit_section_(builder, level, marker, NOT_OP, result, pinned, null);
    return result || pinned;
  }

  // NOT_SYM endOfLine
  private static boolean notOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "notOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, NOT_SYM);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  public static boolean bitWiseNotOp(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "bitWiseNotOp")) return false;
    if (!nextTokenIsSmart(builder, BITWISE_NOT_SYM)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, null);
    result = bitWiseNotOp_0(builder, level + 1);
    pinned = result;
    result = pinned && expr(builder, level, 19);
    exit_section_(builder, level, marker, BIT_WISE_NOT_OP, result, pinned, null);
    return result || pinned;
  }

  // BITWISE_NOT_SYM endOfLine
  private static boolean bitWiseNotOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "bitWiseNotOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, BITWISE_NOT_SYM);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (symbol typeParameters? LEFT_M_BRACKET RIGHT_M_BRACKET)
  // // one-element array without typeName,
  // // and the one-element array with typeName will be regarded as applyIndex first.
  //  | (LEFT_M_BRACKET endOfLine expr commaSep? endOfLine RIGHT_M_BRACKET)
  // // common
  //  | (symbol? LEFT_M_BRACKET endOfLine
  //     (
  //      (expr (commaSep|endOfLine expr)+ (SEMICOLON_SYM? endOfLine expr)* COMMA_SYM?)
  //      |
  //      (expr (commaSep|endOfLine expr)* (SEMICOLON_SYM? endOfLine expr)+ SEMICOLON_SYM?)
  //      |
  //      (expr SEMICOLON_SYM)
  //     )?endOfLine
  //  RIGHT_M_BRACKET)
  public static boolean array(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, ARRAY, "<array>");
    result = array_0(builder, level + 1);
    if (!result) result = array_1(builder, level + 1);
    if (!result) result = array_2(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // symbol typeParameters? LEFT_M_BRACKET RIGHT_M_BRACKET
  private static boolean array_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = symbol(builder, level + 1);
    result = result && array_0_1(builder, level + 1);
    result = result && consumeTokensSmart(builder, 0, LEFT_M_BRACKET, RIGHT_M_BRACKET);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // typeParameters?
  private static boolean array_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_0_1")) return false;
    typeParameters(builder, level + 1);
    return true;
  }

  // LEFT_M_BRACKET endOfLine expr commaSep? endOfLine RIGHT_M_BRACKET
  private static boolean array_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, LEFT_M_BRACKET);
    result = result && endOfLine(builder, level + 1);
    result = result && expr(builder, level + 1, -1);
    result = result && array_1_3(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && consumeToken(builder, RIGHT_M_BRACKET);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // commaSep?
  private static boolean array_1_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_1_3")) return false;
    commaSep(builder, level + 1);
    return true;
  }

  // symbol? LEFT_M_BRACKET endOfLine
  //     (
  //      (expr (commaSep|endOfLine expr)+ (SEMICOLON_SYM? endOfLine expr)* COMMA_SYM?)
  //      |
  //      (expr (commaSep|endOfLine expr)* (SEMICOLON_SYM? endOfLine expr)+ SEMICOLON_SYM?)
  //      |
  //      (expr SEMICOLON_SYM)
  //     )?endOfLine
  //  RIGHT_M_BRACKET
  private static boolean array_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = array_2_0(builder, level + 1);
    result = result && consumeToken(builder, LEFT_M_BRACKET);
    result = result && endOfLine(builder, level + 1);
    result = result && array_2_3(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && consumeToken(builder, RIGHT_M_BRACKET);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // symbol?
  private static boolean array_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_0")) return false;
    symbol(builder, level + 1);
    return true;
  }

  // (
  //      (expr (commaSep|endOfLine expr)+ (SEMICOLON_SYM? endOfLine expr)* COMMA_SYM?)
  //      |
  //      (expr (commaSep|endOfLine expr)* (SEMICOLON_SYM? endOfLine expr)+ SEMICOLON_SYM?)
  //      |
  //      (expr SEMICOLON_SYM)
  //     )?
  private static boolean array_2_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_3")) return false;
    array_2_3_0(builder, level + 1);
    return true;
  }

  // (expr (commaSep|endOfLine expr)+ (SEMICOLON_SYM? endOfLine expr)* COMMA_SYM?)
  //      |
  //      (expr (commaSep|endOfLine expr)* (SEMICOLON_SYM? endOfLine expr)+ SEMICOLON_SYM?)
  //      |
  //      (expr SEMICOLON_SYM)
  private static boolean array_2_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = array_2_3_0_0(builder, level + 1);
    if (!result) result = array_2_3_0_1(builder, level + 1);
    if (!result) result = array_2_3_0_2(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // expr (commaSep|endOfLine expr)+ (SEMICOLON_SYM? endOfLine expr)* COMMA_SYM?
  private static boolean array_2_3_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_3_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = expr(builder, level + 1, -1);
    result = result && array_2_3_0_0_1(builder, level + 1);
    result = result && array_2_3_0_0_2(builder, level + 1);
    result = result && array_2_3_0_0_3(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (commaSep|endOfLine expr)+
  private static boolean array_2_3_0_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_3_0_0_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = array_2_3_0_0_1_0(builder, level + 1);
    while (result) {
      int pos = current_position_(builder);
      if (!array_2_3_0_0_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "array_2_3_0_0_1", pos)) break;
    }
    exit_section_(builder, marker, null, result);
    return result;
  }

  // commaSep|endOfLine expr
  private static boolean array_2_3_0_0_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_3_0_0_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = commaSep(builder, level + 1);
    if (!result) result = array_2_3_0_0_1_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // endOfLine expr
  private static boolean array_2_3_0_0_1_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_3_0_0_1_0_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = endOfLine(builder, level + 1);
    result = result && expr(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (SEMICOLON_SYM? endOfLine expr)*
  private static boolean array_2_3_0_0_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_3_0_0_2")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!array_2_3_0_0_2_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "array_2_3_0_0_2", pos)) break;
    }
    return true;
  }

  // SEMICOLON_SYM? endOfLine expr
  private static boolean array_2_3_0_0_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_3_0_0_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = array_2_3_0_0_2_0_0(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && expr(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // SEMICOLON_SYM?
  private static boolean array_2_3_0_0_2_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_3_0_0_2_0_0")) return false;
    consumeTokenSmart(builder, SEMICOLON_SYM);
    return true;
  }

  // COMMA_SYM?
  private static boolean array_2_3_0_0_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_3_0_0_3")) return false;
    consumeTokenSmart(builder, COMMA_SYM);
    return true;
  }

  // expr (commaSep|endOfLine expr)* (SEMICOLON_SYM? endOfLine expr)+ SEMICOLON_SYM?
  private static boolean array_2_3_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_3_0_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = expr(builder, level + 1, -1);
    result = result && array_2_3_0_1_1(builder, level + 1);
    result = result && array_2_3_0_1_2(builder, level + 1);
    result = result && array_2_3_0_1_3(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (commaSep|endOfLine expr)*
  private static boolean array_2_3_0_1_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_3_0_1_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!array_2_3_0_1_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "array_2_3_0_1_1", pos)) break;
    }
    return true;
  }

  // commaSep|endOfLine expr
  private static boolean array_2_3_0_1_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_3_0_1_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = commaSep(builder, level + 1);
    if (!result) result = array_2_3_0_1_1_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // endOfLine expr
  private static boolean array_2_3_0_1_1_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_3_0_1_1_0_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = endOfLine(builder, level + 1);
    result = result && expr(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (SEMICOLON_SYM? endOfLine expr)+
  private static boolean array_2_3_0_1_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_3_0_1_2")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = array_2_3_0_1_2_0(builder, level + 1);
    while (result) {
      int pos = current_position_(builder);
      if (!array_2_3_0_1_2_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "array_2_3_0_1_2", pos)) break;
    }
    exit_section_(builder, marker, null, result);
    return result;
  }

  // SEMICOLON_SYM? endOfLine expr
  private static boolean array_2_3_0_1_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_3_0_1_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = array_2_3_0_1_2_0_0(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && expr(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // SEMICOLON_SYM?
  private static boolean array_2_3_0_1_2_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_3_0_1_2_0_0")) return false;
    consumeTokenSmart(builder, SEMICOLON_SYM);
    return true;
  }

  // SEMICOLON_SYM?
  private static boolean array_2_3_0_1_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_3_0_1_3")) return false;
    consumeTokenSmart(builder, SEMICOLON_SYM);
    return true;
  }

  // expr SEMICOLON_SYM
  private static boolean array_2_3_0_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "array_2_3_0_2")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = expr(builder, level + 1, -1);
    result = result && consumeToken(builder, SEMICOLON_SYM);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // DOT_SYM? LEFT_BRACKET endOfLine (
  //     comprehensionElement+
  //   | (
  //      (expressionList endOfLine)?
  //      commaSep?
  //      commaSep?
  //      arguments*
  //     )
  //   )? endOfLine RIGHT_BRACKET doBlock?
  private static boolean applyFunctionOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyFunctionOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = applyFunctionOp_0_0(builder, level + 1);
    result = result && consumeToken(builder, LEFT_BRACKET);
    result = result && endOfLine(builder, level + 1);
    result = result && applyFunctionOp_0_3(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && consumeToken(builder, RIGHT_BRACKET);
    result = result && applyFunctionOp_0_6(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // DOT_SYM?
  private static boolean applyFunctionOp_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyFunctionOp_0_0")) return false;
    consumeTokenSmart(builder, DOT_SYM);
    return true;
  }

  // (
  //     comprehensionElement+
  //   | (
  //      (expressionList endOfLine)?
  //      commaSep?
  //      commaSep?
  //      arguments*
  //     )
  //   )?
  private static boolean applyFunctionOp_0_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyFunctionOp_0_3")) return false;
    applyFunctionOp_0_3_0(builder, level + 1);
    return true;
  }

  // comprehensionElement+
  //   | (
  //      (expressionList endOfLine)?
  //      commaSep?
  //      commaSep?
  //      arguments*
  //     )
  private static boolean applyFunctionOp_0_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyFunctionOp_0_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = applyFunctionOp_0_3_0_0(builder, level + 1);
    if (!result) result = applyFunctionOp_0_3_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // comprehensionElement+
  private static boolean applyFunctionOp_0_3_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyFunctionOp_0_3_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = comprehensionElement(builder, level + 1);
    while (result) {
      int pos = current_position_(builder);
      if (!comprehensionElement(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "applyFunctionOp_0_3_0_0", pos)) break;
    }
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (expressionList endOfLine)?
  //      commaSep?
  //      commaSep?
  //      arguments*
  private static boolean applyFunctionOp_0_3_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyFunctionOp_0_3_0_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = applyFunctionOp_0_3_0_1_0(builder, level + 1);
    result = result && applyFunctionOp_0_3_0_1_1(builder, level + 1);
    result = result && applyFunctionOp_0_3_0_1_2(builder, level + 1);
    result = result && applyFunctionOp_0_3_0_1_3(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (expressionList endOfLine)?
  private static boolean applyFunctionOp_0_3_0_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyFunctionOp_0_3_0_1_0")) return false;
    applyFunctionOp_0_3_0_1_0_0(builder, level + 1);
    return true;
  }

  // expressionList endOfLine
  private static boolean applyFunctionOp_0_3_0_1_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyFunctionOp_0_3_0_1_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = expressionList(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // commaSep?
  private static boolean applyFunctionOp_0_3_0_1_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyFunctionOp_0_3_0_1_1")) return false;
    commaSep(builder, level + 1);
    return true;
  }

  // commaSep?
  private static boolean applyFunctionOp_0_3_0_1_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyFunctionOp_0_3_0_1_2")) return false;
    commaSep(builder, level + 1);
    return true;
  }

  // arguments*
  private static boolean applyFunctionOp_0_3_0_1_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyFunctionOp_0_3_0_1_3")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!arguments(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "applyFunctionOp_0_3_0_1_3", pos)) break;
    }
    return true;
  }

  // doBlock?
  private static boolean applyFunctionOp_0_6(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyFunctionOp_0_6")) return false;
    doBlock(builder, level + 1);
    return true;
  }

  // LEFT_M_BRACKET (
  //    comprehensionElement
  //  | exprOrEnd typeAnnotation? (COMMA_SYM exprOrEnd typeAnnotation?)*
  //  ) endOfLine RIGHT_M_BRACKET
  private static boolean applyIndexOp_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyIndexOp_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, LEFT_M_BRACKET);
    result = result && applyIndexOp_0_1(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && consumeToken(builder, RIGHT_M_BRACKET);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // comprehensionElement
  //  | exprOrEnd typeAnnotation? (COMMA_SYM exprOrEnd typeAnnotation?)*
  private static boolean applyIndexOp_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyIndexOp_0_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = comprehensionElement(builder, level + 1);
    if (!result) result = applyIndexOp_0_1_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // exprOrEnd typeAnnotation? (COMMA_SYM exprOrEnd typeAnnotation?)*
  private static boolean applyIndexOp_0_1_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyIndexOp_0_1_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = exprOrEnd(builder, level + 1, -1);
    result = result && applyIndexOp_0_1_1_1(builder, level + 1);
    result = result && applyIndexOp_0_1_1_2(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // typeAnnotation?
  private static boolean applyIndexOp_0_1_1_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyIndexOp_0_1_1_1")) return false;
    typeAnnotation(builder, level + 1);
    return true;
  }

  // (COMMA_SYM exprOrEnd typeAnnotation?)*
  private static boolean applyIndexOp_0_1_1_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyIndexOp_0_1_1_2")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!applyIndexOp_0_1_1_2_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "applyIndexOp_0_1_1_2", pos)) break;
    }
    return true;
  }

  // COMMA_SYM exprOrEnd typeAnnotation?
  private static boolean applyIndexOp_0_1_1_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyIndexOp_0_1_1_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, COMMA_SYM);
    result = result && exprOrEnd(builder, level + 1, -1);
    result = result && applyIndexOp_0_1_1_2_0_2(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // typeAnnotation?
  private static boolean applyIndexOp_0_1_1_2_0_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "applyIndexOp_0_1_1_2_0_2")) return false;
    typeAnnotation(builder, level + 1);
    return true;
  }

  // typeParameters whereClause?
  private static boolean type_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "type_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = typeParameters(builder, level + 1);
    result = result && type_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // whereClause?
  private static boolean type_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "type_0_1")) return false;
    whereClause(builder, level + 1);
    return true;
  }

  // singleQuoteString | tripleQuoteString
  public static boolean string(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "string")) return false;
    if (!nextTokenIsSmart(builder, QUOTE_START, TRIPLE_QUOTE_START)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, STRING, "<string>");
    result = singleQuoteString(builder, level + 1);
    if (!result) result = tripleQuoteString(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // CMD_QUOTE_START stringTemplateElement* CMD_QUOTE_END
  public static boolean command(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "command")) return false;
    if (!nextTokenIsSmart(builder, CMD_QUOTE_START)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, CMD_QUOTE_START);
    result = result && command_1(builder, level + 1);
    result = result && consumeToken(builder, CMD_QUOTE_END);
    exit_section_(builder, marker, COMMAND, result);
    return result;
  }

  // stringTemplateElement*
  private static boolean command_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "command_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!stringTemplateElement(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "command_1", pos)) break;
    }
    return true;
  }

  // REGEX_START stringTrivialElement* REGEX_END
  public static boolean regex(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "regex")) return false;
    if (!nextTokenIsSmart(builder, REGEX_START)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, REGEX_START);
    result = result && regex_1(builder, level + 1);
    result = result && consumeToken(builder, REGEX_END);
    exit_section_(builder, marker, REGEX, result);
    return result;
  }

  // stringTrivialElement*
  private static boolean regex_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "regex_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!stringTrivialElement(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "regex_1", pos)) break;
    }
    return true;
  }

  // RAW_STR_START stringContent* RAW_STR_END
  public static boolean rawString(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rawString")) return false;
    if (!nextTokenIsSmart(builder, RAW_STR_START)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, RAW_STRING, null);
    result = consumeTokenSmart(builder, RAW_STR_START);
    pinned = result; // pin = 1
    result = result && report_error_(builder, rawString_1(builder, level + 1));
    result = pinned && consumeToken(builder, RAW_STR_END) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // stringContent*
  private static boolean rawString_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rawString_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!stringContent(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "rawString_1", pos)) break;
    }
    return true;
  }

  // VERSION_START stringContent* VERSION_END
  public static boolean versionNumber(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "versionNumber")) return false;
    if (!nextTokenIsSmart(builder, VERSION_START)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, VERSION_NUMBER, null);
    result = consumeTokenSmart(builder, VERSION_START);
    pinned = result; // pin = 1
    result = result && report_error_(builder, versionNumber_1(builder, level + 1));
    result = pinned && consumeToken(builder, VERSION_END) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // stringContent*
  private static boolean versionNumber_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "versionNumber_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!stringContent(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "versionNumber_1", pos)) break;
    }
    return true;
  }

  // BYTE_ARRAY_START stringTrivialElement* BYTE_ARRAY_END
  public static boolean byteArray(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "byteArray")) return false;
    if (!nextTokenIsSmart(builder, BYTE_ARRAY_START)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, BYTE_ARRAY, null);
    result = consumeTokenSmart(builder, BYTE_ARRAY_START);
    pinned = result; // pin = 1
    result = result && report_error_(builder, byteArray_1(builder, level + 1));
    result = pinned && consumeToken(builder, BYTE_ARRAY_END) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // stringTrivialElement*
  private static boolean byteArray_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "byteArray_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!stringTrivialElement(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "byteArray_1", pos)) break;
    }
    return true;
  }

  // CHAR_LITERAL
  public static boolean charLit(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "charLit")) return false;
    if (!nextTokenIsSmart(builder, CHAR_LITERAL)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, CHAR_LITERAL);
    exit_section_(builder, marker, CHAR_LIT, result);
    return result;
  }

  // INT_LITERAL
  public static boolean integer(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "integer")) return false;
    if (!nextTokenIsSmart(builder, INT_LITERAL)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, INT_LITERAL);
    exit_section_(builder, marker, INTEGER, result);
    return result;
  }

  // FLOAT_LITERAL | FLOAT_CONSTANT
  public static boolean floatLit(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "floatLit")) return false;
    if (!nextTokenIsSmart(builder, FLOAT_CONSTANT, FLOAT_LITERAL)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLOAT_LIT, "<float lit>");
    result = consumeTokenSmart(builder, FLOAT_LITERAL);
    if (!result) result = consumeTokenSmart(builder, FLOAT_CONSTANT);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // TRUE_KEYWORD | FALSE_KEYWORD
  public static boolean booleanLit(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "booleanLit")) return false;
    if (!nextTokenIsSmart(builder, FALSE_KEYWORD, TRUE_KEYWORD)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, BOOLEAN_LIT, "<boolean lit>");
    result = consumeTokenSmart(builder, TRUE_KEYWORD);
    if (!result) result = consumeTokenSmart(builder, FALSE_KEYWORD);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // IF_KEYWORD expr endOfLine
  //   statements
  //   elseIfClause*
  //   elseClause?
  //  END_KEYWORD
  public static boolean ifExpr(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "ifExpr")) return false;
    if (!nextTokenIsSmart(builder, IF_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, IF_EXPR, null);
    result = consumeTokenSmart(builder, IF_KEYWORD);
    pinned = result; // pin = 1
    result = result && report_error_(builder, expr(builder, level + 1, -1));
    result = pinned && report_error_(builder, endOfLine(builder, level + 1)) && result;
    result = pinned && report_error_(builder, statements(builder, level + 1)) && result;
    result = pinned && report_error_(builder, ifExpr_4(builder, level + 1)) && result;
    result = pinned && report_error_(builder, ifExpr_5(builder, level + 1)) && result;
    result = pinned && consumeToken(builder, END_KEYWORD) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // elseIfClause*
  private static boolean ifExpr_4(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "ifExpr_4")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!elseIfClause(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "ifExpr_4", pos)) break;
    }
    return true;
  }

  // elseClause?
  private static boolean ifExpr_5(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "ifExpr_5")) return false;
    elseClause(builder, level + 1);
    return true;
  }

  // FOR_KEYWORD endOfLine indexer (commaSep indexer)* statements END_KEYWORD
  public static boolean forExpr(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "forExpr")) return false;
    if (!nextTokenIsSmart(builder, FOR_KEYWORD)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, FOR_KEYWORD);
    result = result && endOfLine(builder, level + 1);
    result = result && indexer(builder, level + 1);
    result = result && forExpr_3(builder, level + 1);
    result = result && statements(builder, level + 1);
    result = result && consumeToken(builder, END_KEYWORD);
    exit_section_(builder, marker, FOR_EXPR, result);
    return result;
  }

  // (commaSep indexer)*
  private static boolean forExpr_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "forExpr_3")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!forExpr_3_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "forExpr_3", pos)) break;
    }
    return true;
  }

  // commaSep indexer
  private static boolean forExpr_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "forExpr_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = commaSep(builder, level + 1);
    result = result && indexer(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // LEFT_M_BRACKET endOfLine
  //   comprehensionElement+ endOfLine
  //  RIGHT_M_BRACKET
  public static boolean forComprehension(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "forComprehension")) return false;
    if (!nextTokenIsSmart(builder, LEFT_M_BRACKET)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, LEFT_M_BRACKET);
    result = result && endOfLine(builder, level + 1);
    result = result && forComprehension_2(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && consumeToken(builder, RIGHT_M_BRACKET);
    exit_section_(builder, marker, FOR_COMPREHENSION, result);
    return result;
  }

  // comprehensionElement+
  private static boolean forComprehension_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "forComprehension_2")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = comprehensionElement(builder, level + 1);
    while (result) {
      int pos = current_position_(builder);
      if (!comprehensionElement(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "forComprehension_2", pos)) break;
    }
    exit_section_(builder, marker, null, result);
    return result;
  }

  // WHILE_KEYWORD endOfLine
  //   expr endOfLine
  //    whileLazyBlock
  //  END_KEYWORD
  public static boolean whileExpr(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "whileExpr")) return false;
    if (!nextTokenIsSmart(builder, WHILE_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, WHILE_EXPR, null);
    result = consumeTokenSmart(builder, WHILE_KEYWORD);
    pinned = result; // pin = 1
    result = result && report_error_(builder, endOfLine(builder, level + 1));
    result = pinned && report_error_(builder, expr(builder, level + 1, -1)) && result;
    result = pinned && report_error_(builder, endOfLine(builder, level + 1)) && result;
    result = pinned && report_error_(builder, whileLazyBlock(builder, level + 1)) && result;
    result = pinned && consumeToken(builder, END_KEYWORD) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // FUNCTION_KEYWORD endOfLine
  //   (SYM DOT_SYM)? // TODO
  //    (symbol|bracketedFunctionName)? // function Name
  //    (typeParameters endOfLine)?
  //    (functionSignature endOfLine)?
  //    (whereClause endOfLine)*
  //   <<lazyBlockNotParseEndImpl>>
  //  END_KEYWORD
  public static boolean function(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "function")) return false;
    if (!nextTokenIsSmart(builder, FUNCTION_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FUNCTION, null);
    result = consumeTokenSmart(builder, FUNCTION_KEYWORD);
    pinned = result; // pin = 1
    result = result && report_error_(builder, endOfLine(builder, level + 1));
    result = pinned && report_error_(builder, function_2(builder, level + 1)) && result;
    result = pinned && report_error_(builder, function_3(builder, level + 1)) && result;
    result = pinned && report_error_(builder, function_4(builder, level + 1)) && result;
    result = pinned && report_error_(builder, function_5(builder, level + 1)) && result;
    result = pinned && report_error_(builder, function_6(builder, level + 1)) && result;
    result = pinned && report_error_(builder, lazyBlockNotParseEndImpl(builder, level + 1)) && result;
    result = pinned && consumeToken(builder, END_KEYWORD) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // (SYM DOT_SYM)?
  private static boolean function_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "function_2")) return false;
    function_2_0(builder, level + 1);
    return true;
  }

  // SYM DOT_SYM
  private static boolean function_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "function_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokensSmart(builder, 0, SYM, DOT_SYM);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (symbol|bracketedFunctionName)?
  private static boolean function_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "function_3")) return false;
    function_3_0(builder, level + 1);
    return true;
  }

  // symbol|bracketedFunctionName
  private static boolean function_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "function_3_0")) return false;
    boolean result;
    result = symbol(builder, level + 1);
    if (!result) result = bracketedFunctionName(builder, level + 1);
    return result;
  }

  // (typeParameters endOfLine)?
  private static boolean function_4(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "function_4")) return false;
    function_4_0(builder, level + 1);
    return true;
  }

  // typeParameters endOfLine
  private static boolean function_4_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "function_4_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = typeParameters(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (functionSignature endOfLine)?
  private static boolean function_5(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "function_5")) return false;
    function_5_0(builder, level + 1);
    return true;
  }

  // functionSignature endOfLine
  private static boolean function_5_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "function_5_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = functionSignature(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (whereClause endOfLine)*
  private static boolean function_6(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "function_6")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!function_6_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "function_6", pos)) break;
    }
    return true;
  }

  // whereClause endOfLine
  private static boolean function_6_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "function_6_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = whereClause(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // RETURN_KEYWORD (expr (COMMA_SYM expr)*)?
  public static boolean returnExpr(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "returnExpr")) return false;
    if (!nextTokenIsSmart(builder, RETURN_KEYWORD)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, RETURN_KEYWORD);
    result = result && returnExpr_1(builder, level + 1);
    exit_section_(builder, marker, RETURN_EXPR, result);
    return result;
  }

  // (expr (COMMA_SYM expr)*)?
  private static boolean returnExpr_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "returnExpr_1")) return false;
    returnExpr_1_0(builder, level + 1);
    return true;
  }

  // expr (COMMA_SYM expr)*
  private static boolean returnExpr_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "returnExpr_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = expr(builder, level + 1, -1);
    result = result && returnExpr_1_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (COMMA_SYM expr)*
  private static boolean returnExpr_1_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "returnExpr_1_0_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!returnExpr_1_0_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "returnExpr_1_0_1", pos)) break;
    }
    return true;
  }

  // COMMA_SYM expr
  private static boolean returnExpr_1_0_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "returnExpr_1_0_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, COMMA_SYM);
    result = result && expr(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // BREAK_KEYWORD
  public static boolean breakExpr(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "breakExpr")) return false;
    if (!nextTokenIsSmart(builder, BREAK_KEYWORD)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, BREAK_KEYWORD);
    exit_section_(builder, marker, BREAK_EXPR, result);
    return result;
  }

  // TRY_KEYWORD endOfLine
  //   statements
  //  catchClause?
  //  finallyClause?
  //  END_KEYWORD
  public static boolean tryCatch(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "tryCatch")) return false;
    if (!nextTokenIsSmart(builder, TRY_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, TRY_CATCH, null);
    result = consumeTokenSmart(builder, TRY_KEYWORD);
    pinned = result; // pin = 1
    result = result && report_error_(builder, endOfLine(builder, level + 1));
    result = pinned && report_error_(builder, statements(builder, level + 1)) && result;
    result = pinned && report_error_(builder, tryCatch_3(builder, level + 1)) && result;
    result = pinned && report_error_(builder, tryCatch_4(builder, level + 1)) && result;
    result = pinned && consumeToken(builder, END_KEYWORD) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // catchClause?
  private static boolean tryCatch_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "tryCatch_3")) return false;
    catchClause(builder, level + 1);
    return true;
  }

  // finallyClause?
  private static boolean tryCatch_4(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "tryCatch_4")) return false;
    finallyClause(builder, level + 1);
    return true;
  }

  // CONTINUE_KEYWORD
  public static boolean continueExpr(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "continueExpr")) return false;
    if (!nextTokenIsSmart(builder, CONTINUE_KEYWORD)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, CONTINUE_KEYWORD);
    exit_section_(builder, marker, CONTINUE_EXPR, result);
    return result;
  }

  // LEFT_BRACKET endOfLine
  //   tupleExprs (commaSep tupleExprs)* endOfLine COMMA_SYM? endOfLine
  //  RIGHT_BRACKET
  public static boolean tuple(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "tuple")) return false;
    if (!nextTokenIsSmart(builder, LEFT_BRACKET)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, LEFT_BRACKET);
    result = result && endOfLine(builder, level + 1);
    result = result && tupleExprs(builder, level + 1);
    result = result && tuple_3(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && tuple_5(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && consumeToken(builder, RIGHT_BRACKET);
    exit_section_(builder, marker, TUPLE, result);
    return result;
  }

  // (commaSep tupleExprs)*
  private static boolean tuple_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "tuple_3")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!tuple_3_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "tuple_3", pos)) break;
    }
    return true;
  }

  // commaSep tupleExprs
  private static boolean tuple_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "tuple_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = commaSep(builder, level + 1);
    result = result && tupleExprs(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // COMMA_SYM?
  private static boolean tuple_5(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "tuple_5")) return false;
    consumeTokenSmart(builder, COMMA_SYM);
    return true;
  }

  // MACRO_KEYWORD endOfLine
  //     symbol endOfLine
  //    untypedVariables? endOfLine
  //   <<lazyBlockNotParseEndImpl>>?
  //  END_KEYWORD
  public static boolean macro(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "macro")) return false;
    if (!nextTokenIsSmart(builder, MACRO_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, MACRO, null);
    result = consumeTokenSmart(builder, MACRO_KEYWORD);
    pinned = result; // pin = 1
    result = result && report_error_(builder, endOfLine(builder, level + 1));
    result = pinned && report_error_(builder, symbol(builder, level + 1)) && result;
    result = pinned && report_error_(builder, endOfLine(builder, level + 1)) && result;
    result = pinned && report_error_(builder, macro_4(builder, level + 1)) && result;
    result = pinned && report_error_(builder, endOfLine(builder, level + 1)) && result;
    result = pinned && report_error_(builder, macro_6(builder, level + 1)) && result;
    result = pinned && consumeToken(builder, END_KEYWORD) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // untypedVariables?
  private static boolean macro_4(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "macro_4")) return false;
    untypedVariables(builder, level + 1);
    return true;
  }

  // <<lazyBlockNotParseEndImpl>>?
  private static boolean macro_6(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "macro_6")) return false;
    lazyBlockNotParseEndImpl(builder, level + 1);
    return true;
  }

  // LET_KEYWORD endOfLine assignLevelOp (commaSep assignLevelOp)* statements END_KEYWORD?
  public static boolean let(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "let")) return false;
    if (!nextTokenIsSmart(builder, LET_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, LET, null);
    result = consumeTokenSmart(builder, LET_KEYWORD);
    pinned = result; // pin = 1
    result = result && report_error_(builder, endOfLine(builder, level + 1));
    result = pinned && report_error_(builder, expr(builder, level + 1, 2)) && result;
    result = pinned && report_error_(builder, let_3(builder, level + 1)) && result;
    result = pinned && report_error_(builder, statements(builder, level + 1)) && result;
    result = pinned && let_5(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // (commaSep assignLevelOp)*
  private static boolean let_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "let_3")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!let_3_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "let_3", pos)) break;
    }
    return true;
  }

  // commaSep assignLevelOp
  private static boolean let_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "let_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = commaSep(builder, level + 1);
    result = result && expr(builder, level + 1, 2);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // END_KEYWORD?
  private static boolean let_5(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "let_5")) return false;
    consumeTokenSmart(builder, END_KEYWORD);
    return true;
  }

  // BEGIN_KEYWORD <<lazyBlockNotParseEndImpl>> END_KEYWORD
  public static boolean beginBlock(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "beginBlock")) return false;
    if (!nextTokenIsSmart(builder, BEGIN_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, BEGIN_BLOCK, null);
    result = consumeTokenSmart(builder, BEGIN_KEYWORD);
    pinned = result; // pin = 1
    result = result && report_error_(builder, lazyBlockNotParseEndImpl(builder, level + 1));
    result = pinned && consumeToken(builder, END_KEYWORD) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // COLON_BEGIN_SYM <<lazyBlockParseEndImpl>> RIGHT_BRACKET
  public static boolean colonBlock(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "colonBlock")) return false;
    if (!nextTokenIsSmart(builder, COLON_BEGIN_SYM)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, COLON_BEGIN_SYM);
    result = result && lazyBlockParseEndImpl(builder, level + 1);
    result = result && consumeToken(builder, RIGHT_BRACKET);
    exit_section_(builder, marker, COLON_BLOCK, result);
    return result;
  }

  // EXPORT_KEYWORD endOfLine
  //   memberAccess
  //   (commaSep memberAccess)*
  public static boolean export(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "export")) return false;
    if (!nextTokenIsSmart(builder, EXPORT_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, EXPORT, null);
    result = consumeTokenSmart(builder, EXPORT_KEYWORD);
    pinned = result; // pin = 1
    result = result && report_error_(builder, endOfLine(builder, level + 1));
    result = pinned && report_error_(builder, memberAccess(builder, level + 1)) && result;
    result = pinned && export_3(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // (commaSep memberAccess)*
  private static boolean export_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "export_3")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!export_3_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "export_3", pos)) break;
    }
    return true;
  }

  // commaSep memberAccess
  private static boolean export_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "export_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = commaSep(builder, level + 1);
    result = result && memberAccess(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // IMPORT_KEYWORD endOfLine imported
  public static boolean importExpr(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "importExpr")) return false;
    if (!nextTokenIsSmart(builder, IMPORT_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, IMPORT_EXPR, null);
    result = consumeTokenSmart(builder, IMPORT_KEYWORD);
    pinned = result; // pin = 1
    result = result && report_error_(builder, endOfLine(builder, level + 1));
    result = pinned && imported(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // IMPORTALL_KEYWORD access
  public static boolean importAllExpr(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "importAllExpr")) return false;
    if (!nextTokenIsSmart(builder, IMPORTALL_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, IMPORT_ALL_EXPR, null);
    result = consumeTokenSmart(builder, IMPORTALL_KEYWORD);
    pinned = result; // pin = 1
    result = result && access(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // USING_KEYWORD endOfLine imported
  public static boolean using(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "using")) return false;
    if (!nextTokenIsSmart(builder, USING_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, USING, null);
    result = consumeTokenSmart(builder, USING_KEYWORD);
    pinned = result; // pin = 1
    result = result && report_error_(builder, endOfLine(builder, level + 1));
    result = pinned && imported(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // PRIMITIVE_TYPE_KEYWORD endOfLine
  //   symbol (SUBTYPE_SYM endOfLine expr)? expr endOfLine
  //  END_KEYWORD
  public static boolean primitiveTypeDeclaration(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "primitiveTypeDeclaration")) return false;
    if (!nextTokenIsSmart(builder, PRIMITIVE_TYPE_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, PRIMITIVE_TYPE_DECLARATION, null);
    result = consumeTokenSmart(builder, PRIMITIVE_TYPE_KEYWORD);
    pinned = result; // pin = 1
    result = result && report_error_(builder, endOfLine(builder, level + 1));
    result = pinned && report_error_(builder, symbol(builder, level + 1)) && result;
    result = pinned && report_error_(builder, primitiveTypeDeclaration_3(builder, level + 1)) && result;
    result = pinned && report_error_(builder, expr(builder, level + 1, -1)) && result;
    result = pinned && report_error_(builder, endOfLine(builder, level + 1)) && result;
    result = pinned && consumeToken(builder, END_KEYWORD) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // (SUBTYPE_SYM endOfLine expr)?
  private static boolean primitiveTypeDeclaration_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "primitiveTypeDeclaration_3")) return false;
    primitiveTypeDeclaration_3_0(builder, level + 1);
    return true;
  }

  // SUBTYPE_SYM endOfLine expr
  private static boolean primitiveTypeDeclaration_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "primitiveTypeDeclaration_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, SUBTYPE_SYM);
    result = result && endOfLine(builder, level + 1);
    result = result && expr(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  public static boolean typeAlias(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeAlias")) return false;
    if (!nextTokenIsSmart(builder, TYPEALIAS_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, null);
    result = consumeTokenSmart(builder, TYPEALIAS_KEYWORD);
    pinned = result;
    result = pinned && expr(builder, level, 21);
    result = pinned && report_error_(builder, typeAlias_1(builder, level + 1)) && result;
    exit_section_(builder, level, marker, TYPE_ALIAS, result, pinned, null);
    return result || pinned;
  }

  // typeParameters? userType
  private static boolean typeAlias_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeAlias_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = typeAlias_1_0(builder, level + 1);
    result = result && userType(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // typeParameters?
  private static boolean typeAlias_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "typeAlias_1_0")) return false;
    typeParameters(builder, level + 1);
    return true;
  }

  // ((LOCAL_KEYWORD | CONST_KEYWORD) endOfLine)
  //  symbolLhsInternal (commaSep symbolLhsInternal)*
  public static boolean symbolLhs(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "symbolLhs")) return false;
    if (!nextTokenIsSmart(builder, CONST_KEYWORD, LOCAL_KEYWORD)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _COLLAPSE_, SYMBOL_LHS, "<symbol lhs>");
    result = symbolLhs_0(builder, level + 1);
    pinned = result; // pin = 1
    result = result && report_error_(builder, symbolLhsInternal(builder, level + 1));
    result = pinned && symbolLhs_2(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // (LOCAL_KEYWORD | CONST_KEYWORD) endOfLine
  private static boolean symbolLhs_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "symbolLhs_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = symbolLhs_0_0(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // LOCAL_KEYWORD | CONST_KEYWORD
  private static boolean symbolLhs_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "symbolLhs_0_0")) return false;
    boolean result;
    result = consumeTokenSmart(builder, LOCAL_KEYWORD);
    if (!result) result = consumeTokenSmart(builder, CONST_KEYWORD);
    return result;
  }

  // (commaSep symbolLhsInternal)*
  private static boolean symbolLhs_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "symbolLhs_2")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!symbolLhs_2_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "symbolLhs_2", pos)) break;
    }
    return true;
  }

  // commaSep symbolLhsInternal
  private static boolean symbolLhs_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "symbolLhs_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = commaSep(builder, level + 1);
    result = result && symbolLhsInternal(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // SYM | privateOpAsSymbol
  public static boolean symbol(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "symbol")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _COLLAPSE_, SYMBOL, "<symbol>");
    result = consumeTokenSmart(builder, SYM);
    if (!result) result = privateOpAsSymbol(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // MACRO_SYM
  public static boolean macroSymbol(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "macroSymbol")) return false;
    if (!nextTokenIsSmart(builder, MACRO_SYM)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, MACRO_SYM);
    exit_section_(builder, marker, MACRO_SYMBOL, result);
    return result;
  }

  // LEFT_BRACKET endOfLine
  //   (opAsSymbol | unaryOpAsSymbol)?
  //    statements endOfLine (COMMA_SYM endOfLine)? (FOR_KEYWORD expr IN_KEYWORD expr)*
  //  RIGHT_BRACKET
  public static boolean bracketedExpr(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "bracketedExpr")) return false;
    if (!nextTokenIsSmart(builder, LEFT_BRACKET)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, LEFT_BRACKET);
    result = result && endOfLine(builder, level + 1);
    result = result && bracketedExpr_2(builder, level + 1);
    result = result && statements(builder, level + 1);
    result = result && endOfLine(builder, level + 1);
    result = result && bracketedExpr_5(builder, level + 1);
    result = result && bracketedExpr_6(builder, level + 1);
    result = result && consumeToken(builder, RIGHT_BRACKET);
    exit_section_(builder, marker, BRACKETED_EXPR, result);
    return result;
  }

  // (opAsSymbol | unaryOpAsSymbol)?
  private static boolean bracketedExpr_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "bracketedExpr_2")) return false;
    bracketedExpr_2_0(builder, level + 1);
    return true;
  }

  // opAsSymbol | unaryOpAsSymbol
  private static boolean bracketedExpr_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "bracketedExpr_2_0")) return false;
    boolean result;
    result = opAsSymbol(builder, level + 1);
    if (!result) result = unaryOpAsSymbol(builder, level + 1);
    return result;
  }

  // (COMMA_SYM endOfLine)?
  private static boolean bracketedExpr_5(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "bracketedExpr_5")) return false;
    bracketedExpr_5_0(builder, level + 1);
    return true;
  }

  // COMMA_SYM endOfLine
  private static boolean bracketedExpr_5_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "bracketedExpr_5_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, COMMA_SYM);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // (FOR_KEYWORD expr IN_KEYWORD expr)*
  private static boolean bracketedExpr_6(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "bracketedExpr_6")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!bracketedExpr_6_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "bracketedExpr_6", pos)) break;
    }
    return true;
  }

  // FOR_KEYWORD expr IN_KEYWORD expr
  private static boolean bracketedExpr_6_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "bracketedExpr_6_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, FOR_KEYWORD);
    result = result && expr(builder, level + 1, -1);
    result = result && consumeToken(builder, IN_KEYWORD);
    result = result && expr(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // LEFT_BRACKET comprehensionElement+ RIGHT_BRACKET
  public static boolean bracketedComprehensionExpr(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "bracketedComprehensionExpr")) return false;
    if (!nextTokenIsSmart(builder, LEFT_BRACKET)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, LEFT_BRACKET);
    result = result && bracketedComprehensionExpr_1(builder, level + 1);
    result = result && consumeToken(builder, RIGHT_BRACKET);
    exit_section_(builder, marker, BRACKETED_COMPREHENSION_EXPR, result);
    return result;
  }

  // comprehensionElement+
  private static boolean bracketedComprehensionExpr_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "bracketedComprehensionExpr_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = comprehensionElement(builder, level + 1);
    while (result) {
      int pos = current_position_(builder);
      if (!comprehensionElement(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "bracketedComprehensionExpr_1", pos)) break;
    }
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // Expression root: exprOrEnd
  // Operator priority table:
  // 0: BINARY(plusIndexing)
  // 1: BINARY(multiplyIndexing)
  // 2: POSTFIX(spliceIndexing)
  // 3: BINARY(assignLevelOperatorIndexing)
  // 4: PREFIX(dotApplyFunctionOpIndexing)
  // 5: BINARY(rangeIndexing)
  // 6: ATOM(quoteIndexing)
  // 7: BINARY(ternaryOpIndexing)
  // 8: ATOM(end)
  // 9: ATOM(exprWrapper)
  // 10: PREFIX(bracketedExprIndexing)
  public static boolean exprOrEnd(PsiBuilder builder, int level, int priority) {
    if (!recursion_guard_(builder, level, "exprOrEnd")) return false;
    addVariant(builder, "<expr or end>");
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, "<expr or end>");
    result = dotApplyFunctionOpIndexing(builder, level + 1);
    if (!result) result = quoteIndexing(builder, level + 1);
    if (!result) result = end(builder, level + 1);
    if (!result) result = exprWrapper(builder, level + 1);
    if (!result) result = bracketedExprIndexing(builder, level + 1);
    pinned = result;
    result = result && exprOrEnd_0(builder, level + 1, priority);
    exit_section_(builder, level, marker, null, result, pinned, null);
    return result || pinned;
  }

  public static boolean exprOrEnd_0(PsiBuilder builder, int level, int priority) {
    if (!recursion_guard_(builder, level, "exprOrEnd_0")) return false;
    boolean result = true;
    while (true) {
      Marker marker = enter_section_(builder, level, _LEFT_, null);
      if (priority < 0 && plusLevelOperator(builder, level + 1)) {
        result = exprOrEnd(builder, level, 0);
        exit_section_(builder, level, marker, PLUS_INDEXING, result, true, null);
      }
      else if (priority < 1 && multiplyLevelOperator(builder, level + 1)) {
        result = exprOrEnd(builder, level, 1);
        exit_section_(builder, level, marker, MULTIPLY_INDEXING, result, true, null);
      }
      else if (priority < 2 && consumeTokenSmart(builder, SLICE_SYM)) {
        result = true;
        exit_section_(builder, level, marker, SPLICE_INDEXING, result, true, null);
      }
      else if (priority < 3 && assignLevelOperator(builder, level + 1)) {
        result = exprOrEnd(builder, level, 3);
        exit_section_(builder, level, marker, ASSIGN_LEVEL_OPERATOR_INDEXING, result, true, null);
      }
      else if (priority < 5 && rangeIndexing_0(builder, level + 1)) {
        result = exprOrEnd(builder, level, 5);
        exit_section_(builder, level, marker, RANGE_INDEXING, result, true, null);
      }
      else if (priority < 7 && ternaryOpIndexing_0(builder, level + 1)) {
        result = exprOrEnd(builder, level, 4);
        exit_section_(builder, level, marker, TERNARY_OP_INDEXING, result, true, null);
      }
      else {
        exit_section_(builder, level, marker, null, false, false, null);
        break;
      }
    }
    return result;
  }

  public static boolean dotApplyFunctionOpIndexing(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "dotApplyFunctionOpIndexing")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, null);
    result = dotApplyFunctionOpIndexing_0(builder, level + 1);
    pinned = result;
    result = pinned && exprOrEnd(builder, level, 4);
    exit_section_(builder, level, marker, DOT_APPLY_FUNCTION_OP_INDEXING, result, pinned, null);
    return result || pinned;
  }

  // symbol DOT_SYM privateOpSymbols
  private static boolean dotApplyFunctionOpIndexing_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "dotApplyFunctionOpIndexing_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = symbol(builder, level + 1);
    result = result && consumeToken(builder, DOT_SYM);
    result = result && privateOpSymbols(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // endOfLine COLON_SYM endOfLine
  private static boolean rangeIndexing_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rangeIndexing_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = endOfLine(builder, level + 1);
    result = result && consumeToken(builder, COLON_SYM);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // COLON_SYM exprOrEnd?
  public static boolean quoteIndexing(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "quoteIndexing")) return false;
    if (!nextTokenIsSmart(builder, COLON_SYM)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, COLON_SYM);
    result = result && quoteIndexing_1(builder, level + 1);
    exit_section_(builder, marker, QUOTE_INDEXING, result);
    return result;
  }

  // exprOrEnd?
  private static boolean quoteIndexing_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "quoteIndexing_1")) return false;
    exprOrEnd(builder, level + 1, -1);
    return true;
  }

  // QUESTION_SYM endOfLine
  private static boolean ternaryOpIndexing_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "ternaryOpIndexing_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, QUESTION_SYM);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // END_KEYWORD
  public static boolean end(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "end")) return false;
    if (!nextTokenIsSmart(builder, END_KEYWORD)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, END_KEYWORD);
    exit_section_(builder, marker, END, result);
    return result;
  }

  // memberAccessOp | ternaryOp | primaryExpr
  public static boolean exprWrapper(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "exprWrapper")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, EXPR_WRAPPER, "<expr wrapper>");
    result = expr(builder, level + 1, 20);
    if (!result) result = expr(builder, level + 1, 4);
    if (!result) result = expr(builder, level + 1, 21);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  public static boolean bracketedExprIndexing(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "bracketedExprIndexing")) return false;
    if (!nextTokenIsSmart(builder, LEFT_BRACKET)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, null);
    result = bracketedExprIndexing_0(builder, level + 1);
    pinned = result;
    result = pinned && exprOrEnd(builder, level, -1);
    result = pinned && report_error_(builder, consumeToken(builder, RIGHT_BRACKET)) && result;
    exit_section_(builder, level, marker, BRACKETED_EXPR_INDEXING, result, pinned, null);
    return result || pinned;
  }

  // LEFT_BRACKET endOfLine
  private static boolean bracketedExprIndexing_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "bracketedExprIndexing_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, LEFT_BRACKET);
    result = result && endOfLine(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

}
