package org.minitestlang.runtime;

import org.minitestlang.ast.type.StringTypeAST;
import org.minitestlang.ast.type.TypeAST;

import java.util.Optional;

public class BuiltinTable {

    private final BuiltinString builtinString = new BuiltinString();

    public Optional<BuiltinType> getBuiltinType(TypeAST typeAST) {
        if (typeAST instanceof StringTypeAST) {
            return Optional.of(builtinString);
        } else {
            return Optional.empty();
        }
    }

}
