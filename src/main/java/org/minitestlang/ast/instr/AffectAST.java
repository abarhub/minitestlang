package org.minitestlang.ast.instr;

import org.minitestlang.ast.expr.ExpressionAST;

public class AffectAST implements InstructionAST {

    private String variable;
    private ExpressionAST expression;

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public ExpressionAST getExpression() {
        return expression;
    }

    public void setExpression(ExpressionAST expression) {
        this.expression = expression;
    }
}
