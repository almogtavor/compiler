package ast;

import types.Type;

public class AstStmtVarDec extends AstStmt {

    public AstVarDec varDec;

    public AstStmtVarDec(AstVarDec varDec,int lineNumber) {
        super(lineNumber);
        this.varDec = varDec;
    }

    @Override
    public void printMe() {
        if (varDec != null)
            varDec.printMe();

        AstGraphviz.getInstance().logNode(serialNumber,"STMT_VARDEC");

        if (varDec != null)
            AstGraphviz.getInstance().logEdge(serialNumber, varDec.serialNumber);
    }

    @Override
    public void SemantMe() {
        if (varDec != null)
            varDec.SemantMe();

    }
}
