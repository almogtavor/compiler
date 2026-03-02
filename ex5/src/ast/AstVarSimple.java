package ast;

import Exceptions.SemanticException;
import symboltable.SymbolTable;
import types.*;
import temp.*;
import ir.*;
import codegen.*;

public class AstVarSimple extends AstVar {
    public String name;
    public String irName;
    private String resolvedClassName = null;

    public AstVarSimple(String name, int lineNumber) {
        super(lineNumber);
        this.name = name;
    }

    @Override
    public void printMe() {
        AstGraphviz.getInstance().logNode(serialNumber, String.format("VAR_SIMPLE(%s)", name));
    }

    @Override
    public Type SemantMe() {
        Type t = SymbolTable.getInstance().find(name);
        if (t == null) throw new SemanticException(lineNumber);
        this.irName = SymbolTable.getInstance().getIrName(name);
        if (this.irName == null) {
            TypeClass currentClass = SymbolTable.getInstance().getCurrentClass();
            if (currentClass != null) {
                resolvedClassName = currentClass.name;
            }
        }
        return t;
    }

    @Override
    public Temp IRme() {
        if (irName != null) {
            Temp t = TempFactory.getInstance().getFreshTemp();
            Ir.getInstance().AddIrCommand(new IrCommandLoad(t, this.irName));
            return t;
        }
        if (resolvedClassName != null) {
            String thisParam = CodeGenInfo.getInstance().getCurrentThisParam();
            if (thisParam != null) {
                Temp thisTemp = TempFactory.getInstance().getFreshTemp();
                Ir.getInstance().AddIrCommand(new IrCommandLoad(thisTemp, thisParam));
                int offset = findFieldOffset(resolvedClassName, name);
                Temp dst = TempFactory.getInstance().getFreshTemp();
                Ir.getInstance().AddIrCommand(new IrCommandFieldGet(dst, thisTemp, offset));
                return dst;
            }
        }
        Temp t = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandConstInt(t, 0));
        return t;
    }

    public Temp IRmeForAssign(Temp src) {
        if (irName != null) {
            Ir.getInstance().AddIrCommand(new IrCommandStore(this.irName, src));
            return null;
        }
        if (resolvedClassName != null) {
            String thisParam = CodeGenInfo.getInstance().getCurrentThisParam();
            if (thisParam != null) {
                Temp thisTemp = TempFactory.getInstance().getFreshTemp();
                Ir.getInstance().AddIrCommand(new IrCommandLoad(thisTemp, thisParam));
                int offset = findFieldOffset(resolvedClassName, name);
                Ir.getInstance().AddIrCommand(new IrCommandFieldSet(thisTemp, offset, src));
            }
        }
        return null;
    }

    private int findFieldOffset(String className, String fieldName) {
        ClassLayoutManager clm = ClassLayoutManager.getInstance();
        int offset = clm.getFieldOffset(className, fieldName);
        if (offset >= 0) return offset;
        ClassLayoutManager.ClassInfo info = clm.getClassInfo(className);
        if (info != null && info.parent != null) {
            return findFieldOffset(info.parent.className, fieldName);
        }
        return 1;
    }
}
