package org.minitestlang.interpreter;

import org.minitestlang.ast.ClassAST;
import org.minitestlang.ast.MethodAST;
import org.minitestlang.ast.expr.*;
import org.minitestlang.ast.instr.*;
import org.minitestlang.interpreter.value.*;
import org.minitestlang.runtime.BuiltinTable;
import org.minitestlang.runtime.IOUtils;
import org.minitestlang.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

public class Interpreter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Interpreter.class);

    private final List<Consumer<Map<String, Value>>> methodListener = new ArrayList<>();

    private final IOUtils ioUtils = new IOUtils();

    private BuiltinTable builtinTable = new BuiltinTable();

    public void run(ClassAST ast) throws InterpreterException {
        Optional<MethodAST> optMethod = ast.getMethod("main");
        if (optMethod.isEmpty()) {
            throw new InterpreterException("no main method in class " + ast.getName());
        }
        run(optMethod.get(), ast);
    }

    private void run(MethodAST methodAST, ClassAST ast) throws InterpreterException {
        if (methodAST.getInstructions() != null) {
            Map<String, Value> map = new HashMap<>();
            run(map, methodAST.getInstructions(), ast);
            LOGGER.info("methode {} : {}", methodAST.getName(), map);
            for (var listener : methodListener) {
                listener.accept(map);
            }
        }
    }

    private void run(Map<String, Value> map, List<InstructionAST> instructions, ClassAST ast) throws InterpreterException {
        for (InstructionAST instr : instructions) {
            if (instr instanceof AffectAST affect) {
                Value value = run(map, affect.getExpression());
                LOGGER.debug("affect {} = {}", affect.getVariable(), affect.getExpression());
                map.put(affect.getVariable(), value);
            } else if (instr instanceof DeclareAST declareAST) {
                if (declareAST.value().isPresent()) {
                    Value value = run(map, declareAST.value().get());
                    map.put(declareAST.name(), value);
                }
            } else if (instr instanceof IfAST ifAST) {
                Value value = run(map, ifAST.expr());
                if (value instanceof BoolValue boolValue) {
                    if (boolValue.value()) {
                        run(map, ifAST.block(), ast);
                    } else if (ifAST.elseBlock() != null) {
                        run(map, ifAST.elseBlock(), ast);
                    }
                } else {
                    throw new InterpreterException("invalide if expression");
                }
            } else if (instr instanceof WhileAST whileAST) {
                boolean fin = false;
                while (!fin) {
                    Value value = run(map, whileAST.expr());
                    if (value instanceof BoolValue boolValue) {
                        if (boolValue.value()) {
                            run(map, whileAST.block(), ast);
                        } else {
                            fin = true;
                        }
                    } else {
                        throw new InterpreterException("invalide if expression");
                    }
                }
            } else if (instr instanceof BlockAST blockAST) {
                if (blockAST.instr() != null) {
                    run(map, blockAST.instr(), ast);
                }
            } else if (instr instanceof AppelAST appelAST) {
                if (Objects.equals(appelAST.name(), "print")) {
                    ioUtils.print(appelAST.parameters(), map, this::run);
                } else if (appelAST.object().isPresent() && Objects.equals(appelAST.name(), "length")) {

                } else {
                    Optional<MethodAST> optMethod = ast.getMethod(appelAST.name());
                    if (optMethod.isEmpty()) {
                        throw new InterpreterException("invalide method : " + ast.getName());
                    } else {
                        run(optMethod.get(), ast);
                    }
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
            case StringAST stringAST -> {
                return new StringValue(stringAST.str());
            }
            case CharExpressionAST charAST -> {
                return new CharValue(charAST.value());
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
                    case MOD -> new IntValue(((IntValue) left).number() % ((IntValue) right).number());
                    case AND -> new BoolValue(((BoolValue) left).value() && ((BoolValue) right).value());
                    case OR -> new BoolValue(((BoolValue) left).value() || ((BoolValue) right).value());
                    case EQUAL -> (left instanceof BoolValue) ?
                            new BoolValue(((BoolValue) left).value() == ((BoolValue) right).value()) :
                            new BoolValue(((IntValue) left).number() == ((IntValue) right).number());
                    case NOTEQUAL -> (left instanceof BoolValue) ?
                            new BoolValue(((BoolValue) left).value() != ((BoolValue) right).value()) :
                            new BoolValue(((IntValue) left).number() != ((IntValue) right).number());
                    case GE -> new BoolValue(((IntValue) left).number() >= ((IntValue) right).number());
                    case LE -> new BoolValue(((IntValue) left).number() <= ((IntValue) right).number());
                    case GT -> new BoolValue(((IntValue) left).number() > ((IntValue) right).number());
                    case LT -> new BoolValue(((IntValue) left).number() < ((IntValue) right).number());
                };
            }
            case AppelExpressionAST appelAST -> {
                Value objet = null;
                if (appelAST.objet().isPresent()) {
                    objet = run(map, appelAST.objet().get());
                    if (objet == null) {
                        throw new InterpreterException("Invalide object expression");
                    }
                }
                List<Value> param = new ArrayList<>();
                if (CollectionUtils.size(appelAST.parameters()) > 0) {
                    for (var value : appelAST.parameters()) {
                        var v = run(map, value);
                        param.add(v);
                    }
                }
                if (objet != null) {
                    if (objet instanceof StringValue str) {
                        if (Objects.equals(appelAST.nom(), "length") && CollectionUtils.size(param) == 0) {
                            return new IntValue(str.string().length());
                        }
                    }
                }
                throw new InterpreterException("Invalide call expression");
            }
            case null, default -> throw new InterpreterException("Error for evaluate expression");
        }
    }

    public void addMethodListener(Consumer<Map<String, Value>> fun) {
        methodListener.add(fun);
    }

    public void addPrintListener(Consumer<String> fun) {
        ioUtils.addPrintLister(fun);
    }
}
