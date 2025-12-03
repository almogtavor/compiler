package ast;

import types.*;
import symboltable.SymbolTable;
import Exceptions.SemanticException;

public class AstNew extends AstExp {

    public AstType type;
    public AstExp size;

    public AstNew(AstType type, AstExp size,int lineNumber) {
        super(lineNumber);
        this.type = type;
        this.size = size;
    }

    @Override
    public void printMe() {

        if (type != null) type.printMe();
        if (size != null) size.printMe();

        AstGraphviz.getInstance().logNode(serialNumber,(size == null) ? "NEW" : "NEW_ARRAY");

        if (type != null)
            AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        if (size != null)
            AstGraphviz.getInstance().logEdge(serialNumber, size.serialNumber);
    }

    @Override
    public Type SemantMe() {
System.out.println("SemantMe: " + this.getClass().getSimpleName());

        Type t = type.SemantMe();

        if (t == TypeVoid.getInstance())
            throw new SemanticException(lineNumber);

        // new ClassName
        if (size == null) {
            if (!(t instanceof TypeClass))
                throw new SemanticException(lineNumber);

            return t;
        }

        // new T[exp]
        Type sizeType = size.SemantMe();
        if (sizeType != TypeInt.getInstance())
            throw new SemanticException(lineNumber);

        // בדיקת קבוע > 0
        if (size instanceof AstExpInt) {
            AstExpInt sizeInt = (AstExpInt) size;
            if (sizeInt.value <= 0)
                throw new SemanticException(lineNumber);
        }

        return new TypeArray(t.name, t);
    }

}
