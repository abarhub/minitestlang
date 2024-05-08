package org.minitestlang.ast.instr;

import org.minitestlang.ast.expr.ExpressionAST;

import java.util.List;

public record WhileAST(ExpressionAST expr,
                       List<InstructionAST> block) implements InstructionAST {
}
