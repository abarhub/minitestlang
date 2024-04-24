package org.minitestlang.listener.minitestlang;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.minitestlang.antlr.minitestlang.MinitestlangBaseListener;
import org.minitestlang.antlr.minitestlang.MinitestlangParser;
import org.minitestlang.ast.*;
import org.minitestlang.ast.expr.*;
import org.minitestlang.ast.instr.AffectAST;
import org.minitestlang.ast.instr.DeclareAST;
import org.minitestlang.ast.instr.InstructionAST;
import org.minitestlang.ast.type.BooleanTypeAST;
import org.minitestlang.ast.type.IntTypeAST;
import org.minitestlang.ast.type.TypeAST;
import org.minitestlang.ast.type.VoidTypeAST;
import org.minitestlang.listener.minitestlang.result.ResultExpr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MiniTestLangListener extends MinitestlangBaseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MiniTestLangListener.class);

    private ClassAST classAST;
    private List<MethodAST> methodASTs;
    private List<InstructionAST> instructionASTs;

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
        LOGGER.info("methode {} (return:{})", ctx.Identifier(), ctx.typeMethod().getRuleIndex());
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
    public void exitDeclareVariable(MinitestlangParser.DeclareVariableContext ctx) {
        TypeAST type = createType(ctx.type().getStart());

        List<MinitestlangParser.Decl_varContext> listeDeclaration=ctx.decl_var();
        for (int i = 0; i < listeDeclaration.size(); i++) {
            MinitestlangParser.Decl_varContext declareVar=listeDeclaration.get(i);
            TerminalNode ident = declareVar.Identifier();
            String name = ident.getText();
            ExpressionAST expressionAST=null;
            if (declareVar.expression() != null ) {
                MinitestlangParser.ExpressionContext expr = declareVar.expression();
                if (expr != null&&expr.expr!=null) {
                    expressionAST=expr.expr.expr();
                }
            }
            DeclareAST declareAST = new DeclareAST(type, name, Optional.ofNullable(expressionAST), createPosition(ident.getSymbol()));
            instructionASTs.add(declareAST);
        }
    }

    @Override
    public void exitOpPlusMinus(MinitestlangParser.OpPlusMinusContext ctx) {
        ExpressionAST left, right;
        left=ctx.expression().get(0).expr.expr();
        right=ctx.expression().get(1).expr.expr();
        Operator op;
        if (Objects.equals(ctx.op.getText(), "+")) {
            op = Operator.ADD;
        } else if (Objects.equals(ctx.op.getText(), "-")) {
            op = Operator.SUB;
        } else {
            throw new IllegalArgumentException("Invalid operator " + ctx.op.getText());
        }
        BinaryOperatorExpressionAST binaryOperatorExpressionAST = new BinaryOperatorExpressionAST(left, right, op);
        ctx.expr=new ResultExpr(binaryOperatorExpressionAST);
    }

    @Override
    public void exitOpMultDiv(MinitestlangParser.OpMultDivContext ctx) {
        ExpressionAST left, right;
        left=ctx.expression().get(0).expr.expr();
        right=ctx.expression().get(1).expr.expr();
        Operator op;
        if (Objects.equals(ctx.op.getText(), "*")) {
            op = Operator.MULT;
        } else if (Objects.equals(ctx.op.getText(), "/")) {
            op = Operator.DIV;
        } else {
            throw new IllegalArgumentException("Invalid operator " + ctx.op.getText());
        }
        BinaryOperatorExpressionAST binaryOperatorExpressionAST = new BinaryOperatorExpressionAST(left, right, op);
        ctx.expr=new ResultExpr(binaryOperatorExpressionAST);
    }

    @Override
    public void exitIdent(MinitestlangParser.IdentContext ctx) {
        String nom = ctx.Identifier().getText();
        IdentExpressionAST ident = new IdentExpressionAST(nom, createPosition(ctx.Identifier().getSymbol()));
        ctx.expr=new ResultExpr(ident);
    }

    @Override
    public void exitNumber(MinitestlangParser.NumberContext ctx) {
        int n = Integer.parseInt(ctx.Number().getText());
        NumberExpressionAST number = new NumberExpressionAST(n, createPosition(ctx.Number().getSymbol()));
        ctx.expr=new ResultExpr(number);
    }

    @Override
    public void exitParentExpr(MinitestlangParser.ParentExprContext ctx) {
        // ne rien faire
    }

    @Override
    public void exitAffect(MinitestlangParser.AffectContext ctx) {
        AffectAST affectAST = new AffectAST();
        affectAST.setVariable(ctx.Identifier().getText());
        ExpressionAST expr =ctx.expression().expr.expr();
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

    private PositionAST createPosition(Token token) {
        return new PositionAST(token.getLine(), token.getCharPositionInLine());
    }

    private TypeAST createType(Token token) {
        if (Objects.equals(token.getText(), "int")) {
            return new IntTypeAST(createPosition(token));
        } else if (Objects.equals(token.getText(), "boolean")) {
            return new BooleanTypeAST(createPosition(token));
        } else if (Objects.equals(token.getText(), "void")) {
            return new VoidTypeAST(createPosition(token));
        } else {
            throw new ListenerException("Invalid type " + token.getText());
        }
    }
}
