package org.minitestlang.ast.expr;

import org.minitestlang.ast.PositionAST;

public sealed interface ExpressionAST permits IdentExpressionAST, CharExpressionAST,
        BooleanExpressionAST, BinaryOperatorExpressionAST, NumberExpressionAST, StringAST {

    public PositionAST position();
}
