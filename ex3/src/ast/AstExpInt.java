package ast;

import types.*;
import Exceptions.SemanticException;

public class AstExpInt extends AstExp {

    public int value;

    public AstExpInt(int value, int lineNumber) {
        super(lineNumber);
        this.value = value;
    }

    @Override
    public void printMe() {
        AstGraphviz.getInstance().logNode(serialNumber,String.format("INT(%d)", value));
    }

    @Override
    public Type SemantMe() {
        return TypeInt.getInstance();
    }
}
