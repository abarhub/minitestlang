package org.minitestlang.interpreter;

import org.minitestlang.ast.ClassAST;
import org.minitestlang.ast.MethodAST;
import org.minitestlang.ast.expr.NumberExpressionAST;
import org.minitestlang.ast.instr.AffectAST;
import org.minitestlang.ast.instr.InstructionAST;
import org.minitestlang.runner.AppRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Interpreter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Interpreter.class);

    public void run(ClassAST ast) throws InterpreterException {
        Optional<MethodAST> optMethod = ast.getMethod("main");
        if (optMethod.isEmpty()) {
            throw new InterpreterException("no main method in class " + ast.getName());
        }
        run(optMethod.get());
    }

    private void run(MethodAST methodAST) {
        if (methodAST.getInstructions() != null) {
            Map<String, Integer> map = new HashMap<>();
            for (InstructionAST instr : methodAST.getInstructions()) {
                if (instr instanceof AffectAST affect) {
                    LOGGER.debug("affect {} = {}", affect.getVariable(), affect.getExpression());
                    map.put(affect.getVariable(), ((NumberExpressionAST) affect.getExpression()).getNumber());
                }
            }
            LOGGER.info("methode {} : {}", methodAST.getName(), map);
        }
    }
}
