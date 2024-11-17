package org.minitestlang.manager;

import org.minitestlang.analyser.ClassTable;
import org.minitestlang.ast.ClassAST;
import org.minitestlang.listener.minitestlang.ListenerException;
import org.minitestlang.listener.minitestlang.Parser;
import org.minitestlang.utils.VerifyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ClassManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassManager.class);

    private ClassTable classTable;
    private ClassAST classePrincipale;

    public ClassManager() {
        classTable = new ClassTable();
    }

    public void setClassePrincipale(ClassAST ast) {
        VerifyUtils.verify(ast != null, "ast is null");
        VerifyUtils.verify(!classTable.classeExiste(ast.getName()), "classe " + ast.getName() + " exists");
        classePrincipale = ast;
        classTable.ajouterClasse(ast);
    }

    public ClassAST getClassePrincipale() {
        return classePrincipale;
    }

    public ClassAST chargeFichier(Path fichier) throws LoaderException {
        try {
            LOGGER.info("loading class {}", fichier);
            String s = Files.readString(fichier);
            try (StringReader reader = new StringReader(s)) {
                Parser parser = new Parser();
                return parser.parse(reader);
            } catch (ListenerException e) {
                throw new LoaderException("Error for parse file " + fichier, e);
            }
        } catch (IOException e) {
            throw new LoaderException("Error for read file " + fichier, e);
        }
    }

    public ClassAST chargeClasse(String className) throws LoaderException {
        Optional<ClassAST> classOpt = classTable.getClasse(className);
        if (classOpt.isPresent()) {
            return classOpt.get();
        } else {
            Path p = Path.of("../lib/base/" + className + ".mtl");
            if (Files.exists(p)) {
                ClassAST ast = chargeFichier(p);
                classTable.ajouterClasse(ast);
                return ast;
            } else {
                throw new LoaderException("Error class " + className + " not exists");
            }
        }
    }
}
