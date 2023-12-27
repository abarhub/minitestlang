package org.minitestlang.listener.minitestlang;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.minitestlang.antlr.java.Java8Lexer;
import org.minitestlang.antlr.java.Java8Parser;
import org.minitestlang.antlr.minitestlang.MinitestlangLexer;
import org.minitestlang.antlr.minitestlang.MinitestlangParser;
import org.minitestlang.listener.java.UppercaseMethodListener;

import static org.junit.jupiter.api.Assertions.*;

class MiniTestLangListenerTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void exitClassDef() {
        String javaClassContent = "class SampleClass { int DoSomething(){} }";
        MinitestlangLexer java8Lexer = new MinitestlangLexer(CharStreams.fromString(javaClassContent));

        CommonTokenStream tokens = new CommonTokenStream(java8Lexer);
        MinitestlangParser parser = new MinitestlangParser(tokens);
        ParseTree tree = parser.main();

        ParseTreeWalker walker = new ParseTreeWalker();
        MiniTestLangListener listener= new MiniTestLangListener();

        walker.walk(listener, tree);
    }
}