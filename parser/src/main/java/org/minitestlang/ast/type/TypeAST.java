package org.minitestlang.ast.type;

public sealed interface TypeAST permits BooleanTypeAST, CharTypeAST, IntTypeAST,
        ObjetTypeAST, StringTypeAST, VoidTypeAST {
}
