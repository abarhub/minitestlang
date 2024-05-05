package org.minitestlang.ast.instr;

import org.minitestlang.ast.expr.ExpressionAST;

import java.util.List;

public record IfAST(ExpressionAST expr,
                    List<InstructionAST> block,
                    List<InstructionAST> elseBlock) implements InstructionAST{
}
