package ast;

public class AstVarField extends AstVar {

    public AstVar var;
    public String fieldName;

    public AstVarField(AstVar var, String fieldName) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.var = var;
        this.fieldName = fieldName;

        System.out.format("====================== var -> var DOT ID(%s)\n", fieldName);
    }

    @Override
    public void printMe() {
        System.out.println("AST VAR FIELD: ." + fieldName);

        if (var != null)
            var.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("FIELD(%s)", fieldName)
        );

        if (var != null)
            AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
    }
}
