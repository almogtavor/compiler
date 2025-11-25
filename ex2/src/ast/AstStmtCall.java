package ast;

public class AstStmtCall extends AstStmt {

    public AstCallExp call;

    public AstStmtCall(AstCallExp call) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.call = call;

        System.out.println("====================== stmt -> callExp SEMICOLON");
    }

    @Override
    public void printMe() {
        System.out.println("AST STMT CALL");

        if (call != null) call.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "STMT_CALL"
        );

        if (call != null)
            AstGraphviz.getInstance().logEdge(serialNumber, call.serialNumber);
    }
}
