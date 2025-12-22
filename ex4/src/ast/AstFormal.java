package ast;

import types.*;
import Exceptions.SemanticException;
import symboltable.SymbolTable;
import temp.*;
import ir.*;

public class AstFormal extends AstNode {

    public AstType type;
    public String name;
    public String irName;

    public AstFormal(AstType type, String name) {
        super(type.getLine());
        this.type = type;
        this.name = name;
    }

    @Override
    public void printMe() {
        if (type != null) type.printMe();
        AstGraphviz.getInstance().logNode(serialNumber,String.format("FORMAL\n(%s)", name));
        if (type != null)
            AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
    }

    
    public Type SemantMe() {

        if (SymbolTable.isReservedKeyword(name))
            throw new SemanticException(type.getLine());
        
        Type t = type.SemantMe();

        if (t == TypeVoid.getInstance())
            throw new SemanticException(type.getLine());

        if (SymbolTable.getInstance().findInCurrentScope(name) != null)
            throw new SemanticException(type.getLine());

        this.irName = SymbolTable.getInstance().enterVar(name, t);
        return t;
    }

    public Temp IRme() {
        if (this.irName != null) {
            Ir.getInstance().AddIrCommand(new IrCommandAllocate(this.irName));
            Temp t = TempFactory.getInstance().getFreshTemp();
            Ir.getInstance().AddIrCommand(new IrCommandConstInt(t, 0));  // Dummy value
            Ir.getInstance().AddIrCommand(new IrCommandStore(this.irName, t));
        }
        return null;
    }

}
