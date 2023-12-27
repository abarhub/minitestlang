package org.minitestlang.ast.expr;

public class NumberExpressionAST implements ExpressionAST{

    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
