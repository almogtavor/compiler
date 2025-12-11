package ast;
import symboltable.SymbolTable;
import types.TypeForScopeBoundaries;

public class AstStmtList extends AstNode {

    public AstStmt head;
    public AstStmtList tail;

    public AstStmtList(AstStmt head, AstStmtList tail) {
        super(head.getLine());
        this.head = head;
        this.tail = tail;
    }

    @Override
    public void printMe() {
        if (head != null)
            head.printMe();

        if (tail != null)
            tail.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "STMT_LIST");

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
