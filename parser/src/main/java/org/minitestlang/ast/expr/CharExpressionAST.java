package org.minitestlang.ast.expr;

import org.minitestlang.ast.PositionAST;
import org.minitestlang.utils.VerifyUtils;

public record CharExpressionAST(char value, PositionAST position) implements ExpressionAST {
    public CharExpressionAST {
        VerifyUtils.verifyNotNull(position, "position is null");
    }
}
