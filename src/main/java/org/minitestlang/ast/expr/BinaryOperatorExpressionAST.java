package org.minitestlang.ast.expr;

import com.google.common.base.Verify;
import org.minitestlang.ast.PositionAST;

public record BinaryOperatorExpressionAST(ExpressionAST left,
                                          ExpressionAST right,
                                          Operator operator,
                                          PositionAST position) implements ExpressionAST {
    public BinaryOperatorExpressionAST {
        Verify.verify(operator != null, "operator cannot be null");
        Verify.verify(left != null, "left cannot be null");
        Verify.verify(right != null, "right cannot be null");
        Verify.verify(position != null, "position cannot be null");
    }
}
