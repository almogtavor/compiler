package ast;

import types.*;

public class AstTypeVoid extends AstType {

    public AstTypeVoid(int lineNumber) {
        super(lineNumber);
    }
    
    @Override
    public void printMe() {
        AstGraphviz.getInstance().logNode(serialNumber, "TYPE_VOID");
    }

    @Override
    public Type SemantMe() {
        return TypeVoid.getInstance();
    }
}
