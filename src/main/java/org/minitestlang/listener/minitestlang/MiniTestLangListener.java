package org.minitestlang.listener.minitestlang;

import org.minitestlang.antlr.minitestlang.MinitestlangBaseListener;
import org.minitestlang.antlr.minitestlang.MinitestlangParser;
import org.minitestlang.ast.*;
import org.minitestlang.ast.expr.NumberExpressionAST;
import org.minitestlang.ast.instr.AffectAST;
import org.minitestlang.ast.instr.InstructionAST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MiniTestLangListener extends MinitestlangBaseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MiniTestLangListener.class);

    private ClassAST classAST;
    private List<MethodAST> methodASTs;
    private List<InstructionAST> instructionASTs;

    @Override
    public void enterClassDef(MinitestlangParser.ClassDefContext ctx) {
        methodASTs=new ArrayList<>();
    }

    @Override
    public void exitClassDef(MinitestlangParser.ClassDefContext ctx) {
        LOGGER.info("class {}",ctx.Identifier());
        classAST=new ClassAST();
        classAST.setName(ctx.Identifier().getText());
        classAST.setMethods(methodASTs);
    }

    @Override
    public void exitMethod(MinitestlangParser.MethodContext ctx) {
        LOGGER.info("methode {} (return:{})",ctx.Identifier(),ctx.type().getRuleIndex());
        MethodAST methodAST=new MethodAST();
        methodAST.setName(ctx.Identifier().getText());
        methodAST.setInstructions(instructionASTs);
        instructionASTs=null;
        methodASTs.add(methodAST);
    }

    @Override
    public void enterMethod(MinitestlangParser.MethodContext ctx) {
        instructionASTs=new ArrayList<>();
    }

    @Override
    public void exitAffect(MinitestlangParser.AffectContext ctx) {
        AffectAST affectAST = new AffectAST();
        affectAST.setVariable(ctx.Identifier().getText());
        NumberExpressionAST numberExpressionAST=new NumberExpressionAST();
        numberExpressionAST.setNumber(Integer.parseInt(ctx.Number().getText()));
        affectAST.setExpression(numberExpressionAST);
        instructionASTs.add(affectAST);
    }

}
