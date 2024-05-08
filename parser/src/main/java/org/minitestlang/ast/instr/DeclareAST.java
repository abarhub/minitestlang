package org.minitestlang.ast.instr;

import org.minitestlang.ast.PositionAST;
import org.minitestlang.ast.expr.ExpressionAST;
import org.minitestlang.ast.type.TypeAST;

import java.util.Optional;

public record DeclareAST(TypeAST type, String name,
                         Optional<ExpressionAST> value,
                         PositionAST positionAST) implements InstructionAST {


}
