package org.minitestlang.listener.minitestlang.result;

import org.minitestlang.ast.instr.InstructionAST;

import java.util.List;

public record ResultInstr(List<InstructionAST> instructions) {
}
