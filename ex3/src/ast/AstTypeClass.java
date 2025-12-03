package ast;

import Exceptions.SemanticException;
import symboltable.SymbolTable;
import types.*;

public class AstTypeClass extends AstType {

    public String name;

    public AstTypeClass(String name, int lineNumber) {
        super(lineNumber);
        this.name = name;
    }

    @Override
    public void printMe() {
        AstGraphviz.getInstance().logNode(serialNumber,"TYPE_CLASS(" + name + ")");
    }

    @Override
    public Type SemantMe() {
        System.out.println("SemantMe: " + this.getClass().getSimpleName());

        Type t = SymbolTable.getInstance().find(name);

        // must exist
        if (t == null)
            throw new SemanticException(lineNumber);

        // cannot use void (except as function return type—not here)
        if (t == TypeVoid.getInstance())
            throw new SemanticException(lineNumber);

        return t;
    }

}
