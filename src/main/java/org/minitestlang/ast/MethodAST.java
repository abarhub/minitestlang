package org.minitestlang.ast;

import org.minitestlang.ast.instr.InstructionAST;
import org.minitestlang.ast.type.TypeAST;

import java.util.List;

public class MethodAST {

    private String name;

    private TypeAST returnType;

    private List<TypeAST> parameters;

    private List<InstructionAST> instructions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypeAST getReturnType() {
        return returnType;
    }

    public void setReturnType(TypeAST returnType) {
        this.returnType = returnType;
    }

    public List<TypeAST> getParameters() {
        return parameters;
    }

    public void setParameters(List<TypeAST> parameters) {
        this.parameters = parameters;
    }

    public List<InstructionAST> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<InstructionAST> instructions) {
        this.instructions = instructions;
    }
}
