package ast;

public class AstExpBinop extends AstExp {

    public static final int OP_PLUS   = 1;
    public static final int OP_MINUS  = 2;
    public static final int OP_TIMES  = 3;
    public static final int OP_DIVIDE = 4;
    public static final int OP_LT     = 5;
    public static final int OP_GT     = 6;
    public static final int OP_EQ     = 7;

    public AstExp left;
    public AstExp right;
    public int op;

    public AstExpBinop(AstExp left, AstExp right, int op) {
        serialNumber = AstNodeSerialNumber.getFresh();

        this.left = left;
        this.right = right;
        this.op = op;

        System.out.print("====================== exp -> exp BINOP exp\n");
    }

    private String opToString() {
        return switch (op) {
            case OP_PLUS   -> "+";
            case OP_MINUS  -> "-";
            case OP_TIMES  -> "*";
            case OP_DIVIDE -> "/";
            case OP_LT     -> "<";
            case OP_GT     -> ">";
            case OP_EQ     -> "=";
            default        -> "?";
        };
    }

    @Override
    public void printMe() {
        System.out.println("AST EXP BINOP: " + opToString());

        if (left != null)
            left.printMe();
        if (right != null)
            right.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("BINOP(%s)", opToString())
        );

        if (left != null)
            AstGraphviz.getInstance().logEdge(serialNumber, left.serialNumber);
        if (right != null)
            AstGraphviz.getInstance().logEdge(serialNumber, right.serialNumber);
    }
}
