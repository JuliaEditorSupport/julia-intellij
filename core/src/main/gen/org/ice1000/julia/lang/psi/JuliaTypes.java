// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.ice1000.julia.lang.JuliaElementType;
import org.ice1000.julia.lang.JuliaTokenType;
import org.ice1000.julia.lang.psi.impl.*;

public interface JuliaTypes {

  IElementType ABSTRACT_TYPE_DECLARATION = JuliaElementType.createType("ABSTRACT_TYPE_DECLARATION");
  IElementType AND_OP = JuliaElementType.createType("AND_OP");
  IElementType APPLY_FUNCTION_OP = JuliaElementType.createType("APPLY_FUNCTION_OP");
  IElementType APPLY_INDEX_OP = JuliaElementType.createType("APPLY_INDEX_OP");
  IElementType APPLY_MACRO_OP = JuliaElementType.createType("APPLY_MACRO_OP");
  IElementType ARGUMENTS = JuliaElementType.createType("ARGUMENTS");
  IElementType ARRAY = JuliaElementType.createType("ARRAY");
  IElementType ARROW_OP = JuliaElementType.createType("ARROW_OP");
  IElementType ASSIGN_LEVEL_OP = JuliaElementType.createType("ASSIGN_LEVEL_OP");
  IElementType ASSIGN_LEVEL_OPERATOR = JuliaElementType.createType("ASSIGN_LEVEL_OPERATOR");
  IElementType ASSIGN_LEVEL_OPERATOR_INDEXING = JuliaElementType.createType("ASSIGN_LEVEL_OPERATOR_INDEXING");
  IElementType ASSIGN_OP = JuliaElementType.createType("ASSIGN_OP");
  IElementType BEGIN_BLOCK = JuliaElementType.createType("BEGIN_BLOCK");
  IElementType BITWISE_LEVEL_OP = JuliaElementType.createType("BITWISE_LEVEL_OP");
  IElementType BITWISE_LEVEL_OPERATOR = JuliaElementType.createType("BITWISE_LEVEL_OPERATOR");
  IElementType BIT_WISE_NOT_OP = JuliaElementType.createType("BIT_WISE_NOT_OP");
  IElementType BOOLEAN_LIT = JuliaElementType.createType("BOOLEAN_LIT");
  IElementType BRACKETED_COMPREHENSION_EXPR = JuliaElementType.createType("BRACKETED_COMPREHENSION_EXPR");
  IElementType BRACKETED_EXPR = JuliaElementType.createType("BRACKETED_EXPR");
  IElementType BRACKETED_EXPR_INDEXING = JuliaElementType.createType("BRACKETED_EXPR_INDEXING");
  IElementType BREAK_EXPR = JuliaElementType.createType("BREAK_EXPR");
  IElementType BYTE_ARRAY = JuliaElementType.createType("BYTE_ARRAY");
  IElementType CATCH_CLAUSE = JuliaElementType.createType("CATCH_CLAUSE");
  IElementType CHAR_LIT = JuliaElementType.createType("CHAR_LIT");
  IElementType COLON_BLOCK = JuliaElementType.createType("COLON_BLOCK");
  IElementType COMMAND = JuliaElementType.createType("COMMAND");
  IElementType COMMENT = JuliaElementType.createType("COMMENT");
  IElementType COMPACT_FUNCTION = JuliaElementType.createType("COMPACT_FUNCTION");
  IElementType COMPARISON_LEVEL_OP = JuliaElementType.createType("COMPARISON_LEVEL_OP");
  IElementType COMPARISON_LEVEL_OPERATOR = JuliaElementType.createType("COMPARISON_LEVEL_OPERATOR");
  IElementType COMPOUND_QUOTE_OP = JuliaElementType.createType("COMPOUND_QUOTE_OP");
  IElementType COMPREHENSION_ELEMENT = JuliaElementType.createType("COMPREHENSION_ELEMENT");
  IElementType CONTINUE_EXPR = JuliaElementType.createType("CONTINUE_EXPR");
  IElementType DOT_APPLY_FUNCTION_OP = JuliaElementType.createType("DOT_APPLY_FUNCTION_OP");
  IElementType DOT_APPLY_FUNCTION_OP_INDEXING = JuliaElementType.createType("DOT_APPLY_FUNCTION_OP_INDEXING");
  IElementType DO_BLOCK = JuliaElementType.createType("DO_BLOCK");
  IElementType ELSE_CLAUSE = JuliaElementType.createType("ELSE_CLAUSE");
  IElementType ELSE_IF_CLAUSE = JuliaElementType.createType("ELSE_IF_CLAUSE");
  IElementType END = JuliaElementType.createType("END");
  IElementType EXPONENT_OP = JuliaElementType.createType("EXPONENT_OP");
  IElementType EXPORT = JuliaElementType.createType("EXPORT");
  IElementType EXPR = JuliaElementType.createType("EXPR");
  IElementType EXPR_INTERPOLATE_OP = JuliaElementType.createType("EXPR_INTERPOLATE_OP");
  IElementType EXPR_OR_END = JuliaElementType.createType("EXPR_OR_END");
  IElementType EXPR_WRAPPER = JuliaElementType.createType("EXPR_WRAPPER");
  IElementType FINALLY_CLAUSE = JuliaElementType.createType("FINALLY_CLAUSE");
  IElementType FLOAT_LIT = JuliaElementType.createType("FLOAT_LIT");
  IElementType FOR_COMPREHENSION = JuliaElementType.createType("FOR_COMPREHENSION");
  IElementType FOR_EXPR = JuliaElementType.createType("FOR_EXPR");
  IElementType FRACTION_OP = JuliaElementType.createType("FRACTION_OP");
  IElementType FUNCTION = JuliaElementType.createType("FUNCTION");
  IElementType FUNCTION_SIGNATURE = JuliaElementType.createType("FUNCTION_SIGNATURE");
  IElementType GLOBAL_STATEMENT = JuliaElementType.createType("GLOBAL_STATEMENT");
  IElementType IF_EXPR = JuliaElementType.createType("IF_EXPR");
  IElementType IMPLICIT_MULTIPLY_OP = JuliaElementType.createType("IMPLICIT_MULTIPLY_OP");
  IElementType IMPORT_ALL_EXPR = JuliaElementType.createType("IMPORT_ALL_EXPR");
  IElementType IMPORT_EXPR = JuliaElementType.createType("IMPORT_EXPR");
  IElementType INTEGER = JuliaElementType.createType("INTEGER");
  IElementType IN_OP = JuliaElementType.createType("IN_OP");
  IElementType ISA_OP = JuliaElementType.createType("ISA_OP");
  IElementType LAMBDA = JuliaElementType.createType("LAMBDA");
  IElementType LET = JuliaElementType.createType("LET");
  IElementType MACRO = JuliaElementType.createType("MACRO");
  IElementType MACRO_SYMBOL = JuliaElementType.createType("MACRO_SYMBOL");
  IElementType MEMBER_ACCESS = JuliaElementType.createType("MEMBER_ACCESS");
  IElementType MEMBER_ACCESS_OP = JuliaElementType.createType("MEMBER_ACCESS_OP");
  IElementType MISC_ARROWS_OP = JuliaElementType.createType("MISC_ARROWS_OP");
  IElementType MISC_EXPONENT_OP = JuliaElementType.createType("MISC_EXPONENT_OP");
  IElementType MODULE_DECLARATION = JuliaElementType.createType("MODULE_DECLARATION");
  IElementType MULTIPLY_INDEXING = JuliaElementType.createType("MULTIPLY_INDEXING");
  IElementType MULTIPLY_LEVEL_OP = JuliaElementType.createType("MULTIPLY_LEVEL_OP");
  IElementType MULTIPLY_LEVEL_OPERATOR = JuliaElementType.createType("MULTIPLY_LEVEL_OPERATOR");
  IElementType MULTI_ASSIGN_OP = JuliaElementType.createType("MULTI_ASSIGN_OP");
  IElementType MULTI_INDEXER = JuliaElementType.createType("MULTI_INDEXER");
  IElementType NOT_OP = JuliaElementType.createType("NOT_OP");
  IElementType OP_AS_SYMBOL = JuliaElementType.createType("OP_AS_SYMBOL");
  IElementType OR_OP = JuliaElementType.createType("OR_OP");
  IElementType PIPE_LEVEL_OP = JuliaElementType.createType("PIPE_LEVEL_OP");
  IElementType PIPE_LEVEL_OPERATOR = JuliaElementType.createType("PIPE_LEVEL_OPERATOR");
  IElementType PLUS_INDEXING = JuliaElementType.createType("PLUS_INDEXING");
  IElementType PLUS_LEVEL_OP = JuliaElementType.createType("PLUS_LEVEL_OP");
  IElementType PLUS_LEVEL_OPERATOR = JuliaElementType.createType("PLUS_LEVEL_OPERATOR");
  IElementType PRIMITIVE_TYPE_DECLARATION = JuliaElementType.createType("PRIMITIVE_TYPE_DECLARATION");
  IElementType QUOTE_INDEXING = JuliaElementType.createType("QUOTE_INDEXING");
  IElementType QUOTE_OP = JuliaElementType.createType("QUOTE_OP");
  IElementType RANGE_INDEXING = JuliaElementType.createType("RANGE_INDEXING");
  IElementType RANGE_OP = JuliaElementType.createType("RANGE_OP");
  IElementType RAW_STRING = JuliaElementType.createType("RAW_STRING");
  IElementType REGEX = JuliaElementType.createType("REGEX");
  IElementType RETURN_EXPR = JuliaElementType.createType("RETURN_EXPR");
  IElementType SINGLE_COMPREHENSION = JuliaElementType.createType("SINGLE_COMPREHENSION");
  IElementType SINGLE_INDEXER = JuliaElementType.createType("SINGLE_INDEXER");
  IElementType SPLICE_INDEXING = JuliaElementType.createType("SPLICE_INDEXING");
  IElementType SPLICE_OP = JuliaElementType.createType("SPLICE_OP");
  IElementType STATEMENTS = JuliaElementType.createType("STATEMENTS");
  IElementType STRING = JuliaElementType.createType("STRING");
  IElementType STRING_CONTENT = JuliaElementType.createType("STRING_CONTENT");
  IElementType STRING_LIKE_MULTIPLY_OP = JuliaElementType.createType("STRING_LIKE_MULTIPLY_OP");
  IElementType SYMBOL = JuliaElementType.createType("SYMBOL");
  IElementType SYMBOL_LHS = JuliaElementType.createType("SYMBOL_LHS");
  IElementType TEMPLATE = JuliaElementType.createType("TEMPLATE");
  IElementType TERNARY_OP = JuliaElementType.createType("TERNARY_OP");
  IElementType TERNARY_OP_INDEXING = JuliaElementType.createType("TERNARY_OP_INDEXING");
  IElementType TRANSPOSE_OP = JuliaElementType.createType("TRANSPOSE_OP");
  IElementType TRY_CATCH = JuliaElementType.createType("TRY_CATCH");
  IElementType TUPLE = JuliaElementType.createType("TUPLE");
  IElementType TYPE = JuliaElementType.createType("TYPE");
  IElementType TYPED_NAMED_VARIABLE = JuliaElementType.createType("TYPED_NAMED_VARIABLE");
  IElementType TYPE_ALIAS = JuliaElementType.createType("TYPE_ALIAS");
  IElementType TYPE_ANNOTATION = JuliaElementType.createType("TYPE_ANNOTATION");
  IElementType TYPE_DECLARATION = JuliaElementType.createType("TYPE_DECLARATION");
  IElementType TYPE_OP = JuliaElementType.createType("TYPE_OP");
  IElementType TYPE_PARAMETERS = JuliaElementType.createType("TYPE_PARAMETERS");
  IElementType UNARY_INTERPOLATE_OP = JuliaElementType.createType("UNARY_INTERPOLATE_OP");
  IElementType UNARY_MINUS_OP = JuliaElementType.createType("UNARY_MINUS_OP");
  IElementType UNARY_OP_AS_SYMBOL = JuliaElementType.createType("UNARY_OP_AS_SYMBOL");
  IElementType UNARY_PLUS_OP = JuliaElementType.createType("UNARY_PLUS_OP");
  IElementType UNARY_SUBTYPE_OP = JuliaElementType.createType("UNARY_SUBTYPE_OP");
  IElementType UNARY_TYPE_OP = JuliaElementType.createType("UNARY_TYPE_OP");
  IElementType UNTYPED_VARIABLES = JuliaElementType.createType("UNTYPED_VARIABLES");
  IElementType USER_TYPE = JuliaElementType.createType("USER_TYPE");
  IElementType USING = JuliaElementType.createType("USING");
  IElementType VERSION_NUMBER = JuliaElementType.createType("VERSION_NUMBER");
  IElementType WHERE_CLAUSE = JuliaElementType.createType("WHERE_CLAUSE");
  IElementType WHILE_EXPR = JuliaElementType.createType("WHILE_EXPR");

