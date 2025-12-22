package ast;

import types.Type;
import temp.*;

public class AstStmtVarDec extends AstStmt {

    public AstVarDec varDec;

    public AstStmtVarDec(AstVarDec varDec, int lineNumber) {
        super(lineNumber);
        this.varDec = varDec;
    }

    @Override
    public void printMe() {
        if (varDec != null)
            varDec.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "STMT_VAR_DEC");

        if (varDec != null)
            AstGraphviz.getInstance().logEdge(serialNumber, varDec.serialNumber);
    }

    @Override
    public void SemantMe() {
        if (varDec != null)
            varDec.SemantMe();
    }

    @Override
    public Temp IRme() {
        if (varDec != null) varDec.IRme();
        return null;
    }
}
