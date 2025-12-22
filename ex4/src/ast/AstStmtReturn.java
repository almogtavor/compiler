package ast;
import Exceptions.SemanticException;
import symboltable.SymbolTable;
import types.*;
import temp.*;

public class AstStmtReturn extends AstStmt {

    public AstExp exp;

    public AstStmtReturn(AstExp exp, int lineNumber) {
        super(lineNumber);
        this.exp = exp;
    }

    @Override
    public void printMe() {
        if (exp != null) exp.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "RETURN");

        if (exp != null)
            AstGraphviz.getInstance().logEdge(serialNumber, exp.serialNumber);
    }

    @Override
    public void SemantMe() {
        Type retType = SymbolTable.getInstance().find("__RET_TYPE__");
        if (retType == null)
            throw new SemanticException(lineNumber);

        Type expType = null;
        if (exp != null)
            expType = exp.SemantMe();

        if (retType == TypeVoid.getInstance()) {
            if (exp != null)
                throw new SemanticException(lineNumber);
            return;
        }

        if (exp == null)
            return;

        if (expType == TypeNil.getInstance()) {
            if (!(retType instanceof TypeClass) && !(retType instanceof TypeArray))
                throw new SemanticException(lineNumber);
            return;
        }

        if (!retType.isAssignableFrom(expType))
            throw new SemanticException(lineNumber);
    }

    @Override
    public Temp IRme() {
        if (exp != null) exp.IRme();
        return null;
    }
}
