package ast;

import Exceptions.SemanticException;
import symboltable.SymbolTable;
import types.*;
import temp.*;
import ir.*;

public class AstVarSimple extends AstVar {

    public String name;
    public String irName;

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
        Type t = SymbolTable.getInstance().find(name);

        if (t == null)
            throw new SemanticException(lineNumber);
        
        this.irName = SymbolTable.getInstance().getIrName(name);
        return t;
    }

    @Override
    public Temp IRme() {
        Temp t = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandLoad(t, this.irName));
        return t;
    }
}
