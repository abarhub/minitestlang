package org.minitestlang.interpreter;

import org.junit.jupiter.api.Test;
import org.minitestlang.ast.ClassAST;
import org.minitestlang.listener.minitestlang.Parser;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {

    @Test
    void run() throws Exception {
        String javaClassContent = """
                class SampleClass { 
                int main(){
                    a=5;
                    b=8;
                } 
                }""";
        Parser parser = new Parser();
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));
        assertNotNull(classAst);
        Interpreter interpreter = new Interpreter();
        Map<String, Value> map = new HashMap<>();
        interpreter.addMethodListener(map::putAll);
        interpreter.run(classAst);
        assertEquals(2, map.size());
        assertEquals(5, ((IntValue) map.get("a")).getNumber());
        assertEquals(8, ((IntValue) map.get("b")).getNumber());
    }

    @Test
    void run2() throws Exception {
        String javaClassContent = """
                class SampleClass { 
                int main(){
                    a=5+7;
                    b=8-3;
                } 
                }""";
        Parser parser = new Parser();
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));
        assertNotNull(classAst);
        Interpreter interpreter = new Interpreter();
        Map<String, Value> map = new HashMap<>();
        interpreter.addMethodListener(map::putAll);
        interpreter.run(classAst);
        assertEquals(2, map.size());
        assertEquals(12, ((IntValue) map.get("a")).getNumber());
        assertEquals(5, ((IntValue) map.get("b")).getNumber());
    }


    @Test
    void run3() throws Exception {
        String javaClassContent = """
                class SampleClass { 
                int main(){
                    a=8;
                    b=a-2;
                    c=a-b;
                    d=a*2+b*3+b/2;
                } 
                }""";
        Parser parser = new Parser();
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));
        assertNotNull(classAst);
        Interpreter interpreter = new Interpreter();
        Map<String, Value> map = new HashMap<>();
        interpreter.addMethodListener(map::putAll);
        interpreter.run(classAst);
        assertEquals(4, map.size());
        assertEquals(8, ((IntValue) map.get("a")).getNumber());
        assertEquals(6, ((IntValue) map.get("b")).getNumber());
        assertEquals(2, ((IntValue) map.get("c")).getNumber());
        assertEquals(37, ((IntValue) map.get("d")).getNumber());
    }
}