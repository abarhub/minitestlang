package org.minitestlang.analyser;

import org.minitestlang.ast.ClassAST;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClassTable {

    private final Map<String, ClassAST> classes = new HashMap<>();

    public void ajouterClasse(ClassAST classAST) {
        classes.put(classAST.getName(), classAST);
    }

    public boolean classeExiste(String className) {
        return classes.containsKey(className);
    }

    public Optional<ClassAST> getClasse(String className) {
        return Optional.ofNullable(classes.get(className));
    }

}
