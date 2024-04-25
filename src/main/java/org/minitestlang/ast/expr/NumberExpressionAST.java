package org.minitestlang.ast.expr;

import org.minitestlang.ast.PositionAST;
import org.minitestlang.utils.VerifyUtils;

public record NumberExpressionAST(int number, PositionAST position) implements ExpressionAST {

    public NumberExpressionAST {
        VerifyUtils.verifyNotNull(position, "position is null");
    }
}
