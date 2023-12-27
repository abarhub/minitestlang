package org.minitestlang.ast.expr;

public class IdentExpressionAST implements ExpressionAST{

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
