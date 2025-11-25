package ast;

public class AstTypeString extends AstType {

    public AstTypeString() {
        serialNumber = AstNodeSerialNumber.getFresh();
        System.out.println("====================== type -> TYPE_STRING");
    }

    @Override
    public void printMe() {
        System.out.println("AST TYPE: STRING");

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "TYPE_STRING"
        );
    }
}
