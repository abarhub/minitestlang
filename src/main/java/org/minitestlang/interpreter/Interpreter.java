package org.minitestlang.interpreter;

import org.minitestlang.ast.ClassAST;
import org.minitestlang.ast.MethodAST;
import org.minitestlang.ast.expr.*;
import org.minitestlang.ast.instr.AffectAST;
import org.minitestlang.ast.instr.DeclareAST;
import org.minitestlang.ast.instr.InstructionAST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

public class Interpreter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Interpreter.class);

    private final List<Consumer<Map<String, Value>>> methodListener = new ArrayList<>();

    public void run(ClassAST ast) throws InterpreterException {
        Optional<MethodAST> optMethod = ast.getMethod("main");
        if (optMethod.isEmpty()) {
            throw new InterpreterException("no main method in class " + ast.getName());
        }
        run(optMethod.get());
    }

    private void run(MethodAST methodAST) throws InterpreterException {
        if (methodAST.getInstructions() != null) {
            Map<String, Value> map = new HashMap<>();
            for (InstructionAST instr : methodAST.getInstructions()) {
                if (instr instanceof AffectAST affect) {
                    Value value = run(map, affect.getExpression());
                    LOGGER.debug("affect {} = {}", affect.getVariable(), affect.getExpression());
                    map.put(affect.getVariable(), value);
                } else if (instr instanceof DeclareAST declareAST) {
                    if(declareAST.value().isPresent()){
                        Value value = run(map, declareAST.value().get());
                        map.put(declareAST.name(), value);
                    }
                }
            }
            LOGGER.info("methode {} : {}", methodAST.getName(), map);
            if(methodListener!=null){
                for(var listener:methodListener){
                    listener.accept(map);
                }
            }
        }
    }

    private Value run(Map<String, Value> map, ExpressionAST expression) throws InterpreterException {
        switch (expression) {
            case NumberExpressionAST num -> {
                return new IntValue(num.number());
            }
            case BooleanExpressionAST booleanExpressionAST -> {
                return new BoolValue(booleanExpressionAST.value());
            }
            case IdentExpressionAST ident -> {
                if (map.containsKey(ident.name())) {
                    return map.get(ident.name());
                } else {
                    throw new InterpreterException("Variable " + ident.name() + " not initialized");
                }
            }
            case BinaryOperatorExpressionAST bin -> {
                var left = run(map, bin.left());
                var right = run(map, bin.right());
                if (left == null) {
                    throw new InterpreterException("Invalide left expression");
                } else if (right == null) {
                    throw new InterpreterException("Invalide right expression");
                }
                return switch (bin.operator()) {
                    case ADD -> new IntValue(((IntValue) left).number() + ((IntValue) right).number());
                    case SUB -> new IntValue(((IntValue) left).number() - ((IntValue) right).number());
                    case MULT -> new IntValue(((IntValue) left).number() * ((IntValue) right).number());
                    case DIV -> new IntValue(((IntValue) left).number() / ((IntValue) right).number());
                };
            }
            case null, default -> throw new InterpreterException("Error for evaluate expression");
        }
    }

    public void addMethodListener(Consumer<Map<String, Value>> fun){
        methodListener.add(fun);
    }
}
