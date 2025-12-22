package ast;

import temp.*;

public class AstDecList extends AstNode {

    public AstDec head;
    public AstDecList tail;

    public AstDecList(AstDecList tail, AstDec head) {
        super(head != null ? head.getLine() : 0);
        this.head = head;
        this.tail = tail;
    }

    public void SemantMe() {
        if (head != null) head.SemantMe();
        if (tail != null) tail.SemantMe();
    }

    @Override
    public Temp IRme() {
        if (head != null) head.IRme();
        if (tail != null) tail.IRme();
        return null;
    }
}
