package ast;

public class AstExpNil extends AstExp {

    public AstExpNil() {
        serialNumber = AstNodeSerialNumber.getFresh();
        System.out.println("====================== exp -> NIL");
    }

    @Override
    public void printMe() {
        System.out.println("AST EXP NIL");

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "NIL"
        );
    }
}
