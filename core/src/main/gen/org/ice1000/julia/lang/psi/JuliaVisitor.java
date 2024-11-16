// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import org.ice1000.julia.lang.psi.impl.IJuliaRegex;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.ice1000.julia.lang.psi.impl.IJuliaSymbol;
import org.ice1000.julia.lang.psi.impl.IJuliaFunctionDeclaration;
import org.ice1000.julia.lang.psi.impl.IJuliaString;
import org.ice1000.julia.lang.psi.impl.IJuliaTypeDeclaration;
import org.ice1000.julia.lang.psi.impl.IJuliaExpr;
import org.ice1000.julia.lang.psi.impl.DocStringOwner;
import org.ice1000.julia.lang.psi.impl.IJuliaModuleDeclaration;

public class JuliaVisitor<R> extends PsiElementVisitor {

  public R visitAbstractTypeDeclaration(@NotNull JuliaAbstractTypeDeclaration o) {
    return visitIJuliaTypeDeclaration(o);
  }

  public R visitAndOp(@NotNull JuliaAndOp o) {
    return visitExpr(o);
  }

  public R visitApplyFunctionOp(@NotNull JuliaApplyFunctionOp o) {
    return visitExpr(o);
  }

  public R visitApplyIndexOp(@NotNull JuliaApplyIndexOp o) {
    return visitExpr(o);
  }

  public R visitApplyMacroOp(@NotNull JuliaApplyMacroOp o) {
    return visitExpr(o);
  }

  public R visitArguments(@NotNull JuliaArguments o) {
    return visitPsiElement(o);
  }

  public R visitArray(@NotNull JuliaArray o) {
    return visitExpr(o);
  }

  public R visitArrowOp(@NotNull JuliaArrowOp o) {
    return visitExpr(o);
  }

  public R visitAssignLevelOp(@NotNull JuliaAssignLevelOp o) {
    return visitExpr(o);
  }

  public R visitAssignLevelOperator(@NotNull JuliaAssignLevelOperator o) {
    return visitPsiElement(o);
  }

  public R visitAssignLevelOperatorIndexing(@NotNull JuliaAssignLevelOperatorIndexing o) {
    return visitExprOrEnd(o);
  }

  public R visitAssignOp(@NotNull JuliaAssignOp o) {
    return visitExpr(o);
    // visitPsiNameIdentifierOwner(o);
  }

  public R visitBeginBlock(@NotNull JuliaBeginBlock o) {
    return visitExpr(o);
  }

  public R visitBitWiseNotOp(@NotNull JuliaBitWiseNotOp o) {
    return visitExpr(o);
  }

  public R visitBitwiseLevelOp(@NotNull JuliaBitwiseLevelOp o) {
    return visitExpr(o);
  }

  public R visitBitwiseLevelOperator(@NotNull JuliaBitwiseLevelOperator o) {
    return visitPsiElement(o);
  }

  public R visitBooleanLit(@NotNull JuliaBooleanLit o) {
    return visitExpr(o);
  }

  public R visitBracketedComprehensionExpr(@NotNull JuliaBracketedComprehensionExpr o) {
    return visitExpr(o);
  }

  public R visitBracketedExpr(@NotNull JuliaBracketedExpr o) {
    return visitExpr(o);
  }

  public R visitBracketedExprIndexing(@NotNull JuliaBracketedExprIndexing o) {
    return visitExprOrEnd(o);
  }

  public R visitBreakExpr(@NotNull JuliaBreakExpr o) {
    return visitExpr(o);
  }

  public R visitByteArray(@NotNull JuliaByteArray o) {
    return visitExpr(o);
  }

  public R visitCatchClause(@NotNull JuliaCatchClause o) {
    return visitPsiNameIdentifierOwner(o);
  }

  public R visitCharLit(@NotNull JuliaCharLit o) {
    return visitExpr(o);
  }

  public R visitColonBlock(@NotNull JuliaColonBlock o) {
    return visitExpr(o);
  }

  public R visitCommand(@NotNull JuliaCommand o) {
    return visitExpr(o);
  }

  public R visitComment(@NotNull JuliaComment o) {
    return visitPsiElement(o);
  }

