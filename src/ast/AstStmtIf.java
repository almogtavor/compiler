package ast;

public class AstStmtIf extends AstStmt {

    public AstExp cond;
    public AstStmtList body;

    public AstStmtIf(AstExp cond, AstStmtList body) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.cond = cond;
        this.body = body;

        System.out.println("====================== stmt -> IF (exp) { stmtList }");
    }

    @Override
    public void printMe() {
        System.out.println("AST STMT IF");

        if (cond != null) cond.printMe();
        if (body != null) body.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "IF"
        );

        if (cond != null)
            AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);

        if (body != null)
            AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
    }
}
