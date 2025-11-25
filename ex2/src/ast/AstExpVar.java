package ast;

public class AstExpVar extends AstExp {

    public AstVar var;

    public AstExpVar(AstVar var) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.var = var;

        System.out.print("====================== exp -> var\n");
    }

    @Override
    public void printMe() {
        System.out.println("AST EXP VAR");

        if (var != null)
            var.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "EXP_VAR"
        );

        if (var != null)
            AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
    }
}
