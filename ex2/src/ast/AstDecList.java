package ast;

public class AstDecList extends AstNode {

    public AstDecList tail;
    public AstDec head;

    public AstDecList(AstDecList tail, AstDec head) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.tail = tail;
        this.head = head;

        System.out.println("====================== dec_list -> dec dec_list | empty");
    }

    @Override
    public void printMe() {
        System.out.println("AST DEC LIST");

        // Print head
        if (head != null)
            head.printMe();

        // Print tail
        if (tail != null)
            tail.printMe();

        // Graphviz node
        AstGraphviz.getInstance().logNode(
                serialNumber,
                "DEC LIST"
        );

        // Edge to head
        if (head != null)
            AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);

        // Edge to tail
        if (tail != null)
            AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
    }
}
