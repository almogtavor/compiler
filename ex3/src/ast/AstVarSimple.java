package ast;

import Exceptions.SemanticException;
import symboltable.SymbolTable;
import types.*;

public class AstVarSimple extends AstVar {

    public String name;

    public AstVarSimple(String name, int lineNumber) {
        super(lineNumber);
        this.name = name;
    }

    @Override
    public void printMe() {
        AstGraphviz.getInstance().logNode(serialNumber,String.format("VAR_SIMPLE(%s)", name));
    }

    @Override
    public Type SemantMe() {
        System.out.println("SemantMe: " + this.getClass().getSimpleName());

        Type t = SymbolTable.getInstance().find(name);

        if (t == null)
            throw new SemanticException(lineNumber);
        
        return t;
    }
}
