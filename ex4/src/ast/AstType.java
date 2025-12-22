package ast;

import types.Type;

public abstract class AstType extends AstNode {

    public AstType(int lineNumber) {
        super(lineNumber);
    }

    @Override
    public void printMe(){}

    public abstract Type SemantMe();
}
