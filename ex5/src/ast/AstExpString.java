package ast;

import types.*;
import temp.*;
import ir.*;
import codegen.CodeGenInfo;

public class AstExpString extends AstExp {
    public String value;

    public AstExpString(String value, int lineNumber) {
        super(lineNumber);
        if (value != null && value.length() >= 2 &&
            value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
            this.value = value.substring(1, value.length() - 1);
        } else {
            this.value = value;
        }
    }

    @Override
    public void printMe() {
        AstGraphviz.getInstance().logNode(serialNumber, String.format("STRING(%s)", value));
    }

    @Override
    public Type SemantMe() { return TypeString.getInstance(); }

    @Override
    public Temp IRme() {
        String label = CodeGenInfo.getInstance().addStringLiteral(value);
        Temp t = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandConstString(t, label));
        return t;
    }
}
