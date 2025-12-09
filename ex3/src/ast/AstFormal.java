package ast;

import types.*;
import Exceptions.SemanticException;
import symboltable.SymbolTable;

public class AstFormal extends AstNode {

    public AstType type;
    public String name;

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
        System.out.println("SemantMe: " + this.getClass().getSimpleName());

        if (SymbolTable.isReservedKeyword(name))
            throw new SemanticException(type.getLine());
        
        Type t = type.SemantMe();

        if (t == TypeVoid.getInstance())
            throw new SemanticException(type.getLine());

        SymbolTable.getInstance().enter(name, t);
        return t;
    }


}
