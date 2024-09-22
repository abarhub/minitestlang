package org.minitestlang.runtime;

import org.minitestlang.ast.expr.ExpressionAST;
import org.minitestlang.interpreter.InterpreterException;
import org.minitestlang.interpreter.Value;

import java.util.Map;

@FunctionalInterface
public interface Evaluate {

    Value eval(Map<String, Value> map, ExpressionAST expression) throws InterpreterException;
}
