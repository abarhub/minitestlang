package org.minitestlang.ast.expr;

import org.minitestlang.ast.PositionAST;

public sealed interface ExpressionAST permits AppelExpressionAST, BinaryOperatorExpressionAST, BooleanExpressionAST, CharExpressionAST, IdentExpressionAST, NumberExpressionAST, StringAST {

    public PositionAST position();
}
