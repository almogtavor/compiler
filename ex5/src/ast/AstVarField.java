package ast;

import Exceptions.SemanticException;
import symboltable.SymbolTable;
import types.*;
import temp.*;
import ir.*;
import codegen.*;

public class AstVarField extends AstVar {
    public AstVar var;
    public String fieldName;
    private String resolvedClassName = null;

    public AstVarField(AstVar var, String fieldName, int lineNumber) {
        super(lineNumber);
        this.var = var;
        this.fieldName = fieldName;
    }

    @Override
    public void printMe() {
        if (var != null) var.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, String.format("FIELD(%s)", fieldName));
        if (var != null) AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
    }

    @Override
    public Type SemantMe() {
        Type baseType = var.SemantMe();
        if (!(baseType instanceof TypeClass)) throw new SemanticException(lineNumber);
        resolvedClassName = baseType.name;
        TypeClass curr = (TypeClass) baseType;
        while (curr != null) {
            if (curr.dataMembers != null) {
                Type found = curr.dataMembers.find(fieldName);
                if (found != null) return found;
            }
            curr = curr.father;
        }
        throw new SemanticException(lineNumber);
    }

    @Override
    public Temp IRme() {
        Temp objTemp = var.IRme();
        Ir.getInstance().AddIrCommand(new IrCommandCheckNullPtr(objTemp));
        int offset = findFieldOffset(resolvedClassName, fieldName);
        Temp dst = TempFactory.getInstance().getFreshTemp();
        Ir.getInstance().AddIrCommand(new IrCommandFieldGet(dst, objTemp, offset));
        return dst;
    }

    public Temp IRmeForAssign(Temp src) {
        Temp objTemp = var.IRme();
        Ir.getInstance().AddIrCommand(new IrCommandCheckNullPtr(objTemp));
        int offset = findFieldOffset(resolvedClassName, fieldName);
        Ir.getInstance().AddIrCommand(new IrCommandFieldSet(objTemp, offset, src));
        return null;
    }

    public int getFieldOffsetForIR() {
        return findFieldOffset(resolvedClassName, fieldName);
    }

    private int findFieldOffset(String className, String field) {
        ClassLayoutManager clm = ClassLayoutManager.getInstance();
        int offset = clm.getFieldOffset(className, field);
        if (offset >= 0) return offset;
        ClassLayoutManager.ClassInfo info = clm.getClassInfo(className);
        if (info != null && info.parent != null)
            return findFieldOffset(info.parent.className, field);
        return 1;
    }
}
