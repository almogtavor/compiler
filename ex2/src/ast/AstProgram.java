package ast;

public class AstProgram extends AstNode {
    public AstDecList decList;

    public AstProgram(AstDecList decList) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.decList = decList;

        System.out.println("====================== program -> dec dec_list");
    }

    @Override
    public void printMe() {
        System.out.println("AST PROGRAM");

        // Recursive print
        if (decList != null)
            decList.printMe();

        // Graphviz node
        AstGraphviz.getInstance().logNode(
                serialNumber,
                "PROGRAM"
        );

        // Edge to decList
        if (decList != null)
            AstGraphviz.getInstance().logEdge(serialNumber, decList.serialNumber);
    }
}
