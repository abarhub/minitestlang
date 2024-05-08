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
        assertEquals(5, ((IntValue) map.get("a")).number());
        assertEquals(8, ((IntValue) map.get("b")).number());
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
        assertEquals(12, ((IntValue) map.get("a")).number());
        assertEquals(5, ((IntValue) map.get("b")).number());
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
        assertEquals(8, ((IntValue) map.get("a")).number());
        assertEquals(6, ((IntValue) map.get("b")).number());
        assertEquals(2, ((IntValue) map.get("c")).number());
        assertEquals(37, ((IntValue) map.get("d")).number());
    }

    @Test
    void runIf() throws Exception {
        String javaClassContent = """
                class SampleClass {
                int main(){
                    a=1;
                    b=2;
                    c=3;
                    if(a<b) {
                        c=4;
                    }
                }
                }""";
        Parser parser = new Parser();
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));
        assertNotNull(classAst);
        Interpreter interpreter = new Interpreter();
        Map<String, Value> map = new HashMap<>();
        interpreter.addMethodListener(map::putAll);
        interpreter.run(classAst);
        assertEquals(3, map.size());
        assertEquals(1, ((IntValue) map.get("a")).number());
        assertEquals(2, ((IntValue) map.get("b")).number());
        assertEquals(4, ((IntValue) map.get("c")).number());
    }


    @Test
    void runIf2() throws Exception {
        String javaClassContent = """
                class SampleClass {
                int main(){
                    a=1;
                    b=2;
                    c=3;
                    if(a>b) {
                        c=4;
                    }
                }
                }""";
        Parser parser = new Parser();
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));
        assertNotNull(classAst);
        Interpreter interpreter = new Interpreter();
        Map<String, Value> map = new HashMap<>();
        interpreter.addMethodListener(map::putAll);
        interpreter.run(classAst);
        assertEquals(3, map.size());
        assertEquals(1, ((IntValue) map.get("a")).number());
        assertEquals(2, ((IntValue) map.get("b")).number());
        assertEquals(3, ((IntValue) map.get("c")).number());
    }

    @Test
    void runIfElse() throws Exception {
        String javaClassContent = """
                class SampleClass {
                int main(){
                    a=1;
                    b=2;
                    c=8;
                    if(a<b) {
                        c=6;
                    } else {
                        c=7;
                    }
                }
                }""";
        Parser parser = new Parser();
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));
        assertNotNull(classAst);
        Interpreter interpreter = new Interpreter();
        Map<String, Value> map = new HashMap<>();
        interpreter.addMethodListener(map::putAll);
        interpreter.run(classAst);
        assertEquals(3, map.size());
        assertEquals(1, ((IntValue) map.get("a")).number());
        assertEquals(2, ((IntValue) map.get("b")).number());
        assertEquals(6, ((IntValue) map.get("c")).number());
    }

    @Test
    void runIfElse2() throws Exception {
        String javaClassContent = """
                class SampleClass {
                int main(){
                    a=1;
                    b=2;
                    c=8;
                    if(a>b) {
                        c=6;
                    } else {
                        c=7;
                    }
                }
                }""";
        Parser parser = new Parser();
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));
        assertNotNull(classAst);
        Interpreter interpreter = new Interpreter();
        Map<String, Value> map = new HashMap<>();
        interpreter.addMethodListener(map::putAll);
        interpreter.run(classAst);
        assertEquals(3, map.size());
        assertEquals(1, ((IntValue) map.get("a")).number());
        assertEquals(2, ((IntValue) map.get("b")).number());
        assertEquals(7, ((IntValue) map.get("c")).number());
    }


    @Test
    void runWhile() throws Exception {
        String javaClassContent = """
                class SampleClass {
                int main(){
                    a=1;
                    while(a<3){
                        a=a+1;
                    }
                }
                }""";
        Parser parser = new Parser();
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));
        assertNotNull(classAst);
        Interpreter interpreter = new Interpreter();
        Map<String, Value> map = new HashMap<>();
        interpreter.addMethodListener(map::putAll);
        interpreter.run(classAst);
        assertEquals(1, map.size());
        assertEquals(3, ((IntValue) map.get("a")).number());
    }

    @Test
    void runWhile2() throws Exception {
        String javaClassContent = """
                class SampleClass {
                int main(){
                    a=1;
                    while(a<2){
                        a=a+1;
                    }
                }
                }""";
        Parser parser = new Parser();
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));
        assertNotNull(classAst);
        Interpreter interpreter = new Interpreter();
        Map<String, Value> map = new HashMap<>();
        interpreter.addMethodListener(map::putAll);
        interpreter.run(classAst);
        assertEquals(1, map.size());
        assertEquals(2, ((IntValue) map.get("a")).number());
    }

    @Test
    void runWhile3() throws Exception {
        String javaClassContent = """
                class SampleClass {
                int main(){
                    a=1;
                    while(a<0){
                        a=a+1;
                    }
                }
                }""";
        Parser parser = new Parser();
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));
        assertNotNull(classAst);
        Interpreter interpreter = new Interpreter();
        Map<String, Value> map = new HashMap<>();
        interpreter.addMethodListener(map::putAll);
        interpreter.run(classAst);
        assertEquals(1, map.size());
        assertEquals(1, ((IntValue) map.get("a")).number());
    }

    @Test
    void runWhile4() throws Exception {
        String javaClassContent = """
                class SampleClass {
                int main(){
                    a=1;
                    while(a<10){
                        a=a+1;
                    }
                }
                }""";
        Parser parser = new Parser();
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));
        assertNotNull(classAst);
        Interpreter interpreter = new Interpreter();
        Map<String, Value> map = new HashMap<>();
        interpreter.addMethodListener(map::putAll);
        interpreter.run(classAst);
        assertEquals(1, map.size());
        assertEquals(10, ((IntValue) map.get("a")).number());
    }

    @Test
    void runAppel() throws Exception {
        String javaClassContent = """
                class SampleClass {
                int main(){
                    a=1;
                    print(a);
                }
                }""";
        Parser parser = new Parser();
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));
        assertNotNull(classAst);
        Interpreter interpreter = new Interpreter();
        Map<String, Value> map = new HashMap<>();
        interpreter.addMethodListener(map::putAll);
        interpreter.run(classAst);
        assertEquals(1, map.size());
        assertEquals(1, ((IntValue) map.get("a")).number());
    }

    @Test
    void runAppel2() throws Exception {
        String javaClassContent = """
                class SampleClass {
                int main(){
                    a=15;
                    b=true;
                    c=45+10;
                    print(a,b,c);
                }
                }""";
        Parser parser = new Parser();
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));
        assertNotNull(classAst);
        Interpreter interpreter = new Interpreter();
        Map<String, Value> map = new HashMap<>();
        interpreter.addMethodListener(map::putAll);
        interpreter.run(classAst);
        assertEquals(3, map.size());
        assertEquals(15, ((IntValue) map.get("a")).number());
        assertTrue(((BoolValue) map.get("b")).value());
        assertEquals(55, ((IntValue) map.get("c")).number());
    }
}