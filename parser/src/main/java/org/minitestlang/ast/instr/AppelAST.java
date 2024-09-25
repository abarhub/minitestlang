package org.minitestlang.ast.instr;

import org.minitestlang.ast.expr.ExpressionAST;

import java.util.List;
import java.util.Optional;

public record AppelAST(Optional<ExpressionAST> object, String name,
                       List<ExpressionAST> parameters) implements InstructionAST {
}
