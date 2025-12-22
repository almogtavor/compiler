package ast;

import types.*;
import Exceptions.SemanticException;

public class AstExpList extends AstNode {

    public AstExp head;
    public AstExpList tail;

    public AstExpList(AstExp head, AstExpList tail, int lineNumber) {
        super(lineNumber);
        this.head = head;
        this.tail = tail;

    }

    @Override
    public void printMe() {

        if (head != null) head.printMe();
        if (tail != null) tail.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "EXPLIST");

        if (head != null)
            AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
        if (tail != null)
            AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
    }

    public TypeList SemantMe() {
        Type headType = null;

        if (head != null)
            headType = head.SemantMe();

        TypeList tailList = null;

        if (tail != null)
            tailList = tail.SemantMe();

        return new TypeList(headType, tailList);
    }
}
