package org.minitestlang.ast.expr;

import com.google.common.base.Strings;
import com.google.common.base.Verify;
import org.minitestlang.ast.PositionAST;

public record IdentExpressionAST(String name,PositionAST position) implements ExpressionAST{

    public IdentExpressionAST {
        Verify.verify(!Strings.isNullOrEmpty(name));
        Verify.verify(position != null);
    }
}
