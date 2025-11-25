package ast;

public class AstStmtReturn extends AstStmt {

    public AstExp exp; // יכול להיות null

    public AstStmtReturn(AstExp exp) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.exp = exp;

        System.out.println("====================== stmt -> RETURN (exp)? SEMICOLON");
    }

    @Override
    public void printMe() {
        System.out.println("AST STMT RETURN");

        if (exp != null) exp.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "RETURN"
        );

        if (exp != null)
            AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }
}
