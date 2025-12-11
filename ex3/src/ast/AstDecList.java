package ast;

import Exceptions.SemanticException;
import types.Type;

public class AstDecList extends AstNode {

    public AstDecList tail;
    public AstDec head;

    public AstDecList(AstDecList tail, AstDec head) {
        super(head.getLine());
        this.tail = tail;
        this.head = head;
    }

    @Override
    public void printMe() {

        if (head != null) head.printMe();
        if (tail != null) tail.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "DEC LIST");

        if (head != null)
            AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);

        if (tail != null)
            AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
    }

    public void SemantMe() {

        if (head != null)
            head.SemantMe();

        if (tail != null)
            tail.SemantMe();
    }
}
