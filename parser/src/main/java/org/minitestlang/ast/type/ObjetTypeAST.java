package org.minitestlang.ast.type;

import org.minitestlang.ast.PositionAST;

public record ObjetTypeAST(String identifier, PositionAST position) implements TypeAST {
}
