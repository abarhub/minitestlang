package org.minitestlang.analyser;

import org.minitestlang.ast.type.TypeAST;
import org.minitestlang.utils.VerifyUtils;

public class VarAnalyser {

    private final TypeAST type;
    private boolean affected;

    public VarAnalyser(TypeAST type, boolean affected) {
        VerifyUtils.verifyNotNull(type, "type is null");
        this.type = type;
        this.affected = affected;
    }

    public TypeAST getType() {
        return type;
    }

    public boolean isAffected() {
        return affected;
    }

    public void setAffected(boolean affected) {
        this.affected = affected;
    }
}
