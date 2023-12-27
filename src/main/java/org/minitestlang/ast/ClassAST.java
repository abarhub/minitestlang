package org.minitestlang.ast;

import java.util.List;

public class ClassAST {

    private String name;
    private List<MethodAST> methods;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MethodAST> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodAST> methods) {
        this.methods = methods;
    }
}
