package ast;

import types.Type;
import types.TypeString;

public class AstTypeString extends AstType {

    public AstTypeString(int lineNumber) {
        super(lineNumber);
    }
    
    @Override
    public void printMe() {
        AstGraphviz.getInstance().logNode(serialNumber,"TYPE_STRING");
    }

    @Override
    public Type SemantMe() {
        System.out.println("SemantMe: " + this.getClass().getSimpleName());

        return TypeString.getInstance();
    }
}
