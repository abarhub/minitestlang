package org.minitestlang.ast.expr;

import com.google.common.base.Verify;

public record BinaryOperatorExpressionAST(ExpressionAST left,
                                          ExpressionAST right,
                                          Operator operator) implements ExpressionAST {
    public BinaryOperatorExpressionAST {
        Verify.verify(operator != null, "operator cannot be null");
        Verify.verify(left != null, "left cannot be null");
        Verify.verify(right != null, "right cannot be null");
    }
}
