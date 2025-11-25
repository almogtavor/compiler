package ast;

public class AstTypeClass extends AstType {

    public String name;

    public AstTypeClass(String name) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;

        System.out.format("====================== type -> ID(%s)\n", name);
    }

    @Override
    public void printMe() {
        System.out.println("AST TYPE: CLASS(" + name + ")");

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "TYPE_CLASS(" + name + ")"
        );
    }
}