  public R visitCompactFunction(@NotNull JuliaCompactFunction o) {
    return visitExpr(o);
    // visitIJuliaFunctionDeclaration(o);
  }

  public R visitComparisonLevelOp(@NotNull JuliaComparisonLevelOp o) {
    return visitExpr(o);
  }

  public R visitComparisonLevelOperator(@NotNull JuliaComparisonLevelOperator o) {
    return visitPsiElement(o);
  }

  public R visitCompoundQuoteOp(@NotNull JuliaCompoundQuoteOp o) {
    return visitExpr(o);
  }

  public R visitComprehensionElement(@NotNull JuliaComprehensionElement o) {
    return visitPsiElement(o);
  }

  public R visitContinueExpr(@NotNull JuliaContinueExpr o) {
    return visitExpr(o);
  }

  public R visitDoBlock(@NotNull JuliaDoBlock o) {
    return visitExpr(o);
  }

  public R visitDotApplyFunctionOp(@NotNull JuliaDotApplyFunctionOp o) {
    return visitExpr(o);
  }

  public R visitDotApplyFunctionOpIndexing(@NotNull JuliaDotApplyFunctionOpIndexing o) {
    return visitExprOrEnd(o);
  }

  public R visitElseClause(@NotNull JuliaElseClause o) {
    return visitPsiElement(o);
  }

  public R visitElseIfClause(@NotNull JuliaElseIfClause o) {
    return visitPsiElement(o);
  }

  public R visitEnd(@NotNull JuliaEnd o) {
    return visitExprOrEnd(o);
  }

  public R visitExponentOp(@NotNull JuliaExponentOp o) {
    return visitExpr(o);
  }

  public R visitExport(@NotNull JuliaExport o) {
    return visitExpr(o);
  }

  public R visitExpr(@NotNull JuliaExpr o) {
    return visitIJuliaExpr(o);
  }

  public R visitExprInterpolateOp(@NotNull JuliaExprInterpolateOp o) {
    return visitExpr(o);
  }

  public R visitExprOrEnd(@NotNull JuliaExprOrEnd o) {
    return visitPsiElement(o);
  }

  public R visitExprWrapper(@NotNull JuliaExprWrapper o) {
    return visitExprOrEnd(o);
  }

  public R visitFinallyClause(@NotNull JuliaFinallyClause o) {
    return visitPsiElement(o);
  }

  public R visitFloatLit(@NotNull JuliaFloatLit o) {
    return visitExpr(o);
  }

  public R visitForComprehension(@NotNull JuliaForComprehension o) {
    return visitExpr(o);
  }

  public R visitForExpr(@NotNull JuliaForExpr o) {
    return visitExpr(o);
  }

  public R visitFractionOp(@NotNull JuliaFractionOp o) {
    return visitExpr(o);
  }

  public R visitFunction(@NotNull JuliaFunction o) {
    return visitExpr(o);
    // visitIJuliaFunctionDeclaration(o);
  }

  public R visitFunctionSignature(@NotNull JuliaFunctionSignature o) {
    return visitPsiElement(o);
  }

  public R visitGlobalStatement(@NotNull JuliaGlobalStatement o) {
    return visitExpr(o);
  }

  public R visitIfExpr(@NotNull JuliaIfExpr o) {
    return visitExpr(o);
  }

  public R visitImplicitMultiplyOp(@NotNull JuliaImplicitMultiplyOp o) {
    return visitExpr(o);
  }

  public R visitImportAllExpr(@NotNull JuliaImportAllExpr o) {
    return visitExpr(o);
  }

  public R visitImportExpr(@NotNull JuliaImportExpr o) {
    return visitExpr(o);
  }

  public R visitInOp(@NotNull JuliaInOp o) {
    return visitExpr(o);
  }

  public R visitInteger(@NotNull JuliaInteger o) {
    return visitExpr(o);
  }

  public R visitIsaOp(@NotNull JuliaIsaOp o) {
    return visitExpr(o);
  }

  public R visitLambda(@NotNull JuliaLambda o) {
    return visitExpr(o);
  }

  public R visitLet(@NotNull JuliaLet o) {
    return visitExpr(o);
  }

