package org.minitestlang.ast.expr;

import org.minitestlang.ast.PositionAST;

import java.util.List;
import java.util.Optional;

public record AppelExpressionAST(Optional<ExpressionAST> objet, String nom, List<ExpressionAST> parameters,
                                 PositionAST position) implements ExpressionAST {
}
