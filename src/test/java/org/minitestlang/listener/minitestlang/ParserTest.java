package org.minitestlang.listener.minitestlang;

import org.junit.jupiter.api.Test;
import org.minitestlang.ast.ClassAST;
import org.minitestlang.ast.MethodAST;
import org.minitestlang.ast.expr.NumberExpressionAST;
import org.minitestlang.ast.instr.AffectAST;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void parse() throws IOException {
        String javaClassContent = "class SampleClass { int DoSomething(){} }";
        Parser parser = new Parser();
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));
        assertNotNull(classAst);
        assertEquals("SampleClass", classAst.getName());
        MethodAST method=classAst.getMethods().get(0);
        assertEquals("DoSomething",method.getName());
        assertEquals(0,method.getInstructions().size());
    }

    @Test
    void parse2() throws IOException {
        String javaClassContent = """
                class SampleClass { 
                int DoSomething(){
                    a=5;
                    b=8;
                } 
                }""";
        Parser parser = new Parser();
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));
        assertNotNull(classAst);
        assertEquals("SampleClass", classAst.getName());
        MethodAST method=classAst.getMethods().get(0);
        assertEquals("DoSomething",method.getName());
        assertEquals(2,method.getInstructions().size());
        assertInstanceOf(AffectAST.class, method.getInstructions().get(0));
        AffectAST affect= (AffectAST) method.getInstructions().get(0);
        assertEquals("a",affect.getVariable());
        assertInstanceOf(NumberExpressionAST.class, affect.getExpression());

    }
}