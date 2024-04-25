package org.minitestlang.analyser;

import org.minitestlang.ast.ClassAST;
import org.minitestlang.ast.MethodAST;
import org.minitestlang.ast.expr.*;
import org.minitestlang.ast.instr.AffectAST;
import org.minitestlang.ast.instr.DeclareAST;
import org.minitestlang.ast.instr.InstructionAST;

import java.util.*;

public class Analyser {

    public void analyser(ClassAST ast) throws AnalyserException {
        Optional<MethodAST> optMethod = ast.getMethod("main");
        if (optMethod.isEmpty()) {
            throw new AnalyserException("no main method in class " + ast.getName() + " in position " + ast.getName());
        }
        analyser(optMethod.get());
    }

    private void analyser(MethodAST methodAST) throws AnalyserException {
        if (methodAST.getInstructions() != null) {
            Map<String, VarAnalyser> variables = new HashMap<>();
            for (InstructionAST instr : methodAST.getInstructions()) {
                if (instr instanceof AffectAST affect) {
                    analyser(affect.getExpression(), variables);
                    if (!variables.containsKey(affect.getVariable())) {
                        throw new AnalyserException("variable " + affect.getVariable() +
                                " is not declared in position " + affect.getPositionVariable());
                    }
                    variables.get(affect.getVariable()).setAffected(true);
                } else if (instr instanceof DeclareAST declareAST) {
                    if (variables.containsKey(declareAST.name())) {
                        throw new AnalyserException("variable " + declareAST.name() +
                                " is not already declared in position " + declareAST.positionAST());
                    }
                    variables.put(declareAST.name(), new VarAnalyser(declareAST.type(),
                            declareAST.value().isPresent()));
                }
            }
        }
    }

    private void analyser(ExpressionAST expression, Map<String, VarAnalyser> variables) throws AnalyserException {
        if (expression instanceof IdentExpressionAST identExpressionAST) {
            if (!variables.containsKey(identExpressionAST.name())) {
                throw new AnalyserException("variable " + identExpressionAST.name() +
                        " is not declared in position " + identExpressionAST.position());
            } else if (!variables.get(identExpressionAST.name()).isAffected()) {
                throw new AnalyserException("variable " + identExpressionAST.name() +
                        " is not affected in position " + identExpressionAST.position());
            }
        } else if ((expression instanceof BooleanExpressionAST) || (expression instanceof NumberExpressionAST)) {
            // no check
        } else if (expression instanceof BinaryOperatorExpressionAST binaryOperatorExpressionAST) {
            analyser(binaryOperatorExpressionAST.left(), variables);
            analyser(binaryOperatorExpressionAST.right(), variables);
        } else {
            throw new AnalyserException("invalid expression");
        }
    }

}
