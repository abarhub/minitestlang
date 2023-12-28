package org.minitestlang.interpreter;

import org.minitestlang.ast.ClassAST;
import org.minitestlang.ast.MethodAST;
import org.minitestlang.ast.expr.BinaryOperatorExpressionAST;
import org.minitestlang.ast.expr.ExpressionAST;
import org.minitestlang.ast.expr.IdentExpressionAST;
import org.minitestlang.ast.expr.NumberExpressionAST;
import org.minitestlang.ast.instr.AffectAST;
import org.minitestlang.ast.instr.InstructionAST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

public class Interpreter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Interpreter.class);

    private List<Consumer<Map<String, Value>>> methodListener = new ArrayList<>();

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
        if (expression instanceof NumberExpressionAST num) {
            return new IntValue(num.getNumber());
        } else if (expression instanceof IdentExpressionAST ident) {
            if (map.containsKey(ident.getName())) {
                return map.get(ident.getName());
            } else {
                throw new InterpreterException("Variable " + ident.getName() + " not initialized");
            }
        } else if (expression instanceof BinaryOperatorExpressionAST bin) {
            var left = run(map, bin.getLeft());
            var right = run(map, bin.getRight());
            if (left == null) {
                throw new InterpreterException("Invalide left expression");
            } else if (right == null) {
                throw new InterpreterException("Invalide right expression");
            }
            return switch (bin.getOperator()) {
                case ADD -> new IntValue(((IntValue) left).getNumber() + ((IntValue) right).getNumber());
                case SUB -> new IntValue(((IntValue) left).getNumber() - ((IntValue) right).getNumber());
                case MULT -> new IntValue(((IntValue) left).getNumber() * ((IntValue) right).getNumber());
                case DIV -> new IntValue(((IntValue) left).getNumber() / ((IntValue) right).getNumber());
            };
        } else {
            throw new InterpreterException("Error for evaluate expression");
        }
    }

    public void addMethodListener(Consumer<Map<String, Value>> fun){
        methodListener.add(fun);
    }
}
