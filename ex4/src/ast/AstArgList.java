package ast;
import symboltable.SymbolTable;
import Exceptions.SemanticException;
import types.*;
import temp.*;

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
        if (SymbolTable.getInstance().findInCurrentScope(head.name) != null)
            throw new SemanticException(head.getLine());

        SymbolTable.getInstance().enter(head.name, head.SemantMe());

        if (tail != null)
            tail.enterParamsToScope();
    }

    public TypeList buildTypeList() {
        Type headType = head.type.SemantMe();
        TypeList tailTypes = (tail != null ? tail.buildTypeList() : null);
        return new TypeList(headType, tailTypes);
    }

    public Temp IRme() {
        if (head != null) head.IRme();
        if (tail != null) tail.IRme();
        return null;
    }

}
