package org.minitestlang.ast.expr;

public class BinaryOperatorExpressionAST implements ExpressionAST {

    private ExpressionAST left,right;
    private Operator operator;

    public ExpressionAST getLeft() {
        return left;
    }

    public void setLeft(ExpressionAST left) {
        this.left = left;
    }

    public ExpressionAST getRight() {
        return right;
    }

    public void setRight(ExpressionAST right) {
        this.right = right;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }
}
