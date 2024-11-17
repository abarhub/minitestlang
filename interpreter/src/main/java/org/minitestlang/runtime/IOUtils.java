package org.minitestlang.runtime;

import org.minitestlang.ast.expr.ExpressionAST;
import org.minitestlang.interpreter.*;
import org.minitestlang.interpreter.value.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class IOUtils {

    private final List<Consumer<String>> printListener = new ArrayList<>();


    public void print(List<ExpressionAST> parameters, Map<String, Value> map, Evaluate evaluate) throws InterpreterException {
        StringBuilder s = new StringBuilder();
        if (parameters != null) {
            var first = true;
            for (var param : parameters) {
                var value = evaluate.eval(map, param);
                if (first) {
                    first = false;
                } else {
                    s.append(", ");
                }
                s.append(switch (value) {
                    case null -> "null";
                    case BoolValue b -> b.value() + "";
                    case IntValue i -> i.number() + "";
                    case StringValue s2 -> s2.string();
                    case CharValue c -> c.value();
                    default -> throw new IllegalStateException("Unexpected value: " + value);
                });

            }
        }
        System.out.println(s);
        for (var listener : printListener) {
            listener.accept(s.toString());
        }
    }

    public void addPrintLister(Consumer<String> fun) {
        printListener.add(fun);
    }
}
