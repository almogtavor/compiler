package ast;

public class AstClassDec extends AstDec {
    public String name;
    public String parentName;  // יכול להיות null
    public AstCFieldList fields;

    public AstClassDec(String name, String parentName, AstCFieldList fields) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;
        this.parentName = parentName;
        this.fields = fields;

        System.out.print("====================== classDec -> CLASS ID (EXTENDS ID)? { cFieldList }\n");
    }

    @Override
    public void printMe() {
        System.out.print("AST CLASS DEC\n");

        if (fields != null)
            fields.printMe();

        String label;
        if (parentName != null)
            label = String.format("CLASS(%s EXTENDS %s)", name, parentName);
        else
            label = String.format("CLASS(%s)", name);

        AstGraphviz.getInstance().logNode(serialNumber, label);

        if (fields != null)
            AstGraphviz.getInstance().logEdge(serialNumber, fields.serialNumber);
    }
}
