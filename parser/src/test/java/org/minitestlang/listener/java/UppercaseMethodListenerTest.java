package org.minitestlang.listener.java;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;
import org.minitestlang.antlr.java.Java8Lexer;
import org.minitestlang.antlr.java.Java8Parser;

import static org.junit.jupiter.api.Assertions.*;

class UppercaseMethodListenerTest {

    @Test
    void enterMethodDeclarator() {
        String javaClassContent = "public class SampleClass { void DoSomething(){} }";
        Java8Lexer java8Lexer = new Java8Lexer(CharStreams.fromString(javaClassContent));

        CommonTokenStream tokens = new CommonTokenStream(java8Lexer);
        Java8Parser parser = new Java8Parser(tokens);
        ParseTree tree = parser.compilationUnit();

        ParseTreeWalker walker = new ParseTreeWalker();
        UppercaseMethodListener listener= new UppercaseMethodListener();

        walker.walk(listener, tree);

        assertEquals(listener.getErrors().size(), 1);
        assertEquals(listener.getErrors().get(0),
                "Method DoSomething is uppercased!");
    }
}