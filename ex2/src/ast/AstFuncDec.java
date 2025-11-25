package ast;

public class AstFuncDec extends AstDec {

    public AstType retType;
    public String name;
    public AstArgList args;      // יכול להיות null
    public AstStmtList body;

    public AstFuncDec(AstType retType, String name, AstArgList args, AstStmtList body) {
        this.retType = retType;
        this.name = name;
        this.args = args;
        this.body = body;

        System.out.println("====================== funcDec -> type ID (args) { stmtList }");
    }

    @Override
    public void printMe() {
        System.out.println("AST FUNC DEC: " + name);

        // Recursive printing
        if (retType != null) retType.printMe();
        if (args != null) args.printMe();
        if (body != null) body.printMe();

        // Graphviz node
        AstGraphviz.getInstance().logNode(
                serialNumber,
                "FUNC(" + name + ")"
        );

        // Edges
        if (retType != null)
            AstGraphviz.getInstance().logEdge(serialNumber, retType.serialNumber);

        if (args != null)
            AstGraphviz.getInstance().logEdge(serialNumber, args.serialNumber);

        if (body != null)
            AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
    }
}
