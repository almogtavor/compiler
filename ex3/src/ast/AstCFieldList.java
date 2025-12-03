package ast;

import types.*;

public class AstCFieldList extends AstNode {
    public AstCField head;
    public AstCFieldList tail;

    public AstCFieldList(AstCField head, AstCFieldList tail) {
        super(head.getLine());
        this.head = head;
        this.tail = tail;
    }

    @Override
    public void printMe() {
        if (head != null) head.printMe();
        if (tail != null) tail.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "C-FIELD LIST");

        if (head != null)
            AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);

        if (tail != null)
            AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
    }

    public Type SemantMe() {
        System.out.println("SemantMe: " + this.getClass().getSimpleName());

        if (head != null)
            head.SemantMe();

        if (tail != null)
            tail.SemantMe();

        return null;
    }
}
