package org.minitestlang.listener.minitestlang;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.minitestlang.antlr.minitestlang.MinitestlangLexer;
import org.minitestlang.antlr.minitestlang.MinitestlangParser;
import org.minitestlang.ast.ClassAST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;

public class Parser {

    private static final Logger LOGGER = LoggerFactory.getLogger(Parser.class);

    public ClassAST parse(Reader reader) throws IOException,ListenerException {
        MinitestlangLexer minitestlangLexer = new MinitestlangLexer(CharStreams.fromReader(reader));

        CommonTokenStream tokens = new CommonTokenStream(minitestlangLexer);
        MinitestlangParser parser = new MinitestlangParser(tokens);
        ParseTree tree = parser.main();

        ParseTreeWalker walker = new ParseTreeWalker();
        MiniTestLangListener listener = new MiniTestLangListener();

        walker.walk(listener, tree);
        return listener.getClassAST();
    }
}
