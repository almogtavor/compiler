package ast;
import Exceptions.SemanticException;
import symboltable.SymbolTable;
import types.*;

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
        System.out.println("SemantMe: " + this.getClass().getSimpleName());

        Type retType = SymbolTable.getInstance().find("__RET_TYPE__");
        if (retType == null)
            throw new SemanticException(lineNumber); // return מחוץ לפונקציה

        Type expType = null;
        if (exp != null)
            expType = exp.SemantMe();

        // 1. בדיקה לפונקציה מחזירה void
        if (retType == TypeVoid.getInstance()) {
            if (exp != null)
                throw new SemanticException(lineNumber); // אסור return עם ערך
            return;
        }

        // 2. פונקציה מחזירה טיפוס לא-void
        if (exp == null)
            return; // מותר path בלי return

        // 3. nil rules
        if (expType == TypeNil.getInstance()) {
            if (!(retType instanceof TypeClass) && !(retType instanceof TypeArray))
                throw new SemanticException(lineNumber);
            return;
        }

        // 4. בדיקת assignability רגילה
        if (!retType.isAssignableFrom(expType))
            throw new SemanticException(lineNumber);
    }

}
