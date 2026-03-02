package ast;

import temp.*;
import ir.*;
import codegen.CodeGenInfo;

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
            it.head.SemantMe();
        }
    }

    @Override
    public Temp IRme() {
        CodeGenInfo info = CodeGenInfo.getInstance();

        for (AstDecList it = this; it != null; it = it.tail) {
            if (it.head instanceof AstVarDec) {
                AstVarDec vd = (AstVarDec) it.head;
                Ir.getInstance().AddIrCommand(new IrCommandAllocate(vd.irName));
                info.registerGlobalVar(vd.irName);
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
                f.funcLabel = "func_" + f.name;
                f.IRme();
            }
        }

        for (AstDecList it = this; it != null; it = it.tail) {
            if (it.head instanceof AstClassDec) {
                it.head.IRme();
            }
        }

        return null;
    }
}
