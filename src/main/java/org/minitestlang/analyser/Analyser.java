package org.minitestlang.analyser;

import org.minitestlang.ast.ClassAST;
import org.minitestlang.ast.MethodAST;
import org.minitestlang.ast.expr.ExpressionAST;
import org.minitestlang.ast.expr.IdentExpressionAST;
import org.minitestlang.ast.instr.AffectAST;
import org.minitestlang.ast.instr.InstructionAST;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Analyser {

    public void analyser(ClassAST ast) throws AnalyserException {
        Optional<MethodAST> optMethod = ast.getMethod("main");
        if (optMethod.isEmpty()) {
            throw new AnalyserException("no main method in class " + ast.getName()+" in position "+ast.getName());
        }
        analyser(optMethod.get());
    }

    private void analyser(MethodAST methodAST) throws AnalyserException {
        if(methodAST.getInstructions()!=null){
            Set<String> variables = new HashSet<>();
            for (InstructionAST instr : methodAST.getInstructions()) {
                if (instr instanceof AffectAST affect) {
                    analyser(affect.getExpression(), variables);
                    if(!variables.contains(affect.getVariable())){
                        throw new AnalyserException("variable " + affect.getVariable()+" is not affected in position "+affect.getPositionVariable());
                    }
                    variables.add(affect.getVariable());
                }
            }
        }
    }

    private void analyser(ExpressionAST expression, Set<String> variables) throws AnalyserException {
        if (expression instanceof IdentExpressionAST identExpressionAST) {
            if(!variables.contains(identExpressionAST.name())){
                throw new AnalyserException("variable " + identExpressionAST.name()+" is not affected in position "+identExpressionAST.positionName());
            }
        }
    }

}
