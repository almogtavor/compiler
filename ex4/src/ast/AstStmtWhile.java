package ast;
import symboltable.SymbolTable;
import types.TypeForScopeBoundaries;

import Exceptions.SemanticException;
import types.*;
import temp.*;
import ir.*;

public class AstStmtWhile extends AstStmt {

    public AstExp cond;
    public AstStmtList body;

    public AstStmtWhile(AstExp cond, AstStmtList body, int lineNumber) {
        super(lineNumber);
        this.cond = cond;
        this.body = body;
    }

    @Override
    public void printMe() {
        if (cond != null) cond.printMe();
        if (body != null) body.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "WHILE");

        if (cond != null)
            AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);

        if (body != null)
            AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
    }

    @Override
    public void SemantMe() {
        Type tCond = cond.SemantMe();
        if (tCond != TypeInt.getInstance())
            throw new SemanticException(lineNumber);

        SymbolTable.getInstance().beginScope();

        if (body != null)
            body.SemantMe();

        SymbolTable.getInstance().endScope();
    }

    @Override
    public Temp IRme() {
        String labelStart = IrCommand.getFreshLabel("while_start");
        String labelEnd = IrCommand.getFreshLabel("while_end");
        
        Ir.getInstance().AddIrCommand(new IrCommandLabel(labelStart));
        
        Temp condTemp = cond.IRme();
        Ir.getInstance().AddIrCommand(new IrCommandJumpIfEqToZero(condTemp, labelEnd));
        
        if (body != null) body.IRme();
        
        Ir.getInstance().AddIrCommand(new IrCommandJumpLabel(labelStart));
        Ir.getInstance().AddIrCommand(new IrCommandLabel(labelEnd));
        
        return null;
    }
}
