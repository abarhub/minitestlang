package org.minitestlang.analyser;

import org.minitestlang.ast.ClassAST;
import org.minitestlang.ast.MethodAST;
import org.minitestlang.ast.expr.*;
import org.minitestlang.ast.instr.*;
import org.minitestlang.ast.type.*;
import org.minitestlang.utils.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Analyser {

    public void analyser(ClassAST ast) throws AnalyserException {
        Optional<MethodAST> optMethod = ast.getMethod("main");
        if (optMethod.isEmpty()) {
            throw new AnalyserException("no main method in class " + ast.getName() + " in position " + ast.getName());
        }
        analyser(optMethod.get(), ast);
        for (MethodAST method : ast.getMethods()) {
            if (!Objects.equals(method.getName(), "main")) {
                analyser(method, ast);
            }
        }
    }

    private void analyser(MethodAST methodAST, ClassAST ast) throws AnalyserException {
        if (methodAST.getInstructions() != null) {
            SymbolTable variables = new SymbolTable();
            analyser(methodAST.getInstructions(), variables, ast);
        }
    }

    private void analyser(List<InstructionAST> instructions,
                          SymbolTable variables, ClassAST ast) throws AnalyserException {
        for (InstructionAST instr : instructions) {
            switch (instr) {
                case AffectAST affect -> {
                    analyser(affect.getExpression(), variables);
                    if (!variables.has(affect.getVariable())) {
                        throw new AnalyserException("variable " + affect.getVariable() +
                                " is not declared in position " + affect.getPositionVariable());
                    }
                    variables.get(affect.getVariable()).setAffected(true);
                }
                case DeclareAST declareAST -> {
                    if (variables.has(declareAST.name())) {
                        throw new AnalyserException("variable " + declareAST.name() +
                                " is not already declared in position " + declareAST.positionAST());
                    }
                    variables.put(declareAST.name(), new VarAnalyser(declareAST.type(),
                            declareAST.value().isPresent()));
                }
                case BlockAST blockAST -> {
                    if (blockAST.instr() != null) {
                        SymbolTable variables2 = new SymbolTable(variables);
                        analyser(blockAST.instr(), variables2, ast);
                    }
                }
                case IfAST ifAST -> {
                    TypeAST expr = analyser(ifAST.expr(), variables);
                    if (expr == null) {
                        throw new AnalyserException("expression invalide");
                    } else if (!(expr instanceof BooleanTypeAST)) {
                        throw new AnalyserException("expression invalide");
                    }
                    if (ifAST.block() != null) {
                        SymbolTable variables2 = new SymbolTable(variables);
                        analyser(ifAST.block(), variables2, ast);
                    }
                    if (ifAST.elseBlock() != null) {
                        SymbolTable variables2 = new SymbolTable(variables);
                        analyser(ifAST.elseBlock(), variables2, ast);
                    }
                }
                case WhileAST whileAST -> {
                    TypeAST expr = analyser(whileAST.expr(), variables);
                    if (expr == null) {
                        throw new AnalyserException("expression invalide");
                    } else if (!(expr instanceof BooleanTypeAST)) {
                        throw new AnalyserException("expression invalide");
                    }
                    if (whileAST.block() != null) {
                        SymbolTable variables2 = new SymbolTable(variables);
                        analyser(whileAST.block(), variables2, ast);
                    }
                }
                case AppelAST appelAST -> {
                    Optional<TypeAST> object = Optional.empty();
                    if (appelAST.object().isPresent()) {
                        var type = analyser(appelAST.object().get(), variables);
                        if (type == null) {
                            throw new AnalyserException("appel object not found");
                        } else if (type instanceof StringTypeAST) {
                            object = Optional.of(type);
                        } else {
                            throw new AnalyserException("type invalide");
                        }
                    }
                    if (CollectionUtils.size(appelAST.parameters()) > 0) {
                        for (var expr : appelAST.parameters()) {
                            analyser(expr, variables);
                        }
                    }
                    if (Objects.equals(appelAST.name(), "print")) {
                        // la méthode existe;
                    } else if (object.isPresent() && object.get() instanceof StringTypeAST && Objects.equals(appelAST.name(), "length")) {
                        // la méthode existe;
                    } else {
                        var optMethod = ast.getMethod(appelAST.name());
                        if (optMethod.isEmpty()) {
                            throw new AnalyserException("method not found");
                        }
                    }
                }
                case null, default -> throw new AnalyserException("invalid instruction");
            }
        }
    }


    private TypeAST analyser(ExpressionAST expression, SymbolTable variables) throws AnalyserException {
        switch (expression) {
            case IdentExpressionAST identExpressionAST -> {
                if (!variables.has(identExpressionAST.name())) {
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
                        if (typeLeft instanceof BooleanTypeAST && typeRight instanceof BooleanTypeAST) {
                            // no error
                        } else if (typeLeft instanceof IntTypeAST && typeRight instanceof IntTypeAST) {
                            // no error
                        } else {
                            throw new AnalyserException("left and right types do not match in position " + binaryOperatorExpressionAST.position());
                        }
                        return typeLeft;
                    }
                }
            }
            case null -> throw new AnalyserException("invalid expression ");
            case StringAST stringAST -> {
                return new StringTypeAST(stringAST.position());
            }
            case CharExpressionAST charExpressionAST -> {
                return new CharTypeAST(charExpressionAST.position());
            }
            default -> throw new AnalyserException("invalid expression in position " + expression.position());
        }
    }

}
