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
        assertEquals(5, ((NumberExpressionAST) affect.getExpression()).getNumber());
        affect = (AffectAST) method.getInstructions().get(1);
        assertEquals("b", affect.getVariable());
        assertInstanceOf(NumberExpressionAST.class, affect.getExpression());
        assertEquals(8, ((NumberExpressionAST) affect.getExpression()).getNumber());

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
        assertEquals(4, ((NumberExpressionAST) affect.getExpression()).getNumber());
        affect = (AffectAST) method.getInstructions().get(1);
        assertEquals("b", affect.getVariable());
        assertInstanceOf(NumberExpressionAST.class, affect.getExpression());
        assertEquals(6, ((NumberExpressionAST) affect.getExpression()).getNumber());
        affect = (AffectAST) method.getInstructions().get(2);
        assertEquals("c", affect.getVariable());
        ExpressionAST expr = affect.getExpression();
        assertInstanceOf(BinaryOperatorExpressionAST.class, expr);
        BinaryOperatorExpressionAST opbin = (BinaryOperatorExpressionAST) expr;
        assertEquals(Operator.ADD, opbin.getOperator());
        assertInstanceOf(BinaryOperatorExpressionAST.class, opbin.getLeft());
        var left = (BinaryOperatorExpressionAST) opbin.getLeft();
        assertEquals(Operator.MULT, left.getOperator());
        assertInstanceOf(IdentExpressionAST.class, left.getLeft());
        assertEquals("a", ((IdentExpressionAST) left.getLeft()).getName());
        assertInstanceOf(NumberExpressionAST.class, left.getRight());
        assertEquals(2, ((NumberExpressionAST) left.getRight()).getNumber());
        assertInstanceOf(BinaryOperatorExpressionAST.class, opbin.getRight());
        var right = (BinaryOperatorExpressionAST) opbin.getRight();
        assertEquals(Operator.MULT, right.getOperator());
        assertInstanceOf(IdentExpressionAST.class, right.getLeft());
        assertEquals("b", ((IdentExpressionAST) right.getLeft()).getName());
        assertInstanceOf(NumberExpressionAST.class, right.getRight());
        assertEquals(3, ((NumberExpressionAST) right.getRight()).getNumber());
    }
}