package org.minitestlang.ast.expr;

import org.minitestlang.ast.PositionAST;
import org.minitestlang.utils.StringUtils;
import org.minitestlang.utils.VerifyUtils;

public record IdentExpressionAST(String name,PositionAST position) implements ExpressionAST{

    public IdentExpressionAST {
        VerifyUtils.verify(StringUtils.isNotBlank(name),"name cannot be null or empty");
        VerifyUtils.verifyNotNull(position,"position is null");
    }
}
