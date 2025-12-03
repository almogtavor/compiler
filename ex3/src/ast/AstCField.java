package ast;

import Exceptions.SemanticException;
import types.*;

public class AstCField extends AstNode {
    public AstDec dec;

    public AstCField(AstDec dec) {
        super(dec.getLine());
        this.dec = dec;
    }

    @Override
    public void printMe() {
        if (dec != null)
            dec.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "C-FIELD");

        if (dec != null)
            AstGraphviz.getInstance().logEdge(serialNumber, dec.serialNumber);
    }

    public Type SemantMe() {
        System.out.println("SemantMe: " + this.getClass().getSimpleName());

        dec.SemantMe();
        return null;
    }
}
