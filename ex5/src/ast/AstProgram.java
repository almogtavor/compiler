package ast;

import Exceptions.SemanticException;
import types.Type;
import temp.*;

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
        if (decList != null)
            decList.SemantMe();

        return null;
    }

    @Override
    public Temp IRme() {
        if (decList != null)
            decList.IRme();
        return null;
    }
}
