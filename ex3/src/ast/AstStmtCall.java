package ast;

import Exceptions.SemanticException;
import types.Type;

public class AstStmtCall extends AstStmt {

    public AstCallExp call;

    public AstStmtCall(AstCallExp call, int lineNumber) {
        super(lineNumber);
        this.call = call;
    }

    @Override
    public void printMe() {
        if (call != null)
            call.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "STMT_CALL");

        if (call != null)
            AstGraphviz.getInstance().logEdge(serialNumber, call.serialNumber);
    }

    @Override
    public void SemantMe() {
        if (call == null)
            throw new SemanticException(lineNumber);

        Type t = call.SemantMe();

        if (t == null)
            throw new SemanticException(lineNumber);
    }
}
