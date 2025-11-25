package ast;

public class AstVarDec extends AstDec {

    public AstType type;
    public String name;
    public AstExp init;   // יכול להיות null (או AstNew)

    public AstVarDec(AstType type, String name, AstExp init) {
        serialNumber = AstNodeSerialNumber.getFresh();

        this.type = type;
        this.name = name;
        this.init = init;

        System.out.println("====================== varDec -> type ID (ASSIGN exp)? SEMICOLON");
    }

    @Override
    public void printMe() {
        System.out.println("AST VAR DEC: " + name);

        // Recursive printing
        if (type != null)
            type.printMe();

        if (init != null)
            init.printMe();

        // Graphviz node
        AstGraphviz.getInstance().logNode(
                serialNumber,
                "VARDEC(" + name + ")"
        );

        // Edges
        if (type != null)
            AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);

        if (init != null)
            AstGraphviz.getInstance().logEdge(serialNumber, init.serialNumber);
    }
}
