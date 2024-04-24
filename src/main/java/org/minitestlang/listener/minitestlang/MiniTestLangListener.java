package org.minitestlang.listener.minitestlang;

import org.antlr.v4.runtime.Token;
import org.minitestlang.antlr.minitestlang.MinitestlangBaseListener;
import org.minitestlang.antlr.minitestlang.MinitestlangParser;
import org.minitestlang.ast.*;
import org.minitestlang.ast.expr.*;
import org.minitestlang.ast.instr.AffectAST;
import org.minitestlang.ast.instr.InstructionAST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MiniTestLangListener extends MinitestlangBaseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MiniTestLangListener.class);

    private ClassAST classAST;
    private List<MethodAST> methodASTs;
    private List<InstructionAST> instructionASTs;
    private List<ExpressionAST> listeExpression=new ArrayList<>();

    @Override
    public void enterClassDef(MinitestlangParser.ClassDefContext ctx) {
        methodASTs = new ArrayList<>();
    }

    @Override
    public void exitClassDef(MinitestlangParser.ClassDefContext ctx) {
        LOGGER.info("class {}", ctx.Identifier());
        classAST = new ClassAST();
        classAST.setName(ctx.Identifier().getText());
        classAST.setMethods(methodASTs);
        classAST.setPositionClass(createPosition(ctx.CLASS().getSymbol()));
        classAST.setPositionName(createPosition(ctx.Identifier().getSymbol()));
    }

    @Override
    public void exitMethod(MinitestlangParser.MethodContext ctx) {
        LOGGER.info("methode {} (return:{})", ctx.Identifier(), ctx.type().getRuleIndex());
        MethodAST methodAST = new MethodAST();
        methodAST.setName(ctx.Identifier().getText());
        methodAST.setInstructions(instructionASTs);
        instructionASTs = null;
        methodASTs.add(methodAST);
    }

    @Override
    public void enterMethod(MinitestlangParser.MethodContext ctx) {
        instructionASTs = new ArrayList<>();
    }

    @Override
    public void exitOpPlusMinus(MinitestlangParser.OpPlusMinusContext ctx) {
        ExpressionAST left,right;
        left=listeExpression.get(listeExpression.size()-2);
        right=listeExpression.get(listeExpression.size()-1);
        listeExpression.remove(listeExpression.size()-1);
        listeExpression.remove(listeExpression.size()-1);
        Operator op;
        if(Objects.equals(ctx.op.getText(),"+")){
            op=Operator.ADD;
        }else if(Objects.equals(ctx.op.getText(),"-")){
            op=Operator.SUB;
        }else {
            throw new IllegalArgumentException("Invalid operator "+ctx.op.getText());
        }
        BinaryOperatorExpressionAST binaryOperatorExpressionAST=new BinaryOperatorExpressionAST(left,right,op);
        listeExpression.add(binaryOperatorExpressionAST);
    }

    @Override
    public void exitOpMultDiv(MinitestlangParser.OpMultDivContext ctx) {
        ExpressionAST left,right;
        left=listeExpression.get(listeExpression.size()-2);
        right=listeExpression.get(listeExpression.size()-1);
        listeExpression.remove(listeExpression.size()-1);
        listeExpression.remove(listeExpression.size()-1);
        Operator op;
        if(Objects.equals(ctx.op.getText(),"*")){
            op=Operator.MULT;
        }else if(Objects.equals(ctx.op.getText(),"/")){
            op=Operator.DIV;
        }else {
            throw new IllegalArgumentException("Invalid operator "+ctx.op.getText());
        }
        BinaryOperatorExpressionAST binaryOperatorExpressionAST=new BinaryOperatorExpressionAST(left,right,op);
        listeExpression.add(binaryOperatorExpressionAST);
    }

    @Override
    public void exitIdent(MinitestlangParser.IdentContext ctx) {
        String nom=ctx.Identifier().getText();
        IdentExpressionAST ident=new IdentExpressionAST(nom,createPosition(ctx.Identifier().getSymbol()));
        listeExpression.add(ident);
    }

    @Override
    public void exitNumber(MinitestlangParser.NumberContext ctx) {
        int n=Integer.parseInt(ctx.Number().getText());
        NumberExpressionAST number=new NumberExpressionAST(n,createPosition(ctx.Number().getSymbol()));
        listeExpression.add(number);
    }

    @Override
    public void exitParentExpr(MinitestlangParser.ParentExprContext ctx) {
        // ne rien faire
    }

    @Override
    public void exitAffect(MinitestlangParser.AffectContext ctx) {
        AffectAST affectAST = new AffectAST();
        affectAST.setVariable(ctx.Identifier().getText());
        ExpressionAST expr=listeExpression.get(listeExpression.size()-1);
        listeExpression.remove(listeExpression.size()-1);
        affectAST.setExpression(expr);
        affectAST.setPositionVariable(createPosition(ctx.Identifier().getSymbol()));
        instructionASTs.add(affectAST);
    }

    public ClassAST getClassAST() {
        return classAST;
    }

    public void setClassAST(ClassAST classAST) {
        this.classAST = classAST;
    }

    private PositionAST createPosition(Token token){
        return new PositionAST(token.getLine(),token.getCharPositionInLine());
    }
}
