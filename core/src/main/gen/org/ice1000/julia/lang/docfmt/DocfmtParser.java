// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.docfmt;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static org.ice1000.julia.lang.docfmt.psi.DocfmtTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class DocfmtParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType type, PsiBuilder builder) {
    parseLight(type, builder);
    return builder.getTreeBuilt();
  }

  public void parseLight(IElementType type, PsiBuilder builder) {
    boolean result;
    builder = adapt_builder_(type, builder, this, null);
    Marker marker = enter_section_(builder, 0, _COLLAPSE_, null);
    result = parse_root_(type, builder);
    exit_section_(builder, 0, marker, type, result, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType type, PsiBuilder builder) {
    return parse_root_(type, builder, 0);
  }

  static boolean parse_root_(IElementType type, PsiBuilder builder, int level) {
    return configFile(builder, level + 1);
  }

  /* ********************************************************** */
  // SYM EQ_SYM value
  public static boolean config(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "config")) return false;
    if (!nextTokenIs(builder, SYM)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokens(builder, 0, SYM, EQ_SYM);
    result = result && value(builder, level + 1);
    exit_section_(builder, marker, CONFIG, result);
    return result;
  }

  /* ********************************************************** */
  // EOL* ((config | LINE_COMMENT) (EOL+ (config | LINE_COMMENT))*)? EOL*
  static boolean configFile(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "configFile")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = configFile_0(builder, level + 1);
    result = result && configFile_1(builder, level + 1);
    result = result && configFile_2(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // EOL*
  private static boolean configFile_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "configFile_0")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!consumeToken(builder, EOL)) break;
      if (!empty_element_parsed_guard_(builder, "configFile_0", pos)) break;
    }
    return true;
  }

  // ((config | LINE_COMMENT) (EOL+ (config | LINE_COMMENT))*)?
  private static boolean configFile_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "configFile_1")) return false;
    configFile_1_0(builder, level + 1);
    return true;
  }

  // (config | LINE_COMMENT) (EOL+ (config | LINE_COMMENT))*
  private static boolean configFile_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "configFile_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = configFile_1_0_0(builder, level + 1);
    result = result && configFile_1_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // config | LINE_COMMENT
  private static boolean configFile_1_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "configFile_1_0_0")) return false;
    boolean result;
    result = config(builder, level + 1);
    if (!result) result = consumeToken(builder, LINE_COMMENT);
    return result;
  }

  // (EOL+ (config | LINE_COMMENT))*
  private static boolean configFile_1_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "configFile_1_0_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!configFile_1_0_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "configFile_1_0_1", pos)) break;
    }
    return true;
  }

  // EOL+ (config | LINE_COMMENT)
  private static boolean configFile_1_0_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "configFile_1_0_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = configFile_1_0_1_0_0(builder, level + 1);
    result = result && configFile_1_0_1_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // EOL+
  private static boolean configFile_1_0_1_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "configFile_1_0_1_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, EOL);
    while (result) {
      int pos = current_position_(builder);
      if (!consumeToken(builder, EOL)) break;
      if (!empty_element_parsed_guard_(builder, "configFile_1_0_1_0_0", pos)) break;
    }
    exit_section_(builder, marker, null, result);
    return result;
  }

  // config | LINE_COMMENT
  private static boolean configFile_1_0_1_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "configFile_1_0_1_0_1")) return false;
    boolean result;
    result = config(builder, level + 1);
    if (!result) result = consumeToken(builder, LINE_COMMENT);
    return result;
  }

  // EOL*
  private static boolean configFile_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "configFile_2")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!consumeToken(builder, EOL)) break;
      if (!empty_element_parsed_guard_(builder, "configFile_2", pos)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // SYM | INT
  public static boolean value(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "value")) return false;
    if (!nextTokenIs(builder, "<value>", INT, SYM)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, VALUE, "<value>");
    result = consumeToken(builder, SYM);
    if (!result) result = consumeToken(builder, INT);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

}
