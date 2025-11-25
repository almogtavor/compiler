package ast;

public class AstFormal extends AstNode {
    public AstType type;
    public String name;

    public AstFormal(AstType type, String name) {
        serialNumber = AstNodeSerialNumber.getFresh();

        this.type = type;
        this.name = name;

        System.out.format("====================== arg -> type ID( %s )\n", name);
    }

    @Override
    public void printMe() {
        System.out.format("AST FORMAL ARG (%s)\n", name);

        if (type != null) type.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("FORMAL\n(%s)", name));

        if (type != null)
            AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
    }
}
