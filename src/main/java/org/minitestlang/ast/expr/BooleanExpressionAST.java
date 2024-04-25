package org.minitestlang.ast.expr;

import org.minitestlang.ast.PositionAST;
import org.minitestlang.utils.VerifyUtils;

public record BooleanExpressionAST(boolean value, PositionAST position) implements ExpressionAST {
    public BooleanExpressionAST {
        VerifyUtils.verifyNotNull(position, "position is null");
    }
}
