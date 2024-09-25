package org.minitestlang.ast.instr;

public sealed interface InstructionAST permits AffectAST, AppelAST, BlockAST, DeclareAST, IfAST, WhileAST {
}
