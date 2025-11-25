package ast;

public class AstExpString extends AstExp {

    public String value;

    public AstExpString(String value) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.value = value;

        System.out.printf("====================== exp -> STRING(%s)\n", value);
    }

    @Override
    public void printMe() {
        System.out.println("AST EXP STRING: " + value);

        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("STRING(%s)", value)
        );
    }
}
