package ast;

import Exceptions.SemanticException;
import types.Type;

public class AstProgram extends AstNode {

    public AstDecList decList;

    public AstProgram(AstDecList decList) {
        super(decList.getLine());
        this.decList = decList;
    }

    @Override
    public void printMe() {

        if (decList != null)
            decList.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "PROGRAM");

        if (decList != null)
            AstGraphviz.getInstance().logEdge(serialNumber, decList.serialNumber);
    }

    public Type SemantMe() {
System.out.println("SemantMe: " + this.getClass().getSimpleName());

        if (decList != null)
            decList.SemantMe();

        return null;
    }
}
