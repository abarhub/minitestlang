package org.minitestlang.ast.instr;

import org.minitestlang.ast.expr.ExpressionAST;

import java.util.List;

public record AppelAST(String name, List<ExpressionAST> parameters) implements InstructionAST{
}
