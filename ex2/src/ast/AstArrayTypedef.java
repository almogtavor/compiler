package ast;

public class AstArrayTypedef extends AstDec {
    public AstType type;
    public String name;

    public AstArrayTypedef(AstType type, String name) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.type = type;
        this.name = name;

        System.out.print("====================== arrayTypedef -> ARRAY ID EQ type LBRACK RBRACK SEMICOLON \n");
    }

    public void printMe() {
        System.out.print("AST ARRAY TYPEDEF\n");

        if (type != null) type.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("ARRAY_TYPEDEF(%s)", name)
        );

        if (type != null)
            AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
    }
}

