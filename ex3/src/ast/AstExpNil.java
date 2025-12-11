package ast;

import types.*;
import Exceptions.SemanticException;

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
}
