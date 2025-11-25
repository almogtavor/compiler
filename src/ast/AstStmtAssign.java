package ast;

public class AstStmtAssign extends AstStmt {

    public AstVar var;
    public AstExp exp;

    public AstStmtAssign(AstVar var, AstExp exp) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.var = var;
        this.exp = exp;

        System.out.println("====================== stmt -> var ASSIGN exp");
    }

    @Override
    public void printMe() {
        System.out.println("AST STMT ASSIGN");

        if (var != null)
            var.printMe();
        if (exp != null)
            exp.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "ASSIGN"
        );

        if (var != null)
            AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);

        if (exp != null)
            AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }
}