  public R visitMacro(@NotNull JuliaMacro o) {
    return visitExpr(o);
    // visitPsiNameIdentifierOwner(o);
    // visitDocStringOwner(o);
  }

  public R visitMacroSymbol(@NotNull JuliaMacroSymbol o) {
    return visitExpr(o);
    // visitPsiNameIdentifierOwner(o);
  }

  public R visitMemberAccess(@NotNull JuliaMemberAccess o) {
    return visitPsiElement(o);
  }

  public R visitMemberAccessOp(@NotNull JuliaMemberAccessOp o) {
    return visitExpr(o);
  }

  public R visitMiscArrowsOp(@NotNull JuliaMiscArrowsOp o) {
    return visitExpr(o);
  }

  public R visitMiscExponentOp(@NotNull JuliaMiscExponentOp o) {
    return visitExpr(o);
  }

  public R visitModuleDeclaration(@NotNull JuliaModuleDeclaration o) {
    return visitIJuliaModuleDeclaration(o);
  }

  public R visitMultiAssignOp(@NotNull JuliaMultiAssignOp o) {
    return visitExpr(o);
  }

  public R visitMultiIndexer(@NotNull JuliaMultiIndexer o) {
    return visitPsiElement(o);
  }

  public R visitMultiplyIndexing(@NotNull JuliaMultiplyIndexing o) {
    return visitExprOrEnd(o);
  }

  public R visitMultiplyLevelOp(@NotNull JuliaMultiplyLevelOp o) {
    return visitExpr(o);
  }

  public R visitMultiplyLevelOperator(@NotNull JuliaMultiplyLevelOperator o) {
    return visitPsiElement(o);
  }

  public R visitNotOp(@NotNull JuliaNotOp o) {
    return visitExpr(o);
  }

  public R visitOpAsSymbol(@NotNull JuliaOpAsSymbol o) {
    return visitExpr(o);
  }

  public R visitOrOp(@NotNull JuliaOrOp o) {
    return visitExpr(o);
  }

  public R visitPipeLevelOp(@NotNull JuliaPipeLevelOp o) {
    return visitExpr(o);
  }

  public R visitPipeLevelOperator(@NotNull JuliaPipeLevelOperator o) {
    return visitPsiElement(o);
  }

  public R visitPlusIndexing(@NotNull JuliaPlusIndexing o) {
    return visitExprOrEnd(o);
  }

  public R visitPlusLevelOp(@NotNull JuliaPlusLevelOp o) {
    return visitExpr(o);
  }

  public R visitPlusLevelOperator(@NotNull JuliaPlusLevelOperator o) {
    return visitPsiElement(o);
  }

  public R visitPrimitiveTypeDeclaration(@NotNull JuliaPrimitiveTypeDeclaration o) {
    return visitExpr(o);
  }

  public R visitQuoteIndexing(@NotNull JuliaQuoteIndexing o) {
    return visitExprOrEnd(o);
  }

  public R visitQuoteOp(@NotNull JuliaQuoteOp o) {
    return visitExpr(o);
  }

  public R visitRangeIndexing(@NotNull JuliaRangeIndexing o) {
    return visitExprOrEnd(o);
  }

  public R visitRangeOp(@NotNull JuliaRangeOp o) {
    return visitExpr(o);
  }

  public R visitRawString(@NotNull JuliaRawString o) {
    return visitExpr(o);
  }

  public R visitRegex(@NotNull JuliaRegex o) {
    return visitExpr(o);
    // visitIJuliaRegex(o);
  }

  public R visitReturnExpr(@NotNull JuliaReturnExpr o) {
    return visitExpr(o);
  }

  public R visitSingleComprehension(@NotNull JuliaSingleComprehension o) {
    return visitPsiNameIdentifierOwner(o);
  }

  public R visitSingleIndexer(@NotNull JuliaSingleIndexer o) {
    return visitPsiElement(o);
  }

  public R visitSpliceIndexing(@NotNull JuliaSpliceIndexing o) {
    return visitExprOrEnd(o);
  }

  public R visitSpliceOp(@NotNull JuliaSpliceOp o) {
    return visitExpr(o);
  }

  public R visitStatements(@NotNull JuliaStatements o) {
    return visitPsiElement(o);
  }

