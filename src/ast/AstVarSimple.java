package ast;

public class AstVarSimple extends AstVar {

    public String name;

    public AstVarSimple(String name) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;

        System.out.format("====================== var -> ID(%s)\n", name);
    }

    @Override
    public void printMe() {
        System.out.println("AST VAR SIMPLE: " + name);

        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("VAR_SIMPLE(%s)", name)
        );
    }
}
