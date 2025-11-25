package ast;

public class AstStmtWhile extends AstStmt {

    public AstExp cond;
    public AstStmtList body;

    public AstStmtWhile(AstExp cond, AstStmtList body) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.cond = cond;
        this.body = body;

        System.out.println("====================== stmt -> WHILE (exp) { stmtList }");
    }

    @Override
    public void printMe() {
        System.out.println("AST STMT WHILE");

        if (cond != null) cond.printMe();
        if (body != null) body.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "WHILE"
        );

        if (cond != null)
            AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);

        if (body != null)
            AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
    }
}
