package ast;
import symboltable.SymbolTable;
import types.TypeForScopeBoundaries;

import types.*;
import Exceptions.SemanticException;
import temp.*;
import ir.*;

public class AstStmtIfElse extends AstStmt {

    public AstExp cond;
    public AstStmtList thenBody;
    public AstStmtList elseBody;

    public AstStmtIfElse(AstExp cond, AstStmtList tb, AstStmtList eb,int lineNumber) {
        super(lineNumber);
        this.cond = cond;
        this.thenBody = tb;
        this.elseBody = eb;
    }

    @Override
    public void printMe() {
        if (cond != null) cond.printMe();
        if (thenBody != null) thenBody.printMe();
        if (elseBody != null) elseBody.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "IF_ELSE");

        if (cond != null)
            AstGraphviz.getInstance().logEdge(serialNumber, cond.serialNumber);
        if (thenBody != null)
            AstGraphviz.getInstance().logEdge(serialNumber, thenBody.serialNumber);
        if (elseBody != null)
            AstGraphviz.getInstance().logEdge(serialNumber, elseBody.serialNumber);
    }

    @Override
    public void SemantMe() {
        Type condType = cond.SemantMe();

        if (condType != TypeInt.getInstance())
            throw new SemanticException(lineNumber);

        SymbolTable.getInstance().beginScope();
        if (thenBody != null)
            thenBody.SemantMe();
        SymbolTable.getInstance().endScope();

        SymbolTable.getInstance().beginScope();
        if (elseBody != null)
            elseBody.SemantMe();
        SymbolTable.getInstance().endScope();
    }

    @Override
    public Temp IRme() {
        String labelElse = IrCommand.getFreshLabel("else");
        String labelEnd = IrCommand.getFreshLabel("if_end");
        
        Temp condTemp = cond.IRme();
        Ir.getInstance().AddIrCommand(new IrCommandJumpIfEqToZero(condTemp, labelElse));
        
        if (thenBody != null) thenBody.IRme();
        Ir.getInstance().AddIrCommand(new IrCommandJumpLabel(labelEnd));
        
        Ir.getInstance().AddIrCommand(new IrCommandLabel(labelElse));
        if (elseBody != null) elseBody.IRme();
        
        Ir.getInstance().AddIrCommand(new IrCommandLabel(labelEnd));
        
        return null;
    }
}
