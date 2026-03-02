package ast;

import types.*;
import symboltable.SymbolTable;
import Exceptions.SemanticException;
import temp.*;
import ir.*;
import codegen.*;

public class AstNew extends AstExp {
    public AstType type;
    public AstExp size;
    private String className = null;

    public AstNew(AstType type, AstExp size, int lineNumber) {
        super(lineNumber);
        this.type = type;
        this.size = size;
    }

    @Override
    public void printMe() {
        if (type != null) type.printMe();
        if (size != null) size.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, (size == null) ? "NEW" : "NEW_ARRAY");
        if (type != null) AstGraphviz.getInstance().logEdge(serialNumber, type.serialNumber);
        if (size != null) AstGraphviz.getInstance().logEdge(serialNumber, size.serialNumber);
    }

    @Override
    public Type SemantMe() {
        Type t = type.SemantMe();
        if (t == TypeVoid.getInstance()) throw new SemanticException(lineNumber);

        if (size == null) {
            if (!(t instanceof TypeClass)) throw new SemanticException(lineNumber);
            className = t.name;
            return t;
        }

        Type sizeType = size.SemantMe();
        if (sizeType != TypeInt.getInstance()) throw new SemanticException(lineNumber);
        if (size instanceof AstExpInt && ((AstExpInt) size).value < 0)
            throw new SemanticException(lineNumber);
        return new TypeArray(t.name, t);
    }

    @Override
    public Temp IRme() {
        if (size == null) {
            ClassLayoutManager.ClassInfo ci = ClassLayoutManager.getInstance().getClassInfo(className);
            int objSize = ci.objectSize;

            Temp sizeTemp = TempFactory.getInstance().getFreshTemp();
            Ir.getInstance().AddIrCommand(new IrCommandConstInt(sizeTemp, objSize));

            Temp objTemp = TempFactory.getInstance().getFreshTemp();
            Ir.getInstance().AddIrCommand(new IrCommandMalloc(objTemp, sizeTemp));

            Temp vtableAddr = TempFactory.getInstance().getFreshTemp();
            Ir.getInstance().AddIrCommand(new IrCommandConstString(vtableAddr, "vtable_" + className));
            Ir.getInstance().AddIrCommand(new IrCommandFieldSet(objTemp, 0, vtableAddr));

            initDataMembers(objTemp, ci, className);

            return objTemp;
        } else {
            Temp sizeVal = size.IRme();
            Temp one = TempFactory.getInstance().getFreshTemp();
            Ir.getInstance().AddIrCommand(new IrCommandConstInt(one, 1));
            Temp totalElements = TempFactory.getInstance().getFreshTemp();
            Ir.getInstance().AddIrCommand(new IrCommandBinopAddIntegers(totalElements, sizeVal, one));
            Temp four = TempFactory.getInstance().getFreshTemp();
            Ir.getInstance().AddIrCommand(new IrCommandConstInt(four, 4));
            Temp totalBytes = TempFactory.getInstance().getFreshTemp();
            Ir.getInstance().AddIrCommand(new IrCommandBinopMulIntegers(totalBytes, totalElements, four));

            Temp arrTemp = TempFactory.getInstance().getFreshTemp();
            Ir.getInstance().AddIrCommand(new IrCommandMalloc(arrTemp, totalBytes));
            Ir.getInstance().AddIrCommand(new IrCommandFieldSet(arrTemp, 0, sizeVal));

            return arrTemp;
        }
    }

    private void initDataMembers(Temp objTemp, ClassLayoutManager.ClassInfo ci, String clsName) {
        java.util.Map<String, Object[]> defaults = ClassLayoutManager.getInstance().getClassInfo(clsName) != null ?
            getDefaultValues(clsName) : new java.util.HashMap<>();

        for (java.util.Map.Entry<String, Integer> e : ci.fieldOffsets.entrySet()) {
            String fieldName = e.getKey();
            int offset = e.getValue();
            Object[] defVal = defaults.get(fieldName);
            if (defVal != null) {
                if (defVal[0].equals("int")) {
                    Temp val = TempFactory.getInstance().getFreshTemp();
                    Ir.getInstance().AddIrCommand(new IrCommandConstInt(val, (Integer) defVal[1]));
                    Ir.getInstance().AddIrCommand(new IrCommandFieldSet(objTemp, offset, val));
                } else if (defVal[0].equals("string")) {
                    String label = CodeGenInfo.getInstance().addStringLiteral((String) defVal[1]);
                    Temp val = TempFactory.getInstance().getFreshTemp();
                    Ir.getInstance().AddIrCommand(new IrCommandConstString(val, label));
                    Ir.getInstance().AddIrCommand(new IrCommandFieldSet(objTemp, offset, val));
                } else {
                    Temp val = TempFactory.getInstance().getFreshTemp();
                    Ir.getInstance().AddIrCommand(new IrCommandConstInt(val, 0));
                    Ir.getInstance().AddIrCommand(new IrCommandFieldSet(objTemp, offset, val));
                }
            } else {
                Temp val = TempFactory.getInstance().getFreshTemp();
                Ir.getInstance().AddIrCommand(new IrCommandConstInt(val, 0));
                Ir.getInstance().AddIrCommand(new IrCommandFieldSet(objTemp, offset, val));
            }
        }
    }

    private java.util.Map<String, Object[]> getDefaultValues(String clsName) {
        return ClassLayoutManager.getInstance().getFieldDefaults(clsName);
    }
}
