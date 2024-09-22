package org.minitestlang.listener.minitestlang;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.minitestlang.antlr.minitestlang.MinitestlangBaseListener;
import org.minitestlang.antlr.minitestlang.MinitestlangParser;
import org.minitestlang.ast.ClassAST;
import org.minitestlang.ast.MethodAST;
import org.minitestlang.ast.PositionAST;
import org.minitestlang.ast.expr.*;
import org.minitestlang.ast.instr.*;
import org.minitestlang.ast.type.*;
import org.minitestlang.listener.minitestlang.result.ResultClass;
import org.minitestlang.listener.minitestlang.result.ResultExpr;
import org.minitestlang.listener.minitestlang.result.ResultInstr;
import org.minitestlang.listener.minitestlang.result.ResultMethod;
import org.minitestlang.utils.VerifyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class MiniTestLangListener extends MinitestlangBaseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MiniTestLangListener.class);

    private ClassAST classAST;

    @Override
    public void exitClassDef(MinitestlangParser.ClassDefContext ctx) {
        LOGGER.info("class {}", ctx.Identifier());

        classAST = new ClassAST();
        classAST.setName(ctx.Identifier().getText());
        if (ctx.method() != null) {
            var liste = ctx.method().stream()
                    .map(x -> x.result.method())
                    .toList();
            classAST.setMethods(liste);
        }
        classAST.setPositionClass(createPosition(ctx.CLASS().getSymbol()));
        classAST.setPositionName(createPosition(ctx.Identifier().getSymbol()));
        ctx.result = new ResultClass(classAST);
    }

    @Override
    public void exitMethod(MinitestlangParser.MethodContext ctx) {
        LOGGER.info("methode {} (return:{})", ctx.Identifier(), ctx.typeMethod().getRuleIndex());
        MethodAST methodAST = new MethodAST();
        methodAST.setName(ctx.Identifier().getText());
        if (!CollectionUtils.isEmpty(ctx.instr())) {
            var instr = ctx.instr().stream()
                    .flatMap(x -> {
                        if (x.result != null && !CollectionUtils.isEmpty(x.result.instructions())) {
                            return x.result.instructions().stream();
                        } else {
                            return Stream.empty();
                        }
                    })
                    .toList();
            methodAST.setInstructions(instr);
        }
        ctx.result = new ResultMethod(methodAST);
    }

    @Override
    public void exitDeclareVariable(MinitestlangParser.DeclareVariableContext ctx) {
        TypeAST type = createType(ctx.type().getStart());

        List<InstructionAST> listeInstructions = new ArrayList<>();
        List<MinitestlangParser.Decl_varContext> listeDeclaration = ctx.decl_var();
        for (MinitestlangParser.Decl_varContext declareVar : listeDeclaration) {
            TerminalNode ident = declareVar.Identifier();
            String name = ident.getText();
            ExpressionAST expressionAST = null;
            if (declareVar.expression() != null) {
                MinitestlangParser.ExpressionContext expr = declareVar.expression();
                if (expr != null && expr.expr != null) {
                    expressionAST = expr.expr.expr();
                }
            }
            DeclareAST declareAST = new DeclareAST(type, name, Optional.ofNullable(expressionAST), createPosition(ident.getSymbol()));
            listeInstructions.add(declareAST);
        }
        ctx.result = new ResultInstr(listeInstructions);
    }

    @Override
    public void exitOpPlusMinus(MinitestlangParser.OpPlusMinusContext ctx) {
        createBinaryExpression(ctx.expression().get(0), ctx.expression().get(1), ctx.op, ctx);
    }

    @Override
    public void exitOpMultDiv(MinitestlangParser.OpMultDivContext ctx) {
        createBinaryExpression(ctx.expression().get(0), ctx.expression().get(1), ctx.op, ctx);
    }

    @Override
    public void exitOpEquals(MinitestlangParser.OpEqualsContext ctx) {
        createBinaryExpression(ctx.expression().get(0), ctx.expression().get(1), ctx.op, ctx);
    }

    @Override
    public void exitOpAndOr(MinitestlangParser.OpAndOrContext ctx) {
        createBinaryExpression(ctx.expression().get(0), ctx.expression().get(1), ctx.op, ctx);
    }

    private void createBinaryExpression(MinitestlangParser.ExpressionContext exprLeft,
                                        MinitestlangParser.ExpressionContext exprRight,
                                        Token operator, MinitestlangParser.ExpressionContext ctx) {
        ExpressionAST left, right;
        left = exprLeft.expr.expr();
        right = exprRight.expr.expr();
        Operator op;
        if (Objects.equals(operator.getText(), "+")) {
            op = Operator.ADD;
        } else if (Objects.equals(operator.getText(), "-")) {
            op = Operator.SUB;
        } else if (Objects.equals(operator.getText(), "*")) {
            op = Operator.MULT;
        } else if (Objects.equals(operator.getText(), "/")) {
            op = Operator.DIV;
        } else if (Objects.equals(operator.getText(), "%")) {
            op = Operator.MOD;
        } else if (Objects.equals(operator.getText(), "&&")) {
            op = Operator.AND;
        } else if (Objects.equals(operator.getText(), "||")) {
            op = Operator.OR;
        } else if (Objects.equals(operator.getText(), "==")) {
            op = Operator.EQUAL;
        } else if (Objects.equals(operator.getText(), "!=")) {
            op = Operator.NOTEQUAL;
        } else if (Objects.equals(operator.getText(), "<=")) {
            op = Operator.LE;
        } else if (Objects.equals(operator.getText(), ">=")) {
            op = Operator.GE;
        } else if (Objects.equals(operator.getText(), ">")) {
            op = Operator.GT;
        } else if (Objects.equals(operator.getText(), "<")) {
            op = Operator.LT;
        } else {
            throw new IllegalArgumentException("Invalid operator " + operator.getText() +
                    " in position " + createPosition(operator));
        }
        BinaryOperatorExpressionAST binaryOperatorExpressionAST = new BinaryOperatorExpressionAST(
                left, right, op, createPosition(operator));
        ctx.expr = new ResultExpr(binaryOperatorExpressionAST);
    }

    @Override
    public void exitIdent(MinitestlangParser.IdentContext ctx) {
        String nom = ctx.Identifier().getText();
        IdentExpressionAST ident = new IdentExpressionAST(nom, createPosition(ctx.Identifier().getSymbol()));
        ctx.expr = new ResultExpr(ident);
    }

    @Override
    public void exitNumber(MinitestlangParser.NumberContext ctx) {
        int n = Integer.parseInt(ctx.Number().getText());
        NumberExpressionAST number = new NumberExpressionAST(n, createPosition(ctx.Number().getSymbol()));
        ctx.expr = new ResultExpr(number);
    }

    @Override
    public void exitBooleanValue(MinitestlangParser.BooleanValueContext ctx) {
        boolean b = Boolean.parseBoolean(ctx.getText());
        BooleanExpressionAST bool = new BooleanExpressionAST(b, createPosition(ctx.getStart()));
        ctx.expr = new ResultExpr(bool);
    }

    @Override
    public void exitStr(MinitestlangParser.StrContext ctx) {
        String str = ctx.getText();
        VerifyUtils.verify(str.startsWith("\""), "string bad");
        VerifyUtils.verify(str.endsWith("\""), "string bad");
        VerifyUtils.verify(str.length() >= 2, "string bad");
        str = str.substring(1, str.length() - 1);
        StringAST stringAST = new StringAST(str, createPosition(ctx.getStart()));
        ctx.expr = new ResultExpr(stringAST);
    }

    @Override
    public void exitChr(MinitestlangParser.ChrContext ctx) {
        String str = ctx.getText();
        VerifyUtils.verify(str.startsWith("'"), "char bad");
        VerifyUtils.verify(str.endsWith("'"), "char bad");
        VerifyUtils.verify(str.length() == 3, "char bad");
        str = str.substring(1, str.length() - 1);
        CharExpressionAST stringAST = new CharExpressionAST(str.charAt(0), createPosition(ctx.getStart()));
        ctx.expr = new ResultExpr(stringAST);
    }

    @Override
    public void exitParentExpr(MinitestlangParser.ParentExprContext ctx) {
        // ne rien faire
    }

    @Override
    public void exitAffect(MinitestlangParser.AffectContext ctx) {
        AffectAST affectAST = new AffectAST();
        affectAST.setVariable(ctx.Identifier().getText());
        ExpressionAST expr = ctx.expression().expr.expr();
        affectAST.setExpression(expr);
        affectAST.setPositionVariable(createPosition(ctx.Identifier().getSymbol()));
        ctx.result = new ResultInstr(List.of(affectAST));
    }

    @Override
    public void exitIfInstr(MinitestlangParser.IfInstrContext ctx) {
        ExpressionAST expr = null;
        if (ctx.parExpression() != null) {
            expr = ctx.parExpression().expr.expr();
        }
        List<InstructionAST> instructionsIf = new ArrayList<>();
        List<InstructionAST> instructionsElse = new ArrayList<>();
        if (ctx.instr() != null) {
            if (!ctx.instr().isEmpty() &&
                    ctx.instr().get(0).result != null &&
                    ctx.instr().get(0).result.instructions() != null) {
                instructionsIf = ctx.instr().get(0).result.instructions();
            }
            if (ctx.instr().size() > 1 &&
                    ctx.instr().get(1).result != null &&
                    ctx.instr().get(1).result.instructions() != null) {
                instructionsElse = ctx.instr().get(1).result.instructions();
            }
        }
        var instr = new IfAST(expr,
                instructionsIf,
                instructionsElse);
        ctx.result = new ResultInstr(List.of(instr));
    }

    @Override
    public void exitWhileInstr(MinitestlangParser.WhileInstrContext ctx) {
        ExpressionAST expr = null;
        if (ctx.parExpression() != null) {
            expr = ctx.parExpression().expr.expr();
        }
        List<InstructionAST> instructionAST = new ArrayList<>();
        if (ctx.instr() != null) {
            instructionAST = ctx.instr().result.instructions();
        }
        var instr = new WhileAST(expr, instructionAST);
        ctx.result = new ResultInstr(List.of(instr));
    }

    @Override
    public void exitAppelInstr(MinitestlangParser.AppelInstrContext ctx) {
        String nom = ctx.Identifier().getText();
        List<ExpressionAST> listeExpr = new ArrayList<>();
        if (ctx.expression() != null) {
            listeExpr = ctx.expression().stream()
                    .map(x -> x.expr.expr())
                    .toList();
        }
        InstructionAST instr = new AppelAST(nom, listeExpr);
        ctx.result = new ResultInstr(List.of(instr));
    }

    @Override
    public void exitParExpression(MinitestlangParser.ParExpressionContext ctx) {
        ctx.expr = ctx.expression().expr;
    }

    @Override
    public void exitBlockInstr(MinitestlangParser.BlockInstrContext ctx) {
        var instr = new BlockAST(ctx.instr().stream()
                .flatMap(x -> x.result.instructions().stream())
                .toList());
        ctx.result = new ResultInstr(List.of(instr));
    }


    public ClassAST getClassAST() {
        return classAST;
    }

    private PositionAST createPosition(Token token) {
        return new PositionAST(token.getLine(), token.getCharPositionInLine() + 1);
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
