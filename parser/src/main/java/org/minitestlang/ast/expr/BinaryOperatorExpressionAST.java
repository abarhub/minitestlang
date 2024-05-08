package org.minitestlang.ast.expr;


import org.minitestlang.ast.PositionAST;
import org.minitestlang.utils.VerifyUtils;

public record BinaryOperatorExpressionAST(ExpressionAST left,
                                          ExpressionAST right,
                                          Operator operator,
                                          PositionAST position) implements ExpressionAST {
    public BinaryOperatorExpressionAST {
        VerifyUtils.verifyNotNull(operator, "operator cannot be null");
        VerifyUtils.verifyNotNull(left, "left cannot be null");
        VerifyUtils.verifyNotNull(right, "right cannot be null");
        VerifyUtils.verifyNotNull(position, "position cannot be null");
    }
}
