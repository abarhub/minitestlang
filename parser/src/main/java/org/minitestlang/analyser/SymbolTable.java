package org.minitestlang.analyser;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final SymbolTable symbolTableParent;
    private final Map<String, VarAnalyser> variables = new HashMap<>();

    public SymbolTable() {
        symbolTableParent = null;
    }

    public SymbolTable(final SymbolTable symbolTableParent) {
        this.symbolTableParent = symbolTableParent;
    }

    public boolean has(String name) {
        if (variables.containsKey(name)) {
            return true;
        }
        if (symbolTableParent != null) {
            return symbolTableParent.has(name);
        }
        return false;
    }

    public VarAnalyser get(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        if (symbolTableParent != null) {
            return symbolTableParent.get(name);
        }
        return null;
    }

    public void put(String name, VarAnalyser analyser) {
        variables.put(name, analyser);
    }

}
