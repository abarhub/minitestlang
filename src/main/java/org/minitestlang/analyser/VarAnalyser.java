package org.minitestlang.analyser;

import com.google.common.base.Verify;
import org.minitestlang.ast.type.TypeAST;

public class VarAnalyser {

    private final TypeAST type;
    private boolean affected;

    public VarAnalyser(TypeAST type,boolean affected) {
        Verify.verifyNotNull(type, "type is null");
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
