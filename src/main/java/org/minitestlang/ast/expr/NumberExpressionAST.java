package org.minitestlang.ast.expr;

import com.google.common.base.Verify;
import org.minitestlang.ast.PositionAST;

public record NumberExpressionAST(int number, PositionAST position) implements ExpressionAST {

    public NumberExpressionAST {
        Verify.verifyNotNull(position, "position is null");
    }
}