  IElementType ABSTRACT_KEYWORD = new JuliaTokenType("ABSTRACT_KEYWORD");
  IElementType ABSTRACT_TYPE_KEYWORD = new JuliaTokenType("ABSTRACT_TYPE_KEYWORD");
  IElementType AND_SYM = new JuliaTokenType("AND_SYM");
  IElementType ARROW_SYM = new JuliaTokenType("ARROW_SYM");
  IElementType ASSIGN_SYM = new JuliaTokenType("ASSIGN_SYM");
  IElementType BAREMODULE_KEYWORD = new JuliaTokenType("BAREMODULE_KEYWORD");
  IElementType BEGIN_KEYWORD = new JuliaTokenType("BEGIN_KEYWORD");
  IElementType BITWISE_AND_ASSIGN_SYM = new JuliaTokenType("BITWISE_AND_ASSIGN_SYM");
  IElementType BITWISE_AND_SYM = new JuliaTokenType("BITWISE_AND_SYM");
  IElementType BITWISE_NOT_SYM = new JuliaTokenType("BITWISE_NOT_SYM");
  IElementType BITWISE_OR_ASSIGN_SYM = new JuliaTokenType("BITWISE_OR_ASSIGN_SYM");
  IElementType BITWISE_OR_SYM = new JuliaTokenType("BITWISE_OR_SYM");
  IElementType BITWISE_XOR_ASSIGN_SYM = new JuliaTokenType("BITWISE_XOR_ASSIGN_SYM");
  IElementType BITWISE_XOR_SYM = new JuliaTokenType("BITWISE_XOR_SYM");
  IElementType BLOCK_COMMENT_BODY = new JuliaTokenType("BLOCK_COMMENT_BODY");
  IElementType BLOCK_COMMENT_END = new JuliaTokenType("BLOCK_COMMENT_END");
  IElementType BLOCK_COMMENT_START = new JuliaTokenType("BLOCK_COMMENT_START");
  IElementType BREAK_KEYWORD = new JuliaTokenType("BREAK_KEYWORD");
  IElementType BYTE_ARRAY_END = new JuliaTokenType("BYTE_ARRAY_END");
  IElementType BYTE_ARRAY_START = new JuliaTokenType("BYTE_ARRAY_START");
  IElementType CATCH_KEYWORD = new JuliaTokenType("CATCH_KEYWORD");
  IElementType CHAR_LITERAL = new JuliaTokenType("CHAR_LITERAL");
  IElementType CMD_QUOTE_END = new JuliaTokenType("CMD_QUOTE_END");
  IElementType CMD_QUOTE_START = new JuliaTokenType("CMD_QUOTE_START");
  IElementType COLON_ASSIGN_SYM = new JuliaTokenType("COLON_ASSIGN_SYM");
  IElementType COLON_BEGIN_SYM = new JuliaTokenType("COLON_BEGIN_SYM");
  IElementType COLON_SYM = new JuliaTokenType("COLON_SYM");
  IElementType COMMA_SYM = new JuliaTokenType("COMMA_SYM");
  IElementType CONST_KEYWORD = new JuliaTokenType("CONST_KEYWORD");
  IElementType CONTINUE_KEYWORD = new JuliaTokenType("CONTINUE_KEYWORD");
  IElementType DIVIDE_ASSIGN_SYM = new JuliaTokenType("DIVIDE_ASSIGN_SYM");
  IElementType DIVIDE_SYM = new JuliaTokenType("DIVIDE_SYM");
  IElementType DOT_SYM = new JuliaTokenType("DOT_SYM");
  IElementType DOUBLE_COLON = new JuliaTokenType("DOUBLE_COLON");
  IElementType DOUBLE_DOT_SYM = new JuliaTokenType("DOUBLE_DOT_SYM");
  IElementType DO_KEYWORD = new JuliaTokenType("DO_KEYWORD");
  IElementType ELSEIF_KEYWORD = new JuliaTokenType("ELSEIF_KEYWORD");
  IElementType ELSE_KEYWORD = new JuliaTokenType("ELSE_KEYWORD");
  IElementType END_KEYWORD = new JuliaTokenType("END_KEYWORD");
  IElementType EOL = new JuliaTokenType("EOL");
  IElementType EQUALS_SYM = new JuliaTokenType("EQUALS_SYM");
  IElementType EQ_SYM = new JuliaTokenType("EQ_SYM");
  IElementType EXPONENT_ASSIGN_SYM = new JuliaTokenType("EXPONENT_ASSIGN_SYM");
  IElementType EXPONENT_SYM = new JuliaTokenType("EXPONENT_SYM");
  IElementType EXPORT_KEYWORD = new JuliaTokenType("EXPORT_KEYWORD");
  IElementType EXPR_INTERPOLATE_START = new JuliaTokenType("EXPR_INTERPOLATE_START");
  IElementType FACTORISE_ASSIGN_SYM = new JuliaTokenType("FACTORISE_ASSIGN_SYM");
  IElementType FACTORISE_SYM = new JuliaTokenType("FACTORISE_SYM");
  IElementType FALSE_KEYWORD = new JuliaTokenType("FALSE_KEYWORD");
  IElementType FINALLY_KEYWORD = new JuliaTokenType("FINALLY_KEYWORD");
  IElementType FLOAT_CONSTANT = new JuliaTokenType("FLOAT_CONSTANT");
  IElementType FLOAT_LITERAL = new JuliaTokenType("FLOAT_LITERAL");
  IElementType FOR_KEYWORD = new JuliaTokenType("FOR_KEYWORD");
  IElementType FRACTION_ASSIGN_SYM = new JuliaTokenType("FRACTION_ASSIGN_SYM");
  IElementType FRACTION_SYM = new JuliaTokenType("FRACTION_SYM");
  IElementType FUNCTION_KEYWORD = new JuliaTokenType("FUNCTION_KEYWORD");
  IElementType GLOBAL_KEYWORD = new JuliaTokenType("GLOBAL_KEYWORD");
  IElementType GREATER_THAN_OR_EQUAL_SYM = new JuliaTokenType("GREATER_THAN_OR_EQUAL_SYM");
  IElementType GREATER_THAN_SYM = new JuliaTokenType("GREATER_THAN_SYM");
  IElementType IF_KEYWORD = new JuliaTokenType("IF_KEYWORD");
  IElementType IMMUTABLE_KEYWORD = new JuliaTokenType("IMMUTABLE_KEYWORD");
  IElementType IMPLICIT_MULTIPLY_SYM = new JuliaTokenType("IMPLICIT_MULTIPLY_SYM");
  IElementType IMPORTALL_KEYWORD = new JuliaTokenType("IMPORTALL_KEYWORD");
  IElementType IMPORT_KEYWORD = new JuliaTokenType("IMPORT_KEYWORD");
  IElementType INTERPOLATE_SYM = new JuliaTokenType("INTERPOLATE_SYM");
  IElementType INT_LITERAL = new JuliaTokenType("INT_LITERAL");
  IElementType INVERSE_DIV_ASSIGN_SYM = new JuliaTokenType("INVERSE_DIV_ASSIGN_SYM");
  IElementType INVERSE_DIV_SYM = new JuliaTokenType("INVERSE_DIV_SYM");
  IElementType INVERSE_PIPE_SYM = new JuliaTokenType("INVERSE_PIPE_SYM");
  IElementType INVRESE_PIPE_SYM = new JuliaTokenType("INVRESE_PIPE_SYM");
  IElementType IN_KEYWORD = new JuliaTokenType("IN_KEYWORD");
  IElementType IN_SYM = new JuliaTokenType("IN_SYM");
  IElementType ISA_KEYWORD = new JuliaTokenType("ISA_KEYWORD");
  IElementType ISNT_SYM = new JuliaTokenType("ISNT_SYM");
  IElementType IS_SYM = new JuliaTokenType("IS_SYM");
  IElementType LAMBDA_ABSTRACTION = new JuliaTokenType("LAMBDA_ABSTRACTION");
  IElementType LEFT_BRACKET = new JuliaTokenType("LEFT_BRACKET");
  IElementType LEFT_B_BRACKET = new JuliaTokenType("LEFT_B_BRACKET");
  IElementType LEFT_M_BRACKET = new JuliaTokenType("LEFT_M_BRACKET");
  IElementType LESS_THAN_OR_EQUAL_SYM = new JuliaTokenType("LESS_THAN_OR_EQUAL_SYM");
  IElementType LESS_THAN_SYM = new JuliaTokenType("LESS_THAN_SYM");
  IElementType LET_KEYWORD = new JuliaTokenType("LET_KEYWORD");
  IElementType LINE_COMMENT = new JuliaTokenType("LINE_COMMENT");
  IElementType LOCAL_KEYWORD = new JuliaTokenType("LOCAL_KEYWORD");
  IElementType MACRO_KEYWORD = new JuliaTokenType("MACRO_KEYWORD");
  IElementType MACRO_SYM = new JuliaTokenType("MACRO_SYM");
  IElementType MINUS_ASSIGN_SYM = new JuliaTokenType("MINUS_ASSIGN_SYM");
  IElementType MINUS_SYM = new JuliaTokenType("MINUS_SYM");
  IElementType MISC_ARROW_SYM = new JuliaTokenType("MISC_ARROW_SYM");
  IElementType MISC_COMPARISON_SYM = new JuliaTokenType("MISC_COMPARISON_SYM");
  IElementType MISC_EXPONENT_SYM = new JuliaTokenType("MISC_EXPONENT_SYM");
  IElementType MISC_MULTIPLY_SYM = new JuliaTokenType("MISC_MULTIPLY_SYM");
  IElementType MISC_PLUS_SYM = new JuliaTokenType("MISC_PLUS_SYM");
  IElementType MODULE_KEYWORD = new JuliaTokenType("MODULE_KEYWORD");
  IElementType MULTIPLY_ASSIGN_SYM = new JuliaTokenType("MULTIPLY_ASSIGN_SYM");
  IElementType MULTIPLY_SYM = new JuliaTokenType("MULTIPLY_SYM");
  IElementType MUTABLE_KEYWORD = new JuliaTokenType("MUTABLE_KEYWORD");
  IElementType NOT_SYM = new JuliaTokenType("NOT_SYM");
  IElementType OR_SYM = new JuliaTokenType("OR_SYM");
  IElementType PIPE_SYM = new JuliaTokenType("PIPE_SYM");
  IElementType PLUS_ASSIGN_SYM = new JuliaTokenType("PLUS_ASSIGN_SYM");
  IElementType PLUS_SYM = new JuliaTokenType("PLUS_SYM");
  IElementType PRIMITIVE_TYPE_KEYWORD = new JuliaTokenType("PRIMITIVE_TYPE_KEYWORD");
  IElementType QUESTION_SYM = new JuliaTokenType("QUESTION_SYM");
  IElementType QUOTE_END = new JuliaTokenType("QUOTE_END");
  IElementType QUOTE_KEYWORD = new JuliaTokenType("QUOTE_KEYWORD");
  IElementType QUOTE_START = new JuliaTokenType("QUOTE_START");
  IElementType RAW_STR_END = new JuliaTokenType("RAW_STR_END");
  IElementType RAW_STR_START = new JuliaTokenType("RAW_STR_START");
  IElementType REGEX_END = new JuliaTokenType("REGEX_END");
  IElementType REGEX_START = new JuliaTokenType("REGEX_START");
  IElementType REGULAR_STRING_PART_LITERAL = new JuliaTokenType("REGULAR_STRING_PART_LITERAL");
  IElementType REMAINDER_ASSIGN_SYM = new JuliaTokenType("REMAINDER_ASSIGN_SYM");
  IElementType REMAINDER_SYM = new JuliaTokenType("REMAINDER_SYM");
  IElementType RETURN_KEYWORD = new JuliaTokenType("RETURN_KEYWORD");
  IElementType RIGHT_BRACKET = new JuliaTokenType("RIGHT_BRACKET");
  IElementType RIGHT_B_BRACKET = new JuliaTokenType("RIGHT_B_BRACKET");
  IElementType RIGHT_M_BRACKET = new JuliaTokenType("RIGHT_M_BRACKET");
  IElementType SEMICOLON_SYM = new JuliaTokenType("SEMICOLON_SYM");
  IElementType SHL_ASSIGN_SYM = new JuliaTokenType("SHL_ASSIGN_SYM");
  IElementType SHL_SYM = new JuliaTokenType("SHL_SYM");
  IElementType SHORT_INTERPOLATE_SYM = new JuliaTokenType("SHORT_INTERPOLATE_SYM");
  IElementType SHR_ASSIGN_SYM = new JuliaTokenType("SHR_ASSIGN_SYM");
  IElementType SHR_SYM = new JuliaTokenType("SHR_SYM");
  IElementType SLICE_SYM = new JuliaTokenType("SLICE_SYM");
  IElementType SPECIAL_ARROW_SYM = new JuliaTokenType("SPECIAL_ARROW_SYM");
  IElementType STRING_ESCAPE = new JuliaTokenType("STRING_ESCAPE");
  IElementType STRING_INTERPOLATE_END = new JuliaTokenType("STRING_INTERPOLATE_END");
  IElementType STRING_INTERPOLATE_START = new JuliaTokenType("STRING_INTERPOLATE_START");
  IElementType STRING_UNICODE = new JuliaTokenType("STRING_UNICODE");
  IElementType STRUCT_KEYWORD = new JuliaTokenType("STRUCT_KEYWORD");
  IElementType SUBTYPE_SYM = new JuliaTokenType("SUBTYPE_SYM");
  IElementType SUPERTYPE_SYM = new JuliaTokenType("SUPERTYPE_SYM");
  IElementType SYM = new JuliaTokenType("SYM");
  IElementType TRANSPOSE_SYM = new JuliaTokenType("TRANSPOSE_SYM");
  IElementType TRIPLE_QUOTE_END = new JuliaTokenType("TRIPLE_QUOTE_END");
  IElementType TRIPLE_QUOTE_START = new JuliaTokenType("TRIPLE_QUOTE_START");
  IElementType TRUE_KEYWORD = new JuliaTokenType("TRUE_KEYWORD");
  IElementType TRY_KEYWORD = new JuliaTokenType("TRY_KEYWORD");
  IElementType TYPEALIAS_KEYWORD = new JuliaTokenType("TYPEALIAS_KEYWORD");
  IElementType UNEQUAL_SYM = new JuliaTokenType("UNEQUAL_SYM");
  IElementType UNION_KEYWORD = new JuliaTokenType("UNION_KEYWORD");
  IElementType USHR_ASSIGN_SYM = new JuliaTokenType("USHR_ASSIGN_SYM");
  IElementType USHR_SYM = new JuliaTokenType("USHR_SYM");
  IElementType USING_KEYWORD = new JuliaTokenType("USING_KEYWORD");
  IElementType VERSION_END = new JuliaTokenType("VERSION_END");
  IElementType VERSION_START = new JuliaTokenType("VERSION_START");
  IElementType WHERE_KEYWORD = new JuliaTokenType("WHERE_KEYWORD");
  IElementType WHILE_KEYWORD = new JuliaTokenType("WHILE_KEYWORD");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ABSTRACT_TYPE_DECLARATION) {
        return new JuliaAbstractTypeDeclarationImpl(node);
      }
      else if (type == AND_OP) {
        return new JuliaAndOpImpl(node);
      }
      else if (type == APPLY_FUNCTION_OP) {
        return new JuliaApplyFunctionOpImpl(node);
      }
      else if (type == APPLY_INDEX_OP) {
        return new JuliaApplyIndexOpImpl(node);
      }
      else if (type == APPLY_MACRO_OP) {
        return new JuliaApplyMacroOpImpl(node);
      }
      else if (type == ARGUMENTS) {
        return new JuliaArgumentsImpl(node);
      }
      else if (type == ARRAY) {
        return new JuliaArrayImpl(node);
      }
      else if (type == ARROW_OP) {
        return new JuliaArrowOpImpl(node);
      }
      else if (type == ASSIGN_LEVEL_OP) {
        return new JuliaAssignLevelOpImpl(node);
      }
      else if (type == ASSIGN_LEVEL_OPERATOR) {
        return new JuliaAssignLevelOperatorImpl(node);
      }
      else if (type == ASSIGN_LEVEL_OPERATOR_INDEXING) {
        return new JuliaAssignLevelOperatorIndexingImpl(node);
      }
      else if (type == ASSIGN_OP) {
        return new JuliaAssignOpImpl(node);
      }
      else if (type == BEGIN_BLOCK) {
        return new JuliaBeginBlockImpl(node);
      }
      else if (type == BITWISE_LEVEL_OP) {
        return new JuliaBitwiseLevelOpImpl(node);
      }
      else if (type == BITWISE_LEVEL_OPERATOR) {
        return new JuliaBitwiseLevelOperatorImpl(node);
      }
      else if (type == BIT_WISE_NOT_OP) {
        return new JuliaBitWiseNotOpImpl(node);
      }
      else if (type == BOOLEAN_LIT) {
        return new JuliaBooleanLitImpl(node);
      }
      else if (type == BRACKETED_COMPREHENSION_EXPR) {
        return new JuliaBracketedComprehensionExprImpl(node);
      }
      else if (type == BRACKETED_EXPR) {
        return new JuliaBracketedExprImpl(node);
      }
      else if (type == BRACKETED_EXPR_INDEXING) {
        return new JuliaBracketedExprIndexingImpl(node);
      }
      else if (type == BREAK_EXPR) {
        return new JuliaBreakExprImpl(node);
      }
      else if (type == BYTE_ARRAY) {
        return new JuliaByteArrayImpl(node);
      }
      else if (type == CATCH_CLAUSE) {
        return new JuliaCatchClauseImpl(node);
      }
      else if (type == CHAR_LIT) {
        return new JuliaCharLitImpl(node);
      }
      else if (type == COLON_BLOCK) {
        return new JuliaColonBlockImpl(node);
      }
      else if (type == COMMAND) {
        return new JuliaCommandImpl(node);
      }
      else if (type == COMMENT) {
        return new JuliaCommentImpl(node);
      }
      else if (type == COMPACT_FUNCTION) {
        return new JuliaCompactFunctionImpl(node);
      }
      else if (type == COMPARISON_LEVEL_OP) {
        return new JuliaComparisonLevelOpImpl(node);
      }
      else if (type == COMPARISON_LEVEL_OPERATOR) {
        return new JuliaComparisonLevelOperatorImpl(node);
      }
      else if (type == COMPOUND_QUOTE_OP) {
        return new JuliaCompoundQuoteOpImpl(node);
      }
      else if (type == COMPREHENSION_ELEMENT) {
        return new JuliaComprehensionElementImpl(node);
      }
      else if (type == CONTINUE_EXPR) {
        return new JuliaContinueExprImpl(node);
      }
      else if (type == DOT_APPLY_FUNCTION_OP) {
        return new JuliaDotApplyFunctionOpImpl(node);
      }
      else if (type == DOT_APPLY_FUNCTION_OP_INDEXING) {
        return new JuliaDotApplyFunctionOpIndexingImpl(node);
      }
      else if (type == DO_BLOCK) {
        return new JuliaDoBlockImpl(node);
      }
      else if (type == ELSE_CLAUSE) {
        return new JuliaElseClauseImpl(node);
      }
      else if (type == ELSE_IF_CLAUSE) {
        return new JuliaElseIfClauseImpl(node);
      }
      else if (type == END) {
        return new JuliaEndImpl(node);
      }
      else if (type == EXPONENT_OP) {
        return new JuliaExponentOpImpl(node);
      }
      else if (type == EXPORT) {
        return new JuliaExportImpl(node);
      }
      else if (type == EXPR_INTERPOLATE_OP) {
        return new JuliaExprInterpolateOpImpl(node);
      }
      else if (type == EXPR_WRAPPER) {
        return new JuliaExprWrapperImpl(node);
      }
      else if (type == FINALLY_CLAUSE) {
        return new JuliaFinallyClauseImpl(node);
      }
      else if (type == FLOAT_LIT) {
        return new JuliaFloatLitImpl(node);
      }
      else if (type == FOR_COMPREHENSION) {
        return new JuliaForComprehensionImpl(node);
      }
      else if (type == FOR_EXPR) {
        return new JuliaForExprImpl(node);
      }
      else if (type == FRACTION_OP) {
        return new JuliaFractionOpImpl(node);
      }
      else if (type == FUNCTION) {
        return new JuliaFunctionImpl(node);
      }
      else if (type == FUNCTION_SIGNATURE) {
        return new JuliaFunctionSignatureImpl(node);
      }
      else if (type == GLOBAL_STATEMENT) {
        return new JuliaGlobalStatementImpl(node);
      }
      else if (type == IF_EXPR) {
        return new JuliaIfExprImpl(node);
      }
      else if (type == IMPLICIT_MULTIPLY_OP) {
        return new JuliaImplicitMultiplyOpImpl(node);
      }
      else if (type == IMPORT_ALL_EXPR) {
        return new JuliaImportAllExprImpl(node);
      }
      else if (type == IMPORT_EXPR) {
        return new JuliaImportExprImpl(node);
      }
      else if (type == INTEGER) {
        return new JuliaIntegerImpl(node);
      }
      else if (type == IN_OP) {
        return new JuliaInOpImpl(node);
      }
      else if (type == ISA_OP) {
        return new JuliaIsaOpImpl(node);
      }
      else if (type == LAMBDA) {
        return new JuliaLambdaImpl(node);
      }
      else if (type == LET) {
        return new JuliaLetImpl(node);
      }
      else if (type == MACRO) {
        return new JuliaMacroImpl(node);
      }
      else if (type == MACRO_SYMBOL) {
        return new JuliaMacroSymbolImpl(node);
      }
      else if (type == MEMBER_ACCESS) {
        return new JuliaMemberAccessImpl(node);
      }
      else if (type == MEMBER_ACCESS_OP) {
        return new JuliaMemberAccessOpImpl(node);
      }
      else if (type == MISC_ARROWS_OP) {
        return new JuliaMiscArrowsOpImpl(node);
      }
      else if (type == MISC_EXPONENT_OP) {
        return new JuliaMiscExponentOpImpl(node);
      }
      else if (type == MODULE_DECLARATION) {
        return new JuliaModuleDeclarationImpl(node);
      }
      else if (type == MULTIPLY_INDEXING) {
        return new JuliaMultiplyIndexingImpl(node);
      }
      else if (type == MULTIPLY_LEVEL_OP) {
        return new JuliaMultiplyLevelOpImpl(node);
      }
      else if (type == MULTIPLY_LEVEL_OPERATOR) {
        return new JuliaMultiplyLevelOperatorImpl(node);
      }
      else if (type == MULTI_ASSIGN_OP) {
        return new JuliaMultiAssignOpImpl(node);
      }
      else if (type == MULTI_INDEXER) {
        return new JuliaMultiIndexerImpl(node);
      }
      else if (type == NOT_OP) {
        return new JuliaNotOpImpl(node);
      }
      else if (type == OP_AS_SYMBOL) {
        return new JuliaOpAsSymbolImpl(node);
      }
      else if (type == OR_OP) {
        return new JuliaOrOpImpl(node);
      }
      else if (type == PIPE_LEVEL_OP) {
        return new JuliaPipeLevelOpImpl(node);
      }
      else if (type == PIPE_LEVEL_OPERATOR) {
        return new JuliaPipeLevelOperatorImpl(node);
      }
      else if (type == PLUS_INDEXING) {
        return new JuliaPlusIndexingImpl(node);
      }
      else if (type == PLUS_LEVEL_OP) {
        return new JuliaPlusLevelOpImpl(node);
      }
      else if (type == PLUS_LEVEL_OPERATOR) {
        return new JuliaPlusLevelOperatorImpl(node);
      }
      else if (type == PRIMITIVE_TYPE_DECLARATION) {
        return new JuliaPrimitiveTypeDeclarationImpl(node);
      }
      else if (type == QUOTE_INDEXING) {
        return new JuliaQuoteIndexingImpl(node);
      }
      else if (type == QUOTE_OP) {
        return new JuliaQuoteOpImpl(node);
      }
      else if (type == RANGE_INDEXING) {
        return new JuliaRangeIndexingImpl(node);
      }
      else if (type == RANGE_OP) {
        return new JuliaRangeOpImpl(node);
      }
      else if (type == RAW_STRING) {
        return new JuliaRawStringImpl(node);
      }
      else if (type == REGEX) {
        return new JuliaRegexImpl(node);
      }
      else if (type == RETURN_EXPR) {
        return new JuliaReturnExprImpl(node);
      }
      else if (type == SINGLE_COMPREHENSION) {
        return new JuliaSingleComprehensionImpl(node);
      }
      else if (type == SINGLE_INDEXER) {
        return new JuliaSingleIndexerImpl(node);
      }
      else if (type == SPLICE_INDEXING) {
        return new JuliaSpliceIndexingImpl(node);
      }
      else if (type == SPLICE_OP) {
        return new JuliaSpliceOpImpl(node);
      }
      else if (type == STATEMENTS) {
        return new JuliaStatementsImpl(node);
      }
      else if (type == STRING) {
        return new JuliaStringImpl(node);
      }
      else if (type == STRING_CONTENT) {
        return new JuliaStringContentImpl(node);
      }
      else if (type == STRING_LIKE_MULTIPLY_OP) {
        return new JuliaStringLikeMultiplyOpImpl(node);
      }
      else if (type == SYMBOL) {
        return new JuliaSymbolImpl(node);
      }
      else if (type == SYMBOL_LHS) {
        return new JuliaSymbolLhsImpl(node);
      }
      else if (type == TEMPLATE) {
        return new JuliaTemplateImpl(node);
      }
      else if (type == TERNARY_OP) {
        return new JuliaTernaryOpImpl(node);
      }
      else if (type == TERNARY_OP_INDEXING) {
        return new JuliaTernaryOpIndexingImpl(node);
      }
      else if (type == TRANSPOSE_OP) {
        return new JuliaTransposeOpImpl(node);
      }
      else if (type == TRY_CATCH) {
        return new JuliaTryCatchImpl(node);
      }
      else if (type == TUPLE) {
        return new JuliaTupleImpl(node);
      }
      else if (type == TYPE) {
        return new JuliaTypeImpl(node);
      }
      else if (type == TYPED_NAMED_VARIABLE) {
        return new JuliaTypedNamedVariableImpl(node);
      }
      else if (type == TYPE_ALIAS) {
        return new JuliaTypeAliasImpl(node);
      }
      else if (type == TYPE_ANNOTATION) {
        return new JuliaTypeAnnotationImpl(node);
      }
      else if (type == TYPE_DECLARATION) {
        return new JuliaTypeDeclarationImpl(node);
      }
      else if (type == TYPE_OP) {
        return new JuliaTypeOpImpl(node);
      }
      else if (type == TYPE_PARAMETERS) {
        return new JuliaTypeParametersImpl(node);
      }
      else if (type == UNARY_INTERPOLATE_OP) {
        return new JuliaUnaryInterpolateOpImpl(node);
      }
      else if (type == UNARY_MINUS_OP) {
        return new JuliaUnaryMinusOpImpl(node);
      }
      else if (type == UNARY_OP_AS_SYMBOL) {
        return new JuliaUnaryOpAsSymbolImpl(node);
      }
      else if (type == UNARY_PLUS_OP) {
        return new JuliaUnaryPlusOpImpl(node);
      }
      else if (type == UNARY_SUBTYPE_OP) {
        return new JuliaUnarySubtypeOpImpl(node);
      }
      else if (type == UNARY_TYPE_OP) {
        return new JuliaUnaryTypeOpImpl(node);
      }
      else if (type == UNTYPED_VARIABLES) {
        return new JuliaUntypedVariablesImpl(node);
      }
      else if (type == USER_TYPE) {
        return new JuliaUserTypeImpl(node);
      }
      else if (type == USING) {
        return new JuliaUsingImpl(node);
      }
      else if (type == VERSION_NUMBER) {
        return new JuliaVersionNumberImpl(node);
      }
      else if (type == WHERE_CLAUSE) {
        return new JuliaWhereClauseImpl(node);
      }
      else if (type == WHILE_EXPR) {
        return new JuliaWhileExprImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
