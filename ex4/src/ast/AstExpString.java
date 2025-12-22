package ast;

import types.*;
import Exceptions.SemanticException;
import temp.*;
import ir.*;

public class AstExpString extends AstExp {

    public String value;

    public AstExpString(String value, int lineNumber) {
        super(lineNumber);
        this.value = value;
    }

    @Override
    public void printMe() {
        AstGraphviz.getInstance().logNode(serialNumber,String.format("STRING(%s)", value));
    }

    @Override
    public Type SemantMe() {
        return TypeString.getInstance();
    }

    @Override
    public Temp IRme() {
        Temp t = TempFactory.getInstance().getFreshTemp();
        return t;
    }
}
