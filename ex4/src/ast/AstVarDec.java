package ast;

import Exceptions.SemanticException;
import symboltable.SymbolTable;
import types.*;
import temp.*;
import ir.*;

public class AstVarDec extends AstDec {

    public AstType type;
    public String name;
    public AstExp init;
    public String irName;

    public AstVarDec(AstType type, String name, AstExp init, int lineNumber) {
        super(lineNumber);
        this.type = type;
        this.name = name;
        this.init = init;
    }

    @Override
    public void printMe() {

        if (type != null) type.printMe();
        if (init != null) init.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "VARDEC(" + name + ")");
        if (type != null) AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        if (init != null) AstGraphviz.getInstance().logEdge(serialNumber, init.serialNumber);
    }

    @Override
    public Type SemantMe() {
        if (SymbolTable.isReservedKeyword(name))
            throw new SemanticException(lineNumber);
        
        Type t = type.SemantMe();
        if (t == TypeVoid.getInstance())
            throw new SemanticException(lineNumber);

        if (SymbolTable.getInstance().findInCurrentScope(name) != null)
            throw new SemanticException(lineNumber);

        Type initType = null;
        if (init != null) {
            initType = init.SemantMe();

            if (initType == TypeNil.getInstance()) {
                if (!(t instanceof TypeClass) && !(t instanceof TypeArray))
                    throw new SemanticException(lineNumber);
            }

            if (!t.isAssignableFrom(initType))
                throw new SemanticException(lineNumber);
        }

        this.irName = SymbolTable.getInstance().enterVar(name, t);
        return t;
    }

    @Override
    public Temp IRme() {
        Ir.getInstance().AddIrCommand(new IrCommandAllocate(this.irName));
        if (init != null) {
            Temp src = init.IRme();
            Ir.getInstance().AddIrCommand(new IrCommandStore(this.irName, src));
        }
        return null;
    }
}
