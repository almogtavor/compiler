package ast;

import Exceptions.SemanticException;
import symboltable.SymbolTable;
import types.*;

public class AstCallExp extends AstExp {
    public AstVar object;
    public String name;
    public AstExpList args;

    public AstCallExp(AstVar object, String name, AstExpList args, int lineNumber) {
        super(lineNumber);
        this.object = object;
        this.name = name;
        this.args = args;
    }

    @Override
    public void printMe() {
        if (args != null) args.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, String.format("CALL(%s)", name));
        if (args != null) AstGraphviz.getInstance().logEdge(serialNumber, args.serialNumber);
    }

    @Override
    public Type SemantMe() {
        System.out.println("SemantMe: " + this.getClass().getSimpleName());

        TypeFunction func;
        TypeList expectedParams;
        TypeList givenParams = (args != null ? args.SemantMe() : null);

        if (object == null) {
            Type t = SymbolTable.getInstance().find(name);
            if (!(t instanceof TypeFunction))
                throw new SemanticException(lineNumber);

            func = (TypeFunction) t;
            expectedParams = func.params;
        } else {
            Type base = object.SemantMe();
            if (!(base instanceof TypeClass))
                throw new SemanticException(lineNumber);

            TypeClass curr = (TypeClass) base;
            func = null;

            while (curr != null) {
                Type found = (curr.dataMembers != null ? curr.dataMembers.find(name) : null);
                if (found instanceof TypeFunction) {
                    func = (TypeFunction) found;
                    break;
                }
                curr = curr.father;
            }

            if (func == null)
                throw new SemanticException(lineNumber);

            expectedParams = func.params;
        }

        TypeList e = expectedParams;
        TypeList g = givenParams;

        while (e != null && g != null) {
            if (!e.head.isAssignableFrom(g.head))
                throw new SemanticException(lineNumber);

            e = e.tail;
            g = g.tail;
        }

        if (e != null || g != null)
            throw new SemanticException(lineNumber);

        return func.returnType;
    }

}
