package ast;

import Exceptions.SemanticException;
import symboltable.SymbolTable;
import types.*;
import temp.*;

public class AstArrayTypedef extends AstDec {

    public AstType type;
    public String name;

    public AstArrayTypedef(AstType type, String name, int lineNumber) {
        super(lineNumber);
        this.type = type;
        this.name = name;
    }

    @Override
    public void printMe() {
        if (type != null) type.printMe();
        AstGraphviz.getInstance().logNode(serialNumber,String.format("ARRAY_TYPEDEF(%s)", name));
        if (type != null)
            AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
    }

    @Override
    public TypeArray SemantMe() {
 
        if (SymbolTable.isReservedKeyword(name))
            throw new SemanticException(lineNumber);

        Type elementType = type.SemantMe();

        if (elementType == TypeVoid.getInstance())
            throw new SemanticException(lineNumber);

        if (SymbolTable.getInstance().findInCurrentScope(name) != null)
            throw new SemanticException(lineNumber);

        TypeArray arrType = new TypeArray(name, elementType);

        SymbolTable.getInstance().enter(name, arrType);

        return arrType;
    }

    @Override
    public Temp IRme() {
        return null;
    }
}
