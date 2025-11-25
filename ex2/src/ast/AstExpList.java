package ast;

public class AstExpList extends AstNode {

    public AstExpList tail; // rest of args
    public AstExp head;     // current argument

    public AstExpList(AstExpList tail, AstExp head) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.tail = tail;
        this.head = head;

        System.out.print("====================== expList -> exp (, exp)*\n");
    }

    @Override
    public void printMe() {
        System.out.println("AST EXP LIST");

        if (head != null)
            head.printMe();
        if (tail != null)
            tail.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                "EXPLIST"
        );

        if (head != null)
            AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
        if (tail != null)
            AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
    }
}
