package ast;

public class AstVarSubscript extends AstVar {

    public AstVar var;
    public AstExp subscript;

    public AstVarSubscript(AstVar var, AstExp subscript) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.var = var;
        this.subscript = subscript;

        System.out.print("====================== var -> var [ exp ]\n");
    }

    @Override
    public void printMe() {
        System.out.println("AST VAR SUBSCRIPT");

        if (var != null)
            var.printMe();

        if (subscript != null)
            subscript.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "SUBSCRIPT"
        );

        if (var != null)
            AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);

        if (subscript != null)
            AstGraphviz.getInstance().logEdge(serialNumber, subscript.serialNumber);
    }
}
