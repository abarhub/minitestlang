package org.minitestlang.ast;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    public Optional<MethodAST> getMethod(String name){
        if(methods==null) {
            return Optional.empty();
        }else {
            return methods.stream()
                    .filter(x-> Objects.equals(name,x.getName()))
                    .findAny();
        }
    }
}
