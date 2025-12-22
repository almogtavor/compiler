package ast;

import types.*;
import Exceptions.SemanticException;
import temp.*;

public class AstExpVar extends AstExp {

    public AstVar var;

    public AstExpVar(AstVar var, int lineNumber) {
        super(lineNumber);
        this.var = var;
    }

    @Override
    public void printMe() {

        if (var != null)
            var.printMe();

        AstGraphviz.getInstance().logNode(serialNumber,"EXP_VAR");

        if (var != null)
            AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
    }

    @Override
    public Type SemantMe() {

        Type t = var.SemantMe();

        if (t instanceof TypeFunction)
            throw new SemanticException(lineNumber);


        return t;
    }

    @Override
    public Temp IRme() {
        return var.IRme();
    }
}
