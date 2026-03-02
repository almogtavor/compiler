package ast;

import types.*;
import Exceptions.SemanticException;
import temp.*;
import ir.*;

public class AstStmtAssign extends AstStmt {
    public AstVar var;
    public AstExp exp;

    public AstStmtAssign(AstVar var, AstExp exp, int lineNumber) {
        super(lineNumber);
        this.var = var;
        this.exp = exp;
    }

    @Override
    public void printMe() {
        if (var != null) var.printMe();
        if (exp != null) exp.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "ASSIGN");
        if (var != null) AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
        if (exp != null) AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }

    @Override
    public void SemantMe() {
        Type leftType = var.SemantMe();
        Type rightType = exp.SemantMe();
        if (!leftType.isAssignableFrom(rightType))
            throw new SemanticException(lineNumber);
    }

    @Override
    public Temp IRme() {
        if (var instanceof AstVarSimple) {
            AstVarSimple sv = (AstVarSimple) var;
            Temp src = exp.IRme();
            sv.IRmeForAssign(src);
        } else if (var instanceof AstVarField) {
            AstVarField fv = (AstVarField) var;
            Temp objTemp = fv.var.IRme();
            Ir.getInstance().AddIrCommand(new IrCommandCheckNullPtr(objTemp));
            Temp src = exp.IRme();
            Ir.getInstance().AddIrCommand(new IrCommandFieldSet(objTemp, fv.getFieldOffsetForIR(), src));
        } else if (var instanceof AstVarSubscript) {
            AstVarSubscript sv = (AstVarSubscript) var;
            Temp arrTemp = sv.var.IRme();
            Ir.getInstance().AddIrCommand(new IrCommandCheckNullPtr(arrTemp));
            Temp idxTemp = sv.subscript.IRme();
            Ir.getInstance().AddIrCommand(new IrCommandCheckBounds(arrTemp, idxTemp));
            Temp src = exp.IRme();
            Ir.getInstance().AddIrCommand(new IrCommandArraySet(arrTemp, idxTemp, src));
        }
        return null;
    }
}
