package ast;

public class AstCFieldList extends AstNode {
    public AstCFieldList head;
    public AstCField tail;

    public AstCFieldList(AstCFieldList head, AstCField tail) {
        serialNumber = AstNodeSerialNumber.getFresh();

        this.head = head;
        this.tail = tail;

        System.out.print("====================== lcField -> lcField cField | cField\n");
    }

    @Override
    public void printMe() {
        System.out.print("AST CLASS FIELD LIST\n");

        if (head != null) head.printMe();
        if (tail != null) tail.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "C-FIELD\nLIST");

        if (head != null)
            AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
        if (tail != null)
            AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
    }
}
