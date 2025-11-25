package ast;

public class AstTypeInt extends AstType {

    public AstTypeInt() {
        serialNumber = AstNodeSerialNumber.getFresh();
        System.out.println("====================== type -> TYPE_INT");
    }

    @Override
    public void printMe() {
        System.out.println("AST TYPE: INT");

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "TYPE_INT"
        );
    }
}
