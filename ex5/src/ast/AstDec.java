package ast;

public abstract class AstDec extends AstNode {

    public AstDec(int lineNumber) {
        super(lineNumber);
    }

    @Override
    public void printMe() {
    }

    public abstract types.Type SemantMe();
}
