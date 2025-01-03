package org.minitestlang.ast.type;

import org.minitestlang.ast.PositionAST;

public record StringTypeAST(PositionAST position) implements TypeAST {

    public final static String NAME = "String";

}
