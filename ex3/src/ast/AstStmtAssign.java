package ast;

import types.*;
import Exceptions.SemanticException;

public class AstStmtAssign extends AstStmt {

    public AstVar var;
    public AstExp exp;

    public AstStmtAssign(AstVar var, AstExp exp, int lineNumber) {
        super(lineNumber);
        this.var = var;
        this.exp = exp;
    }

    @Override
    public void printMe() {
        if (var != null)
            var.printMe();
        if (exp != null)
            exp.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "ASSIGN");

        if (var != null)
            AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
        if (exp != null)
            AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }

    @Override
    public void SemantMe() {
System.out.println("SemantMe: " + this.getClass().getSimpleName());

        Type leftType = var.SemantMe();
        Type rightType = exp.SemantMe();

        if (!leftType.isAssignableFrom(rightType))
            throw new SemanticException(lineNumber);
    }

}
