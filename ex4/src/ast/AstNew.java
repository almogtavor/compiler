package ast;

import types.*;
import symboltable.SymbolTable;
import Exceptions.SemanticException;
import temp.*;
import ir.*;

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
        Type t = type.SemantMe();

        if (t == TypeVoid.getInstance())
            throw new SemanticException(lineNumber);

        if (size == null) {
            if (!(t instanceof TypeClass))
                throw new SemanticException(lineNumber);

            return t;
        }

        Type sizeType = size.SemantMe();
        if (sizeType != TypeInt.getInstance())
            throw new SemanticException(lineNumber);

        if (size instanceof AstExpInt) {
            AstExpInt sizeInt = (AstExpInt) size;
            if (sizeInt.value <= 0)
                throw new SemanticException(lineNumber);
        }

        return new TypeArray(t.name, t);
    }

    @Override
    public Temp IRme() {
        Temp t = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandConstInt(t, 0));
        return t;
    }
}
