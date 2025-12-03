package ast;
import symboltable.SymbolTable;
import Exceptions.SemanticException;
import types.*;

public class AstArgList extends AstNode {

    public AstFormal head;
    public AstArgList tail;

    public AstArgList(AstFormal head, AstArgList tail) {
        super(head.getLine());
        this.head = head;
        this.tail = tail;
    }

    @Override
    public void printMe() {
        if (head != null) head.printMe();
        if (tail != null) tail.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "ARG LIST");
        if (head != null) AstGraphviz.getInstance().logEdge(serialNumber, head.serialNumber);
        if (tail != null) AstGraphviz.getInstance().logEdge(serialNumber, tail.serialNumber);
    }

    public TypeList SemantMe() {
        Type headType = head.SemantMe();

        if (headType == TypeVoid.getInstance())
            throw new SemanticException(head.getLine());

        TypeList tailList = null;
        if (tail != null)
            tailList = tail.SemantMe();

        return new TypeList(headType, tailList);
    }

    public void enterParamsToScope() {
        System.out.println("SemantMe: " + this.getClass().getSimpleName());

        // 1. הכנס את הפרמטר הנוכחי
        if (SymbolTable.getInstance().findInCurrentScope(head.name) != null)
            throw new SemanticException(head.getLine());

        SymbolTable.getInstance().enter(head.name, head.SemantMe());

        // 2. עבור לרשימה הבאה
        if (tail != null)
            tail.enterParamsToScope();
    }

    public TypeList buildTypeList() {
        Type headType = head.type.SemantMe();
        TypeList tailTypes = (tail != null ? tail.buildTypeList() : null);
        return new TypeList(headType, tailTypes);
    }

}
