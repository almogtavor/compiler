package ast;

import types.*;
import temp.*;
import ir.*;

public class AstExpInt extends AstExp {

    public int value;

    public AstExpInt(int value, int lineNumber) {
        super(lineNumber);
        this.value = value;
    }

    @Override
    public void printMe() {
        AstGraphviz.getInstance().logNode(serialNumber, "INT(" + value + ")");
    }

    @Override
    public Type SemantMe() {
        return TypeInt.getInstance();
    }

    @Override
    public Temp IRme() {
        Temp t = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandConstInt(t, value));
        return t;
    }
}
