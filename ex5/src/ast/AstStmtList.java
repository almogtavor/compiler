package ast;

import temp.*;

public class AstStmtList extends AstNode {

    public AstStmt head;
    public AstStmtList tail;

    public AstStmtList(AstStmt head, AstStmtList tail) {
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
