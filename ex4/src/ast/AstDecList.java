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
                AstVarDec v = (AstVarDec) it.head;
                types.Type t = v.type.SemantMe();
                v.irName = symboltable.SymbolTable.getInstance().enterVar(v.name, t);
            }
        }
        
        for (AstDecList it = this; it != null; it = it.tail) {
            if (it.head instanceof AstVarDec) {
                AstVarDec v = (AstVarDec) it.head;
                if (v.init != null) {
                    types.Type t = v.type.SemantMe();
                    types.Type initType = v.init.SemantMe();
                    if (!t.isAssignableFrom(initType))
                        throw new Exceptions.SemanticException(v.lineNumber);
                }
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
