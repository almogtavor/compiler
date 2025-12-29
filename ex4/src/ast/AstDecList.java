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
        for (AstDecList it = this; it != null; it = it.tail) {
            if (it.head instanceof AstVarDec) {
                ((AstVarDec) it.head).SemantMe();
            }
        }
        
        for (AstDecList it = this; it != null; it = it.tail) {
            if (!(it.head instanceof AstVarDec)) {
                it.head.SemantMe();
            }
        }
    }

    @Override
    public Temp IRme() {
        for (AstDecList it = this; it != null; it = it.tail) {
        if (it.head instanceof AstVarDec) {
            ((AstVarDec) it.head).irAllocate();
            }
        }

        for (AstDecList it = this; it != null; it = it.tail) {
            if (it.head instanceof AstVarDec) {
                ((AstVarDec) it.head).irInit();
            }
        }

        for (AstDecList it = this; it != null; it = it.tail) {
            if (it.head instanceof AstFuncDec) {
                AstFuncDec f = (AstFuncDec) it.head;
                if ("main".equals(f.name)) {
                    f.IRme();
                    break;
                }
            }
        }

        return null;
    }
}
