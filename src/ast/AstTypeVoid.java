package ast;

public class AstTypeVoid extends AstType {

    public AstTypeVoid() {
        serialNumber = AstNodeSerialNumber.getFresh();
        System.out.println("====================== type -> TYPE_VOID");
    }

    @Override
    public void printMe() {
        System.out.println("AST TYPE: VOID");

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "TYPE_VOID"
        );
    }
}
