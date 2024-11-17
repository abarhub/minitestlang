package org.minitestlang.runner;

import org.minitestlang.analyser.Analyser;
import org.minitestlang.analyser.AnalyserException;
import org.minitestlang.ast.ClassAST;
import org.minitestlang.interpreter.Interpreter;
import org.minitestlang.interpreter.InterpreterException;
import org.minitestlang.manager.ClassManager;
import org.minitestlang.manager.LoaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

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
        ClassManager manager = new ClassManager();
        try {
            ClassAST ast = manager.chargeFichier(p);
            manager.setClassePrincipale(ast);
            Analyser analyser = new Analyser();
            analyser.analyser(manager);
            Interpreter interpreter = new Interpreter();
            interpreter.run(ast);
        } catch (LoaderException e) {
            LOGGER.error(e.getMessage(),e);
            error("Error loading " + e.getMessage());
        } catch (InterpreterException e) {
            LOGGER.error(e.getMessage(),e);
            error("Error run " + e.getMessage());
        } catch (AnalyserException e) {
            LOGGER.error(e.getMessage(),e);
            error("Error analyse " + e.getMessage());
        }
    }

    private void error(String messageError) {
        LOGGER.debug("Error: {}", messageError);
        System.err.println(messageError);
        System.exit(1);
    }
}
