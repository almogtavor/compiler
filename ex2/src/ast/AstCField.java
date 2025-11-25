package ast;

public class AstCField extends AstNode {
    public AstDec dec;  // יכול להיות VarDec או FuncDec

    public AstCField(AstDec dec) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.dec = dec;

        System.out.print("====================== cField -> varDec | funcDec\n");
    }

    public void printMe() {
        System.out.print("AST C-FIELD\n");

        if (dec != null)
            dec.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "C-FIELD");

        if (dec != null)
            AstGraphviz.getInstance().logEdge(serialNumber, dec.serialNumber);
    }
}
