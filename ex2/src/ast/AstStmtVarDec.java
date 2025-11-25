package ast;

public class AstStmtVarDec extends AstStmt {

    public AstVarDec varDec;

    public AstStmtVarDec(AstVarDec varDec) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.varDec = varDec;

        System.out.println("====================== stmt -> varDec");
    }

    @Override
    public void printMe() {
        System.out.println("AST STMT VARDEC");

        if (varDec != null)
            varDec.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "STMT_VARDEC"
        );

        if (varDec != null)
            AstGraphviz.getInstance().logEdge(serialNumber, varDec.serialNumber);
    }
}
