package ast;
import symboltable.SymbolTable;
import types.TypeForScopeBoundaries;

import types.*;
import Exceptions.SemanticException;

public class AstStmtIfElse extends AstStmt {

    public AstExp cond;
    public AstStmtList thenBody;
    public AstStmtList elseBody;

    public AstStmtIfElse(AstExp cond, AstStmtList tb, AstStmtList eb,int lineNumber) {
        super(lineNumber);
        this.cond = cond;
        this.thenBody = tb;
        this.elseBody = eb;
    }

    @Override
    public void printMe() {
        if (cond != null) cond.printMe();
        if (thenBody != null) thenBody.printMe();
        if (elseBody != null) elseBody.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "IF_ELSE");

        if (cond != null)
            AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);
        if (thenBody != null)
            AstGraphviz.getInstance().logEdge(serialNumber, thenBody.serialNumber);
        if (elseBody != null)
            AstGraphviz.getInstance().logEdge(serialNumber, elseBody.serialNumber);
    }

    @Override
    public void SemantMe() {
        Type condType = cond.SemantMe();

        if (condType != TypeInt.getInstance())
            throw new SemanticException(lineNumber);

        SymbolTable.getInstance().beginScope();
        if (thenBody != null)
            thenBody.SemantMe();
        SymbolTable.getInstance().endScope();

        SymbolTable.getInstance().beginScope();
        if (elseBody != null)
            elseBody.SemantMe();
        SymbolTable.getInstance().endScope();
    }

}
