package ast;

import types.Type;

public abstract class AstVar extends AstNode {

    public AstVar(int lineNumber) {
        super(lineNumber);
    }

    @Override
    public void printMe() {
    }

    public abstract Type SemantMe();
}
