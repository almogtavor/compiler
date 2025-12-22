package ast;

import Exceptions.SemanticException;
import symboltable.SymbolTable;
import types.*;
import temp.*;
import ir.*;

public class AstFuncDec extends AstDec {

    public AstType retType;
    public String name;
    public AstArgList args;
    public AstStmtList body;

    public AstFuncDec(AstType retType, String name, AstArgList args, AstStmtList body, int lineNumber) {
        super(lineNumber);
        this.retType = retType;
        this.name = name;
        this.args = args;
        this.body = body;
    }

    @Override
    public void printMe() {

        if (retType != null) retType.printMe();
        if (args != null) args.printMe();
        if (body != null) body.printMe();

        AstGraphviz.getInstance().logNode(serialNumber, "FUNC(" + name + ")");

        if (retType != null)
            AstGraphviz.getInstance().logEdge(serialNumber, retType.serialNumber);
        if (args != null)
            AstGraphviz.getInstance().logEdge(serialNumber, args.serialNumber);
        if (body != null)
            AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
    }

    @Override
    public Type SemantMe() {
        if (SymbolTable.isReservedKeyword(name))
            throw new SemanticException(lineNumber);

        Type returnType = retType.SemantMe();

        TypeList paramsList = null;
        if (args != null)
            paramsList = args.buildTypeList();

        if (SymbolTable.getInstance().findInCurrentScope(name) != null)
            throw new SemanticException(lineNumber);

        TypeFunction funcType = new TypeFunction(returnType, name, paramsList);
        SymbolTable.getInstance().enter(name, funcType);

        SymbolTable.getInstance().beginScope();

        SymbolTable.getInstance().enter("__RET_TYPE__", returnType);

        if (args != null)
            args.SemantMe();

        if (body != null)
            body.SemantMe();

        SymbolTable.getInstance().endScope();

        return funcType;
    }

    @Override
    public Temp IRme() {
        Ir.getInstance().AddIrCommand(new IrCommandLabel(name));
        if (args != null) args.IRme();
        if (body != null) body.IRme();
        return null;
    }
}
