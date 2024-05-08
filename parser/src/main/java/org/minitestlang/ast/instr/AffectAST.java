package org.minitestlang.ast.instr;

import org.minitestlang.ast.PositionAST;
import org.minitestlang.ast.expr.ExpressionAST;

public class AffectAST implements InstructionAST {

    private String variable;
    private ExpressionAST expression;
    private PositionAST positionVariable;

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

    public PositionAST getPositionVariable() {
        return positionVariable;
    }

    public void setPositionVariable(PositionAST positionVariable) {
        this.positionVariable = positionVariable;
    }
}
