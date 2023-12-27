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
import org.minitestlang.ast.ClassAST;
import org.minitestlang.ast.MethodAST;
import org.minitestlang.listener.java.UppercaseMethodListener;

import static org.junit.jupiter.api.Assertions.*;

class MiniTestLangListenerTest {

    @Test
    void exitClassDef() {
        String javaClassContent = "class SampleClass { int DoSomething(){} }";

        ClassAST classAST=parse(javaClassContent);

        assertNotNull(classAST);
        assertEquals("SampleClass",classAST.getName());
        assertEquals(1,classAST.getMethods().size());
        MethodAST method=classAST.getMethods().get(0);
        assertEquals("DoSomething",method.getName());
        assertEquals(0,method.getInstructions().size());
    }

    private ClassAST parse(String s){
        MinitestlangLexer minitestlangLexer = new MinitestlangLexer(CharStreams.fromString(s));

        CommonTokenStream tokens = new CommonTokenStream(minitestlangLexer);
        MinitestlangParser parser = new MinitestlangParser(tokens);
        ParseTree tree = parser.main();

        ParseTreeWalker walker = new ParseTreeWalker();
        MiniTestLangListener listener= new MiniTestLangListener();

        walker.walk(listener, tree);
        return listener.getClassAST();
    }
}