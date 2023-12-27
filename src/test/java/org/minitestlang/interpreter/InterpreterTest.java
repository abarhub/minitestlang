package org.minitestlang.interpreter;

import org.junit.jupiter.api.Test;
import org.minitestlang.ast.ClassAST;
import org.minitestlang.listener.minitestlang.Parser;

import java.io.StringReader;

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
        Interpreter interpreter=new Interpreter();
        interpreter.run(classAst);
    }
}