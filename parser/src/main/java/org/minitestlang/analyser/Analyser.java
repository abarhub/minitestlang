package org.minitestlang.analyser;

import org.minitestlang.ast.ClassAST;
import org.minitestlang.ast.MethodAST;
import org.minitestlang.ast.expr.*;
import org.minitestlang.ast.instr.*;
import org.minitestlang.ast.type.BooleanTypeAST;
import org.minitestlang.ast.type.IntTypeAST;
import org.minitestlang.ast.type.TypeAST;

import java.util.HashMap;
import java.util.List;
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
            analyser(methodAST.getInstructions(), variables);
        }
    }

    private void analyser(List<InstructionAST> instructions,
                          Map<String, VarAnalyser> variables) throws AnalyserException {
        for (InstructionAST instr : instructions) {
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
            } else if (instr instanceof BlockAST blockAST) {
                if (blockAST.instr() != null) {
                    Map<String, VarAnalyser> variables2 = new HashMap<>();
                    variables2.putAll(variables);
                    analyser(blockAST.instr(), variables2);
                }
            } else if (instr instanceof IfAST ifAST) {
                TypeAST expr = analyser(ifAST.expr(), variables);
                if (expr == null) {
                    throw new AnalyserException("expression invalide");
                } else if (!(expr instanceof BooleanTypeAST)) {
                    throw new AnalyserException("expression invalide");
                }
                if (ifAST.block() != null) {
                    Map<String, VarAnalyser> variables2 = new HashMap<>();
                    variables2.putAll(variables);
                    analyser(ifAST.block(), variables2);
                }
                if (ifAST.elseBlock() != null) {
                    Map<String, VarAnalyser> variables2 = new HashMap<>();
                    variables2.putAll(variables);
                    analyser(ifAST.elseBlock(), variables2);
                }
            } else if (instr instanceof WhileAST whileAST) {
                TypeAST expr = analyser(whileAST.expr(), variables);
                if (expr == null) {
                    throw new AnalyserException("expression invalide");
                } else if (!(expr instanceof BooleanTypeAST)) {
                    throw new AnalyserException("expression invalide");
                }
                if (whileAST.block() != null) {
                    Map<String, VarAnalyser> variables2 = new HashMap<>();
                    variables2.putAll(variables);
                    analyser(whileAST.block(), variables2);
                }
            } else {
                throw new AnalyserException("invalid instruction");
            }
        }
    }


    private TypeAST analyser(ExpressionAST expression, Map<String, VarAnalyser> variables) throws AnalyserException {
        switch (expression) {
            case IdentExpressionAST identExpressionAST -> {
                if (!variables.containsKey(identExpressionAST.name())) {
                    throw new AnalyserException("variable " + identExpressionAST.name() +
                            " is not declared in position " + identExpressionAST.position());
                } else if (!variables.get(identExpressionAST.name()).isAffected()) {
                    throw new AnalyserException("variable " + identExpressionAST.name() +
                            " is not affected in position " + identExpressionAST.position());
                }
                return variables.get(identExpressionAST.name()).getType();
            }
            case BooleanExpressionAST booleanExpressionAST -> {
                return new BooleanTypeAST(booleanExpressionAST.position());
            }
            case NumberExpressionAST numberExpressionAST -> {
                return new IntTypeAST(numberExpressionAST.position());
            }
            case BinaryOperatorExpressionAST binaryOperatorExpressionAST -> {
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
            }
            case null, default ->
                    throw new AnalyserException("invalid expression in position " + expression.position());
        }
    }

}
