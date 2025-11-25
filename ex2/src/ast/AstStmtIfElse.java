package ast;

public class AstStmtIfElse extends AstStmt {

    public AstExp cond;
    public AstStmtList thenBody;
    public AstStmtList elseBody;

    public AstStmtIfElse(AstExp cond, AstStmtList tb, AstStmtList eb) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.cond = cond;
        this.thenBody = tb;
        this.elseBody = eb;

        System.out.println("====================== stmt -> IF ... ELSE ...");
    }

    @Override
    public void printMe() {
        System.out.println("AST STMT IF-ELSE");

        if (cond != null) cond.printMe();
        if (thenBody != null) thenBody.printMe();
        if (elseBody != null) elseBody.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "IF_ELSE"
        );

        if (cond != null)
            AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);

        if (thenBody != null)
            AstGraphviz.getInstance().logEdge(serialNumber, thenBody.serialNumber);

        if (elseBody != null)
            AstGraphviz.getInstance().logEdge(serialNumber, elseBody.serialNumber);
    }
}
