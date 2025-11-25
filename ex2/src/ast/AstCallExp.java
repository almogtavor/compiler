package ast;

public class AstCallExp extends AstExp {

    public AstVar object;   // null אם פונקציה גלובלית
    public String name;
    public AstExpList args; // יכול להיות null

    public AstCallExp(AstVar object, String name, AstExpList args) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.object = object;
        this.name = name;
        this.args = args;

        System.out.print("====================== callExp -> [var .] ID(args)\n");
    }

    @Override
    public void printMe() {
        System.out.println("AST CALL EXP: " + name);

        if (object != null)
            object.printMe();

        if (args != null)
            args.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("CALL(%s)", name)
        );

        if (object != null)
            AstGraphviz.getInstance().logEdge(serialNumber, object.serialNumber);

        if (args != null)
            AstGraphviz.getInstance().logEdge(serialNumber, args.serialNumber);
    }
}
