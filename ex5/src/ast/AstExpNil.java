package ast;

import types.*;
import Exceptions.SemanticException;
import temp.*;
import ir.*;

public class AstExpNil extends AstExp {

    public AstExpNil(int lineNumber) {
        super(lineNumber);
    }

    @Override
    public void printMe() {
        AstGraphviz.getInstance().logNode(serialNumber,"NIL");
    }

    @Override
    public Type SemantMe() {
        return TypeNil.getInstance();
    }

    @Override
    public Temp IRme() {
        Temp t = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandConstInt(t, 0));
        return t;
    }
}
