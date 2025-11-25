package ast;

public class AstArgList extends AstNode {
    public AstFormal head;
    public AstArgList tail;

    public AstArgList(AstFormal head, AstArgList tail) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.head = head;
        this.tail = tail;

        System.out.print("====================== argList -> formal argList | formal\n");
    }

    @Override
    public void printMe() {
        System.out.print("AST ARG LIST\n");

        if (head != null) head.printMe();
        if (tail != null) tail.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "ARG LIST");

        if (head != null) AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
        if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
    }
}
