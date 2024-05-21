package org.minitestlang.ast.expr;

import org.minitestlang.ast.PositionAST;

public record StringAST(String str, PositionAST position) implements ExpressionAST{
}
