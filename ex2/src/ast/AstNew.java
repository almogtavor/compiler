package ast;

public class AstNew extends AstExp {

    public AstType type;
    public AstExp size; // null אם זה NEW ClassName

    public AstNew(AstType type, AstExp size) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.type = type;
        this.size = size;

        System.out.print("====================== newExp -> NEW type [exp]\n");
    }

    @Override
    public void printMe() {
        System.out.println("AST NEW EXP");

        if (type != null)
            type.printMe();
        if (size != null)
            size.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                (size == null)
                    ? "NEW"
                    : "NEW_ARRAY"
        );

        if (type != null)
            AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        if (size != null)
            AstGraphviz.getInstance().logEdge(serialNumber, size.serialNumber);
    }
}
