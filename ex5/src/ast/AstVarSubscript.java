package ast;

import Exceptions.SemanticException;
import symboltable.SymbolTable;
import types.*;
import temp.*;
import ir.*;

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
        if (var != null) AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
        if (subscript != null) AstGraphviz.getInstance().logEdge(serialNumber, subscript.serialNumber);
    }

    @Override
    public Type SemantMe() {
        Type t = var.SemantMe();
        if (!(t instanceof TypeArray)) throw new SemanticException(lineNumber);
        Type tSub = subscript.SemantMe();
        if (tSub != TypeInt.getInstance()) throw new SemanticException(lineNumber);
        if (subscript instanceof AstExpInt && ((AstExpInt) subscript).value < 0)
            throw new SemanticException(lineNumber);
        return ((TypeArray) t).elementType;
    }

    @Override
    public Temp IRme() {
        Temp arrTemp = var.IRme();
        Ir.getInstance().AddIrCommand(new IrCommandCheckNullPtr(arrTemp));
        Temp idxTemp = subscript.IRme();
        Ir.getInstance().AddIrCommand(new IrCommandCheckBounds(arrTemp, idxTemp));
        Temp dst = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandArrayGet(dst, arrTemp, idxTemp));
        return dst;
    }

    public Temp IRmeForAssign(Temp src) {
        Temp arrTemp = var.IRme();
        Ir.getInstance().AddIrCommand(new IrCommandCheckNullPtr(arrTemp));
        Temp idxTemp = subscript.IRme();
        Ir.getInstance().AddIrCommand(new IrCommandCheckBounds(arrTemp, idxTemp));
        Ir.getInstance().AddIrCommand(new IrCommandArraySet(arrTemp, idxTemp, src));
        return null;
    }
}
