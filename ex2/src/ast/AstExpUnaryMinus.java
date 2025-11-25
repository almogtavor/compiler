package ast;

public class AstExpUnaryMinus extends AstExp {
    public AstExp exp;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AstExpUnaryMinus(AstExp exp) {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        serialNumber = AstNodeSerialNumber.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.println("====================== exp -> MINUS exp");

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.exp = exp;
    }

    /***************/
    /* PRINT ME    */
    /***************/
    public void printMe() {
        System.out.print("AST NODE UNARY MINUS EXP\n");

        if (exp != null)
            exp.printMe();

        AstGraphviz.getInstance().logNode(
            serialNumber,
            "UNARY_MINUS"
        );

        if (exp != null)
            AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }
}
