package ast;

import types.Type;

public abstract class AstExp extends AstNode {

    public AstExp(int lineNumber) {
        super(lineNumber);
    }

    @Override
    public void printMe() {
    }

    public abstract Type SemantMe();
}
