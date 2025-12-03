package ast;
import symboltable.SymbolTable;
import types.TypeForScopeBoundaries;

import types.*;
import Exceptions.SemanticException;

public class AstStmtIf extends AstStmt {

    public AstExp cond;
    public AstStmtList body;

    public AstStmtIf(AstExp cond, AstStmtList body, int lineNumber) {
        super(lineNumber);
        this.cond = cond;
        this.body = body;
    }

    @Override
    public void printMe() {
        if (cond != null) cond.printMe();
        if (body != null) body.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "IF");

        if (cond != null)
            AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);

        if (body != null)
            AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
    }

    @Override
    public void SemantMe() {
System.out.println("SemantMe: " + this.getClass().getSimpleName());

        Type condType = cond.SemantMe();

        if (condType != TypeInt.getInstance())
            throw new SemanticException(lineNumber);

        SymbolTable.getInstance().beginScope();

        if (body != null)
            body.SemantMe();

        SymbolTable.getInstance().endScope();
    }

}
