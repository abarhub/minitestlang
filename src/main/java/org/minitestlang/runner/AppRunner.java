package org.minitestlang.runner;

import org.minitestlang.analyser.Analyser;
import org.minitestlang.analyser.AnalyserException;
import org.minitestlang.ast.ClassAST;
import org.minitestlang.interpreter.Interpreter;
import org.minitestlang.interpreter.InterpreterException;
import org.minitestlang.listener.minitestlang.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class AppRunner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppRunner.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (args.getSourceArgs() != null && args.getSourceArgs().length == 1) {
            String file = args.getSourceArgs()[0];
            parse(file);
        } else {
            error("Bad argument");
        }
    }

    private void parse(String file) {
        Path p = Path.of(file);
        if (Files.notExists(p)) {
            error("File " + p + " not exists");
        }
        try {
            String s = Files.readString(p);
            try (StringReader reader = new StringReader(s)) {
                Parser parser = new Parser();
                ClassAST ast = parser.parse(reader);
                Analyser analyser = new Analyser();
                analyser.analyser(ast);
                Interpreter interpreter = new Interpreter();
                interpreter.run(ast);
            } catch (InterpreterException e) {
                error("Error run " + e.getMessage());
            } catch (AnalyserException e) {
                error("Error analyse " + e.getMessage());
            }
        } catch (IOException e) {
            error("Error for read file " + p);
        }
    }

    private void error(String messageError) {
        LOGGER.debug("Error: {}", messageError);
        System.err.println(messageError);
        System.exit(1);
    }
}
