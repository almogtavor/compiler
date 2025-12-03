package ast;

import Exceptions.SemanticException;
import symboltable.SymbolTable;
import types.*;

public class AstVarField extends AstVar {

    public AstVar var;
    public String fieldName;

    public AstVarField(AstVar var, String fieldName, int lineNumber) {
        super(lineNumber);
        this.var = var;
        this.fieldName = fieldName;
    }
    
    @Override
    public void printMe() {
        if (var != null) var.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, String.format("FIELD(%s)", fieldName));

        if (var != null)
            AstGraphviz.getInstance().logEdge(serialNumber, var.serialNumber);
    }

    @Override
    public Type SemantMe() {
System.out.println("SemantMe: " + this.getClass().getSimpleName());

        Type baseType = var.SemantMe();

        if (!(baseType instanceof TypeClass))
            throw new SemanticException(lineNumber);

        TypeClass curr = (TypeClass) baseType;

        while (curr != null) {
            if (curr.dataMembers != null) {
                Type found = curr.dataMembers.find(fieldName);
                if (found != null)
                    return found;
            }
            curr = curr.father;
        }

        throw new SemanticException(lineNumber);
    }

}
