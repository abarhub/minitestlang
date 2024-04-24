package org.minitestlang.analyser;

import org.minitestlang.ast.ClassAST;
import org.minitestlang.ast.MethodAST;
import org.minitestlang.ast.expr.ExpressionAST;
import org.minitestlang.ast.expr.IdentExpressionAST;
import org.minitestlang.ast.instr.AffectAST;
import org.minitestlang.ast.instr.DeclareAST;
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
            Set<String> variablesDeclared = new HashSet<>();
            Set<String> variablesAffected = new HashSet<>();
            for (InstructionAST instr : methodAST.getInstructions()) {
                if (instr instanceof AffectAST affect) {
                    analyser(affect.getExpression(), variablesAffected);
                    if(!variablesDeclared.contains(affect.getVariable())){
                        throw new AnalyserException("variable " + affect.getVariable()+" is not declared in position "+affect.getPositionVariable());
                    }
                    variablesAffected.add(affect.getVariable());
                } else if(instr instanceof DeclareAST declareAST){
                    if(variablesDeclared.contains(declareAST.name())){
                        throw new AnalyserException("variable " + declareAST.name()+" is not already declared in position "+declareAST.positionAST());
                    }
                    variablesDeclared.add(declareAST.name());
                }
            }
        }
    }

    private void analyser(ExpressionAST expression, Set<String> variablesAffected) throws AnalyserException {
        if (expression instanceof IdentExpressionAST identExpressionAST) {
            if(!variablesAffected.contains(identExpressionAST.name())){
                throw new AnalyserException("variable " + identExpressionAST.name()+" is not affected in position "+identExpressionAST.position());
            }
        }
    }

}
