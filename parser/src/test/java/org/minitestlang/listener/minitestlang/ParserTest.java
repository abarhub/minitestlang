package org.minitestlang.listener.minitestlang;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.minitestlang.ast.ClassAST;
import org.minitestlang.ast.MethodAST;
import org.minitestlang.ast.expr.*;
import org.minitestlang.ast.instr.*;
import org.minitestlang.utils.CollectionUtils;

import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void parse() throws IOException {
        // ARRANGE
        String javaClassContent = "class SampleClass { int DoSomething(){} }";
        Parser parser = new Parser();
        // ACT
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));

        // ASSERT
        assertNotNull(classAst);
        assertEquals("SampleClass", classAst.getName());
        MethodAST method = classAst.getMethods().getFirst();
        assertEquals("DoSomething", method.getName());
        assertEquals(0, CollectionUtils.size(method.getInstructions()));
    }

    @Test
    void parse2() throws IOException {
        // ARRANGE
        String javaClassContent = """
                class SampleClass {
                int DoSomething(){
                    a=5;
                    b=8;
                }
                }""";
        Parser parser = new Parser();

        // ACT
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));

        // ASSERT
        assertNotNull(classAst);
        assertEquals("SampleClass", classAst.getName());
        MethodAST method = classAst.getMethods().getFirst();
        assertEquals("DoSomething", method.getName());
        assertEquals(2, method.getInstructions().size());
        assertInstanceOf(AffectAST.class, method.getInstructions().getFirst());
        AffectAST affect = (AffectAST) method.getInstructions().getFirst();
        assertEquals("a", affect.getVariable());
        assertInstanceOf(NumberExpressionAST.class, affect.getExpression());
        assertEquals(5, ((NumberExpressionAST) affect.getExpression()).number());
        affect = (AffectAST) method.getInstructions().get(1);
        assertEquals("b", affect.getVariable());
        assertInstanceOf(NumberExpressionAST.class, affect.getExpression());
        assertEquals(8, ((NumberExpressionAST) affect.getExpression()).number());

        assertThat(classAst)
                .extracting("name", "positionClass.line", "positionClass.column",
                        "positionName.line", "positionName.column")
                .contains("SampleClass", 1, 1, 1, 7);
    }

    @Test
    void parse3() throws IOException {
        // ARRANGE
        String javaClassContent = """
                class SampleClass {
                int DoSomething(){
                    a=4;
                    b=6;
                    c=a*2+b*3;
                }
                }""";
        Parser parser = new Parser();

        // ACT
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));

        // ASSERT
        assertNotNull(classAst);
        assertEquals("SampleClass", classAst.getName());
        MethodAST method = classAst.getMethods().getFirst();
        assertEquals("DoSomething", method.getName());
        assertEquals(3, method.getInstructions().size());
        assertInstanceOf(AffectAST.class, method.getInstructions().getFirst());
        AffectAST affect = (AffectAST) method.getInstructions().getFirst();
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

    @Test
    void parse4() throws IOException {
        // ARRANGE
        String javaClassContent = """
                class SampleClass {
                int DoSomething(){
                    a=4;
                    b=6;
                    if(a<5){
                       a=7;
                    } else {
                       a=8;
                    }
                    while(b<10){
                        b=15;
                    }
                }
                }""";
        Parser parser = new Parser();

        // ACT
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));

        // ASSERT
        assertNotNull(classAst);
        assertEquals("SampleClass", classAst.getName());
        MethodAST method = classAst.getMethods().getFirst();
        assertEquals("DoSomething", method.getName());
        assertEquals(4, method.getInstructions().size());
        assertInstanceOf(AffectAST.class, method.getInstructions().getFirst());
        AffectAST affect = (AffectAST) method.getInstructions().getFirst();
        assertEquals("a", affect.getVariable());
        assertInstanceOf(NumberExpressionAST.class, affect.getExpression());
        assertEquals(4, ((NumberExpressionAST) affect.getExpression()).number());
        affect = (AffectAST) method.getInstructions().get(1);
        assertEquals("b", affect.getVariable());
        assertInstanceOf(NumberExpressionAST.class, affect.getExpression());
        assertEquals(6, ((NumberExpressionAST) affect.getExpression()).number());
        IfAST ifAST = (IfAST) method.getInstructions().get(2);
        assertInstanceOf(BinaryOperatorExpressionAST.class, ifAST.expr());
        BinaryOperatorExpressionAST opbin = (BinaryOperatorExpressionAST) ifAST.expr();
        assertEquals(Operator.LT, opbin.operator());
        assertInstanceOf(IdentExpressionAST.class, opbin.left());
        assertEquals("a", ((IdentExpressionAST) opbin.left()).name());
        assertInstanceOf(NumberExpressionAST.class, opbin.right());
        assertEquals(5, ((NumberExpressionAST) opbin.right()).number());

        assertThat(classAst)
                .extracting("name", "positionClass.line", "positionClass.column",
                        "positionName.line", "positionName.column")
                .contains("SampleClass", 1, 1, 1, 7);

        assertEquals(4, method.getInstructions().size());

        assertThat(method.getInstructions())
                .element(0)
                .isInstanceOf(AffectAST.class)
                .extracting("variable", "expression.number")
                .contains("a", 4);

        assertThat(method.getInstructions())
                .element(1)
                .isInstanceOf(AffectAST.class)
                .extracting("variable", "expression.number")
                .contains("b", 6);

        assertThat(method.getInstructions())
                .element(2)
                .isInstanceOf(IfAST.class)
                .extracting("expr.operator", "expr.left.name", "expr.right.number"
                )
                .contains(Operator.LT, "a", 5);
        assertEquals(1, ((IfAST) method.getInstructions().get(2)).block().size());
        assertThat(((BlockAST) ((IfAST) method.getInstructions().get(2)).block().getFirst()).instr())
                .extracting(
                        "variable", "expression.number")
                .contains(tuple("a", 7));
        assertThat(method.getInstructions())
                .element(2)
                .extracting("block")
                .asInstanceOf(InstanceOfAssertFactories.LIST)
                .element(0)
                .extracting("instr")
                .asInstanceOf(InstanceOfAssertFactories.LIST)
                .extracting(
                        "variable", "expression.number")
                .contains(tuple("a", 7));
        assertEquals(1, ((IfAST) method.getInstructions().get(2)).block().size());
        assertThat(((BlockAST) ((IfAST) method.getInstructions().get(2)).elseBlock().getFirst()).instr())
                .extracting(
                        "variable", "expression.number")
                .contains(tuple("a", 8));

        assertThat(method.getInstructions())
                .element(3)
                .isInstanceOf(WhileAST.class)
                .extracting("expr.operator", "expr.left.name", "expr.right.number")
                .contains(Operator.LT, "b", 10);
        assertEquals(1, ((WhileAST) method.getInstructions().get(3)).block().size());
        assertThat(((BlockAST) ((WhileAST) method.getInstructions().get(3)).block().getFirst()).instr())
                .extracting(
                        "variable", "expression.number")
                .contains(tuple("b", 15));
    }

    @Test
    void parse5() throws IOException {
        // ARRANGE
        String javaClassContent = """
                class SampleClass {
                int DoSomething(){
                    a=10;
                    b=30;
                    print(a,b);
                }
                }""";
        Parser parser = new Parser();

        // ACT
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));

        // ASSERT
        assertNotNull(classAst);
        assertEquals("SampleClass", classAst.getName());
        MethodAST method = classAst.getMethods().getFirst();
        assertEquals("DoSomething", method.getName());
        assertEquals(3, method.getInstructions().size());
        assertInstanceOf(AffectAST.class, method.getInstructions().getFirst());
        AffectAST affect = (AffectAST) method.getInstructions().getFirst();
        assertEquals("a", affect.getVariable());
        assertInstanceOf(NumberExpressionAST.class, affect.getExpression());
        assertEquals(10, ((NumberExpressionAST) affect.getExpression()).number());
        affect = (AffectAST) method.getInstructions().get(1);
        assertEquals("b", affect.getVariable());
        assertInstanceOf(NumberExpressionAST.class, affect.getExpression());
        assertEquals(30, ((NumberExpressionAST) affect.getExpression()).number());
        AppelAST appelAST = (AppelAST) method.getInstructions().get(2);
        assertEquals("print", appelAST.name());
        assertEquals(2, appelAST.parameters().size());
        assertInstanceOf(IdentExpressionAST.class, appelAST.parameters().get(0));
        assertEquals("a", ((IdentExpressionAST) appelAST.parameters().get(0)).name());
        assertInstanceOf(IdentExpressionAST.class, appelAST.parameters().get(1));
        assertEquals("b", ((IdentExpressionAST) appelAST.parameters().get(1)).name());
    }

    @Test
    void parse6() throws IOException {
        // ARRANGE
        String javaClassContent = """
                class SampleClass {
                int DoSomething(){
                    a="abc";
                }
                }""";
        Parser parser = new Parser();

        // ACT
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));

        // ASSERT
        assertNotNull(classAst);
        assertEquals("SampleClass", classAst.getName());
        MethodAST method = classAst.getMethods().getFirst();
        assertEquals("DoSomething", method.getName());
        assertEquals(1, method.getInstructions().size());
        assertInstanceOf(AffectAST.class, method.getInstructions().getFirst());
        AffectAST affect = (AffectAST) method.getInstructions().getFirst();
        assertEquals("a", affect.getVariable());
        assertInstanceOf(StringAST.class, affect.getExpression());
        assertEquals("abc", ((StringAST) affect.getExpression()).str());
    }

    @Test
    void parse7() throws IOException {
        // ARRANGE
        String javaClassContent = """
                class SampleClass {
                int DoSomething(){
                    a='a';
                }
                }""";
        Parser parser = new Parser();

        // ACT
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));

        // ASSERT
        assertNotNull(classAst);
        assertEquals("SampleClass", classAst.getName());
        MethodAST method = classAst.getMethods().getFirst();
        assertEquals("DoSomething", method.getName());
        assertEquals(1, method.getInstructions().size());
        assertInstanceOf(AffectAST.class, method.getInstructions().getFirst());
        AffectAST affect = (AffectAST) method.getInstructions().getFirst();
        assertEquals("a", affect.getVariable());
        assertInstanceOf(CharExpressionAST.class, affect.getExpression());
        assertEquals('a', ((CharExpressionAST) affect.getExpression()).value());
    }

    @Test
    void parse8() throws IOException {
        // ARRANGE
        String javaClassContent = """
                class SampleClass {
                int DoSomething(){
                    x.print(1,2);
                }
                }""";
        Parser parser = new Parser();

        // ACT
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));

        // ASSERT
        assertNotNull(classAst);
        assertEquals("SampleClass", classAst.getName());
        MethodAST method = classAst.getMethods().getFirst();
        assertEquals("DoSomething", method.getName());
        assertEquals(1, method.getInstructions().size());
        assertInstanceOf(AppelAST.class, method.getInstructions().getFirst());
        AppelAST appel = (AppelAST) method.getInstructions().getFirst();
        assertTrue(appel.object().isPresent());
        IdentExpressionAST ident = (IdentExpressionAST) appel.object().get();
        assertInstanceOf(IdentExpressionAST.class, ident);
        assertEquals("x", ident.name());
        assertEquals("print", appel.name());
        assertNotNull(appel.parameters());
        assertEquals(2, appel.parameters().size());
        assertInstanceOf(NumberExpressionAST.class, appel.parameters().get(0));
        assertEquals(1, ((NumberExpressionAST) appel.parameters().get(0)).number());
        assertInstanceOf(NumberExpressionAST.class, appel.parameters().get(1));
        assertEquals(2, ((NumberExpressionAST) appel.parameters().get(1)).number());
    }

    @Test
    void parse9() throws IOException {
        // ARRANGE
        String javaClassContent = """
                class SampleClass {
                int DoSomething(){
                    x.print();
                }
                }""";
        Parser parser = new Parser();

        // ACT
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));

        // ASSERT
        assertNotNull(classAst);
        assertEquals("SampleClass", classAst.getName());
        MethodAST method = classAst.getMethods().getFirst();
        assertEquals("DoSomething", method.getName());
        assertEquals(1, method.getInstructions().size());
        assertInstanceOf(AppelAST.class, method.getInstructions().getFirst());
        AppelAST appel = (AppelAST) method.getInstructions().getFirst();
        assertTrue(appel.object().isPresent());
        IdentExpressionAST ident = (IdentExpressionAST) appel.object().get();
        assertInstanceOf(IdentExpressionAST.class, ident);
        assertEquals("x", ident.name());
        assertEquals("print", appel.name());
        assertNotNull(appel.parameters());
        assertEquals(0, appel.parameters().size());
    }

    @Test
    void parse10() throws IOException {
        // ARRANGE
        String javaClassContent = """
                class SampleClass {
                int DoSomething(){
                    print(5,8);
                }
                }""";
        Parser parser = new Parser();

        // ACT
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));

        // ASSERT
        assertNotNull(classAst);
        assertEquals("SampleClass", classAst.getName());
        MethodAST method = classAst.getMethods().getFirst();
        assertEquals("DoSomething", method.getName());
        assertEquals(1, method.getInstructions().size());
        assertInstanceOf(AppelAST.class, method.getInstructions().getFirst());
        AppelAST appel = (AppelAST) method.getInstructions().getFirst();
        assertFalse(appel.object().isPresent());
        assertEquals("print", appel.name());
        assertNotNull(appel.parameters());
        assertEquals(2, appel.parameters().size());
        assertInstanceOf(NumberExpressionAST.class, appel.parameters().get(0));
        assertEquals(5, ((NumberExpressionAST) appel.parameters().get(0)).number());
        assertInstanceOf(NumberExpressionAST.class, appel.parameters().get(1));
        assertEquals(8, ((NumberExpressionAST) appel.parameters().get(1)).number());
    }

    @Test
    void parse11() throws IOException {
        // ARRANGE
        String javaClassContent = """
                class SampleClass {
                int DoSomething(){
                    a="abc".length();
                }
                }""";
        Parser parser = new Parser();

        // ACT
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));

        // ASSERT
        assertNotNull(classAst);
        assertEquals("SampleClass", classAst.getName());
        MethodAST method = classAst.getMethods().getFirst();
        assertEquals("DoSomething", method.getName());
        assertEquals(1, method.getInstructions().size());
        assertInstanceOf(AffectAST.class, method.getInstructions().getFirst());
        AffectAST affect = (AffectAST) method.getInstructions().getFirst();
        assertEquals("a", affect.getVariable());
        assertInstanceOf(AppelExpressionAST.class, affect.getExpression());
        var appel = (AppelExpressionAST) affect.getExpression();
        assertEquals("length", appel.nom());
        assertTrue(appel.objet().isPresent());
        assertInstanceOf(StringAST.class, appel.objet().get());
        assertEquals("abc", ((StringAST) appel.objet().get()).str());
        assertEquals(0, appel.parameters().size());
    }

    @Test
    void parse12() throws IOException {
        // ARRANGE
        String javaClassContent = """
                class SampleClass {
                int DoSomething(){
                    a=call(7,4);
                }
                }""";
        Parser parser = new Parser();

        // ACT
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));

        // ASSERT
        assertNotNull(classAst);
        assertEquals("SampleClass", classAst.getName());
        MethodAST method = classAst.getMethods().getFirst();
        assertEquals("DoSomething", method.getName());
        assertEquals(1, method.getInstructions().size());
        assertInstanceOf(AffectAST.class, method.getInstructions().getFirst());
        AffectAST affect = (AffectAST) method.getInstructions().getFirst();
        assertEquals("a", affect.getVariable());
        assertInstanceOf(AppelExpressionAST.class, affect.getExpression());
        var appel = (AppelExpressionAST) affect.getExpression();
        assertEquals("call", appel.nom());
        assertTrue(appel.objet().isEmpty());
        assertEquals(2, appel.parameters().size());
        assertEquals(7, ((NumberExpressionAST) appel.parameters().get(0)).number());
        assertEquals(4, ((NumberExpressionAST) appel.parameters().get(1)).number());
    }

    @Test
    void parse13() throws IOException {
        // ARRANGE
        String javaClassContent = """
                class SampleClass {
                int DoSomething(){
                    a=call();
                }
                }""";
        Parser parser = new Parser();

        // ACT
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));

        // ASSERT
        assertNotNull(classAst);
        assertEquals("SampleClass", classAst.getName());
        MethodAST method = classAst.getMethods().getFirst();
        assertEquals("DoSomething", method.getName());
        assertEquals(1, method.getInstructions().size());
        assertInstanceOf(AffectAST.class, method.getInstructions().getFirst());
        AffectAST affect = (AffectAST) method.getInstructions().getFirst();
        assertEquals("a", affect.getVariable());
        assertInstanceOf(AppelExpressionAST.class, affect.getExpression());
        var appel = (AppelExpressionAST) affect.getExpression();
        assertEquals("call", appel.nom());
        assertTrue(appel.objet().isEmpty());
        assertEquals(0, appel.parameters().size());
    }

    @Test
    void parse14() throws IOException {
        // ARRANGE
        String javaClassContent = """
                class SampleClass {
                int DoSomething(){
                    a="abc".length(8,10);
                }
                }""";
        Parser parser = new Parser();

        // ACT
        ClassAST classAst = parser.parse(new StringReader(javaClassContent));

        // ASSERT
        assertNotNull(classAst);
        assertEquals("SampleClass", classAst.getName());
        MethodAST method = classAst.getMethods().getFirst();
        assertEquals("DoSomething", method.getName());
        assertEquals(1, method.getInstructions().size());
        assertInstanceOf(AffectAST.class, method.getInstructions().getFirst());
        AffectAST affect = (AffectAST) method.getInstructions().getFirst();
        assertEquals("a", affect.getVariable());
        assertInstanceOf(AppelExpressionAST.class, affect.getExpression());
        var appel = (AppelExpressionAST) affect.getExpression();
        assertEquals("length", appel.nom());
        assertTrue(appel.objet().isPresent());
        assertInstanceOf(StringAST.class, appel.objet().get());
        assertEquals("abc", ((StringAST) appel.objet().get()).str());
        assertEquals(2, appel.parameters().size());
        assertEquals(8, ((NumberExpressionAST) appel.parameters().get(0)).number());
        assertEquals(10, ((NumberExpressionAST) appel.parameters().get(1)).number());
    }


    @Test
    void parse15() {
        // ARRANGE
        String javaClassContent = """
                class SampleClass {
                int DoSomething(){
                    a
                }
                }""";
        Parser parser = new Parser();

        // ACT
        ParseCancellationException exception = assertThrows(ParseCancellationException.class,
                () -> parser.parse(new StringReader(javaClassContent)));

        // ASSERT
        assertEquals("line 4:0 no viable alternative at input 'a\\n}'",
                exception.getMessage());
    }
}