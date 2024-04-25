package org.minitestlang.analyser;

import org.minitestlang.ast.ClassAST;
import org.minitestlang.ast.MethodAST;
import org.minitestlang.ast.expr.*;
import org.minitestlang.ast.instr.AffectAST;
import org.minitestlang.ast.instr.DeclareAST;
import org.minitestlang.ast.instr.InstructionAST;
import org.minitestlang.ast.type.BooleanTypeAST;
import org.minitestlang.ast.type.IntTypeAST;
import org.minitestlang.ast.type.TypeAST;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    private TypeAST analyser(ExpressionAST expression, Map<String, VarAnalyser> variables) throws AnalyserException {
        if (expression instanceof IdentExpressionAST identExpressionAST) {
            if (!variables.containsKey(identExpressionAST.name())) {
                throw new AnalyserException("variable " + identExpressionAST.name() +
                        " is not declared in position " + identExpressionAST.position());
            } else if (!variables.get(identExpressionAST.name()).isAffected()) {
                throw new AnalyserException("variable " + identExpressionAST.name() +
                        " is not affected in position " + identExpressionAST.position());
            }
            return variables.get(identExpressionAST.name()).getType();
        } else if ((expression instanceof BooleanExpressionAST booleanExpressionAST)) {
            return new BooleanTypeAST(booleanExpressionAST.position());
        } else if (expression instanceof NumberExpressionAST numberExpressionAST) {
            return new IntTypeAST(numberExpressionAST.position());
        } else if (expression instanceof BinaryOperatorExpressionAST binaryOperatorExpressionAST) {
            var typeLeft = analyser(binaryOperatorExpressionAST.left(), variables);
            var typeRight = analyser(binaryOperatorExpressionAST.right(), variables);
            if (typeLeft instanceof BooleanTypeAST && typeRight instanceof BooleanTypeAST) {
                // no error
            } else if (typeLeft instanceof IntTypeAST && typeRight instanceof IntTypeAST) {
                // no error
            } else {
                throw new AnalyserException("left and right types do not match in position " + binaryOperatorExpressionAST.position());
            }
            switch (binaryOperatorExpressionAST.operator()) {
                case AND, OR, EQUAL, NOTEQUAL, GT, LT -> {
                    if (!(typeLeft instanceof BooleanTypeAST)) {
                        throw new AnalyserException("left type is not boolean");
                    }
                    if (!(typeRight instanceof BooleanTypeAST)) {
                        throw new AnalyserException("right type is not boolean");
                    }
                    return new BooleanTypeAST(binaryOperatorExpressionAST.position());
                }
                default -> {
                    return typeLeft;
                }
            }
        } else {
            throw new AnalyserException("invalid expression in position " + expression.position());
        }
    }

}
