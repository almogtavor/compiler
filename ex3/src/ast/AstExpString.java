package ast;

import types.*;
import Exceptions.SemanticException;
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
        System.out.println("SemantMe: " + this.getClass().getSimpleName());

        return TypeString.getInstance();
    }
}
