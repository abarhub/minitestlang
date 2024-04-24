package org.minitestlang.listener.minitestlang;

import org.junit.jupiter.api.Test;
import org.minitestlang.ast.ClassAST;
import org.minitestlang.ast.MethodAST;
import org.minitestlang.ast.expr.*;
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
        MethodAST method = classAst.getMethods().get(0);
        assertEquals("DoSomething", method.getName());
        assertEquals(0, method.getInstructions().size());
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
        MethodAST method = classAst.getMethods().get(0);
        assertEquals("DoSomething", method.getName());
        assertEquals(2, method.getInstructions().size());
        assertInstanceOf(AffectAST.class, method.getInstructions().get(0));
        AffectAST affect = (AffectAST) method.getInstructions().get(0);
        assertEquals("a", affect.getVariable());
        assertInstanceOf(NumberExpressionAST.class, affect.getExpression());
        assertEquals(5, ((NumberExpressionAST) affect.getExpression()).number());
        affect = (AffectAST) method.getInstructions().get(1);
        assertEquals("b", affect.getVariable());
        assertInstanceOf(NumberExpressionAST.class, affect.getExpression());
        assertEquals(8, ((NumberExpressionAST) affect.getExpression()).number());

    }

    @Test
    void parse3() throws IOException {
        String javaClassContent = """
                class SampleClass { 
                int DoSomething(){
                    a=4;
                    b=6;
                    c=a*2+b*3;
                } 
                }""";
        Parser parser = new Parser();
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));
        assertNotNull(classAst);
        assertEquals("SampleClass", classAst.getName());
        MethodAST method = classAst.getMethods().get(0);
        assertEquals("DoSomething", method.getName());
        assertEquals(3, method.getInstructions().size());
        assertInstanceOf(AffectAST.class, method.getInstructions().get(0));
        AffectAST affect = (AffectAST) method.getInstructions().get(0);
        assertEquals("a", affect.getVariable());
        assertInstanceOf(NumberExpressionAST.class, affect.getExpression());
        assertEquals(4, ((NumberExpressionAST) affect.getExpression()).number());
        affect = (AffectAST) method.getInstructions().get(1);
        assertEquals("b", affect.getVariable());
        assertInstanceOf(NumberExpressionAST.class, affect.getExpression());
        assertEquals(6, ((NumberExpressionAST) affect.getExpression()).number());
        affect = (AffectAST) method.getInstructions().get(2);
        assertEquals("c", affect.getVariable());
        ExpressionAST expr = affect.getExpression();
        assertInstanceOf(BinaryOperatorExpressionAST.class, expr);
        BinaryOperatorExpressionAST opbin = (BinaryOperatorExpressionAST) expr;
        assertEquals(Operator.ADD, opbin.operator());
        assertInstanceOf(BinaryOperatorExpressionAST.class, opbin.left());
        var left = (BinaryOperatorExpressionAST) opbin.left();
        assertEquals(Operator.MULT, left.operator());
        assertInstanceOf(IdentExpressionAST.class, left.left());
        assertEquals("a", ((IdentExpressionAST) left.left()).name());
        assertInstanceOf(NumberExpressionAST.class, left.right());
        assertEquals(2, ((NumberExpressionAST) left.right()).number());
        assertInstanceOf(BinaryOperatorExpressionAST.class, opbin.right());
        var right = (BinaryOperatorExpressionAST) opbin.right();
        assertEquals(Operator.MULT, right.operator());
        assertInstanceOf(IdentExpressionAST.class, right.left());
        assertEquals("b", ((IdentExpressionAST) right.left()).name());
        assertInstanceOf(NumberExpressionAST.class, right.right());
        assertEquals(3, ((NumberExpressionAST) right.right()).number());
    }
}