  public R visitString(@NotNull JuliaString o) {
    return visitExpr(o);
    // visitIJuliaString(o);
  }

  public R visitStringContent(@NotNull JuliaStringContent o) {
    return visitPsiElement(o);
  }

  public R visitStringLikeMultiplyOp(@NotNull JuliaStringLikeMultiplyOp o) {
    return visitExpr(o);
  }

  public R visitSymbol(@NotNull JuliaSymbol o) {
    return visitExpr(o);
    // visitIJuliaSymbol(o);
  }

  public R visitSymbolLhs(@NotNull JuliaSymbolLhs o) {
    return visitExpr(o);
  }

  public R visitTemplate(@NotNull JuliaTemplate o) {
    return visitPsiElement(o);
  }

  public R visitTernaryOp(@NotNull JuliaTernaryOp o) {
    return visitExpr(o);
  }

  public R visitTernaryOpIndexing(@NotNull JuliaTernaryOpIndexing o) {
    return visitExprOrEnd(o);
  }

  public R visitTransposeOp(@NotNull JuliaTransposeOp o) {
    return visitExpr(o);
  }

  public R visitTryCatch(@NotNull JuliaTryCatch o) {
    return visitExpr(o);
  }

  public R visitTuple(@NotNull JuliaTuple o) {
    return visitExpr(o);
  }

  public R visitType(@NotNull JuliaType o) {
    return visitExpr(o);
  }

  public R visitTypeAlias(@NotNull JuliaTypeAlias o) {
    return visitExpr(o);
  }

  public R visitTypeAnnotation(@NotNull JuliaTypeAnnotation o) {
    return visitPsiElement(o);
  }

  public R visitTypeDeclaration(@NotNull JuliaTypeDeclaration o) {
    return visitIJuliaTypeDeclaration(o);
  }

  public R visitTypeOp(@NotNull JuliaTypeOp o) {
    return visitExpr(o);
  }

  public R visitTypeParameters(@NotNull JuliaTypeParameters o) {
    return visitPsiElement(o);
  }

  public R visitTypedNamedVariable(@NotNull JuliaTypedNamedVariable o) {
    return visitPsiNameIdentifierOwner(o);
  }

  public R visitUnaryInterpolateOp(@NotNull JuliaUnaryInterpolateOp o) {
    return visitExpr(o);
  }

  public R visitUnaryMinusOp(@NotNull JuliaUnaryMinusOp o) {
    return visitExpr(o);
  }

  public R visitUnaryOpAsSymbol(@NotNull JuliaUnaryOpAsSymbol o) {
    return visitExpr(o);
  }

  public R visitUnaryPlusOp(@NotNull JuliaUnaryPlusOp o) {
    return visitExpr(o);
  }

  public R visitUnarySubtypeOp(@NotNull JuliaUnarySubtypeOp o) {
    return visitExpr(o);
  }

  public R visitUnaryTypeOp(@NotNull JuliaUnaryTypeOp o) {
    return visitExpr(o);
  }

  public R visitUntypedVariables(@NotNull JuliaUntypedVariables o) {
    return visitPsiElement(o);
  }

  public R visitUserType(@NotNull JuliaUserType o) {
    return visitPsiElement(o);
  }

  public R visitUsing(@NotNull JuliaUsing o) {
    return visitExpr(o);
  }

  public R visitVersionNumber(@NotNull JuliaVersionNumber o) {
    return visitExpr(o);
  }

  public R visitWhereClause(@NotNull JuliaWhereClause o) {
    return visitPsiElement(o);
  }

  public R visitWhileExpr(@NotNull JuliaWhileExpr o) {
    return visitExpr(o);
  }

  public R visitPsiNameIdentifierOwner(@NotNull PsiNameIdentifierOwner o) {
    visitElement(o);
    return null;
  }

  public R visitIJuliaExpr(@NotNull IJuliaExpr o) {
    visitElement(o);
    return null;
  }

  public R visitIJuliaModuleDeclaration(@NotNull IJuliaModuleDeclaration o) {
    visitElement(o);
    return null;
  }

  public R visitIJuliaTypeDeclaration(@NotNull IJuliaTypeDeclaration o) {
    visitElement(o);
    return null;
  }

  public R visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
    return null;
  }

}
