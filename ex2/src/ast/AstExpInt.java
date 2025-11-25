package ast;

public class AstExpInt extends AstExp {

    public int value;

    public AstExpInt(int value) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.value = value;

        System.out.printf("====================== exp -> INT(%d)\n", value);
    }

    @Override
    public void printMe() {
        System.out.println("AST EXP INT: " + value);

        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("INT(%d)", value)
        );
    }
}
