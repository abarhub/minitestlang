package org.minitestlang.ast.expr;

import com.google.common.base.Verify;
import org.minitestlang.ast.PositionAST;

public record BooleanExpressionAST(boolean value, PositionAST position) implements ExpressionAST {
    public BooleanExpressionAST {
        Verify.verifyNotNull(position, "position is null");
    }
}
