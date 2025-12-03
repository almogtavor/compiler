package ast;

import types.Type;
import types.TypeInt;

public class AstTypeInt extends AstType {
    
    public AstTypeInt(int lineNumber) {
        super(lineNumber);
    }
    
    @Override
    public void printMe() {
        AstGraphviz.getInstance().logNode(serialNumber,"TYPE_INT");
    }

    @Override
    public Type SemantMe() {
        System.out.println("SemantMe: " + this.getClass().getSimpleName());

        return TypeInt.getInstance();
    }
}
