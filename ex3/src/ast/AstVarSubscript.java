package ast;

import Exceptions.SemanticException;
import symboltable.SymbolTable;
import types.*;

public class AstVarSubscript extends AstVar {

    public AstVar var;
    public AstExp subscript;

    public AstVarSubscript(AstVar var, AstExp subscript, int lineNumber) {
        super(lineNumber);
        this.var = var;
        this.subscript = subscript;
    }

    @Override
    public void printMe() {
        if (var != null) var.printMe();
        if (subscript != null) subscript.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "SUBSCRIPT");

        if (var != null)
            AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);

        if (subscript != null)
            AstGraphviz.getInstance().logEdge(serialNumber, subscript.serialNumber);
    }

    @Override
    public Type SemantMe() {

        Type t = var.SemantMe();

        if (!(t instanceof TypeArray))
            throw new SemanticException(lineNumber);

        Type tSub = subscript.SemantMe();
        if (tSub != TypeInt.getInstance())
            throw new SemanticException(lineNumber);

        if (subscript instanceof AstExpInt) {
            AstExpInt subInt = (AstExpInt) subscript;
            if (subInt.value < 0)
                throw new SemanticException(lineNumber);
        }

        return ((TypeArray) t).elementType;
    }
}
