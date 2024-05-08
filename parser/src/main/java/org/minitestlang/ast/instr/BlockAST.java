package org.minitestlang.ast.instr;

import java.util.List;

public record BlockAST(List<InstructionAST> instr) implements InstructionAST {
}
