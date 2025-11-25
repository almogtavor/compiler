package ast;

public class AstStmtList extends AstNode {

    public AstStmt head;
    public AstStmtList tail;

    public AstStmtList(AstStmt head, AstStmtList tail) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.head = head;
        this.tail = tail;

        System.out.println("====================== stmtList -> stmt stmtList | stmt");
    }

    @Override
    public void printMe() {
        System.out.println("AST STMT LIST");

        if (head != null)
            head.printMe();

        if (tail != null)
            tail.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "STMT_LIST"
        );

        if (head != null)
            AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);

        if (tail != null)
            AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
    }
}
