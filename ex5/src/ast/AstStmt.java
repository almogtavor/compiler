package ast;

import types.Type;

public abstract class AstStmt extends AstNode {

    public AstStmt(int lineNumber) {
        super(lineNumber);
    }

    @Override
    public void printMe() {
    }

    public abstract void SemantMe();
}
